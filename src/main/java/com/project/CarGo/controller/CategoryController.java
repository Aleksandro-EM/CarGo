package com.project.CarGo.controller;

import com.project.CarGo.entity.Category;
import com.project.CarGo.entity.CategorySubtype;
import com.project.CarGo.entity.CategoryType;
import com.project.CarGo.repository.CategoryRepository;
import com.project.CarGo.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private S3Service s3Service;

    private static final String DEFAULT_IMAGE_URL = "https://cargo-file-storage.s3.amazonaws.com/default_image.png";

    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        List<String> imageUrlsS3 = s3Service.listFiles();
        model.addAttribute("imageUrlsS3", imageUrlsS3);

        return "categories";
    }

    @GetMapping("/category/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("types", CategoryType.values());
        model.addAttribute("subtypes", CategorySubtype.values());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
        return "category-form";
    }

    @PostMapping("/category/add")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult, Model model,
                               @RequestParam(value="imageFile", required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {

        if(categoryRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype())) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/add";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            model.addAttribute("DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
            return "category-form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = s3Service.uploadFile(imageFile);
                category.setImageUrl(imageUrl);
            } else if (category.getImageUrl() == null || category.getImageUrl().isEmpty()) {
                category.setImageUrl(DEFAULT_IMAGE_URL);
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image!");
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Category was added successfully!");
        return "redirect:/admin/category/add";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));

        model.addAttribute("category", category);
        model.addAttribute("types", CategoryType.values());
        model.addAttribute("subtypes", CategorySubtype.values());

        List<String> imageUrlsS3 = s3Service.listFiles();
        model.addAttribute("imageUrlsS3", imageUrlsS3);
        model.addAttribute("DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
        return "category-form";
    }

    @PostMapping("/category/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult, Model model,
                                 @RequestParam(value="imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));

        if(categoryRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype())
                && !Objects.equals(existingCategory.getId(), id)) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/edit/" + id;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            model.addAttribute("DEFAULT_IMAGE_URL", DEFAULT_IMAGE_URL);
            return "category-form";
        }

        existingCategory.setId(id);
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                if(existingCategory.getImageUrl() != null && !existingCategory.getImageUrl().equals(DEFAULT_IMAGE_URL)) {
                    String oldFileName = getFileName(existingCategory.getImageUrl());
                    s3Service.deleteFile(oldFileName);
                }
                String newImageUrl = s3Service.uploadFile(imageFile);
                existingCategory.setImageUrl(newImageUrl);
            } else if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                existingCategory.setImageUrl(category.getImageUrl());
            } else {
                existingCategory.setImageUrl(DEFAULT_IMAGE_URL);
            }

            existingCategory.setType(category.getType());
            existingCategory.setSubtype(category.getSubtype());

            categoryRepository.save(existingCategory);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image!");
        }

        return "redirect:/admin/category/edit/" + id;
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));

        if(!category.getImageUrl().isEmpty() && !category.getImageUrl().equals(DEFAULT_IMAGE_URL)) {
            boolean usedByOtherCategories = categoryRepository.existsByImageUrlAndIdNot(category.getImageUrl(), id);

            if (!usedByOtherCategories) {
                String fileName = getFileName(category.getImageUrl());
                s3Service.deleteFile(fileName);
            }
        }

        categoryRepository.deleteById(id);
        ra.addFlashAttribute("success", "Category deleted successfully!");
        return "redirect:/admin/categories";
    }

    private String getFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }
}