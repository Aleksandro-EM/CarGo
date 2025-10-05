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

    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/category/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("types", CategoryType.values());
        model.addAttribute("subtypes", CategorySubtype.values());
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-form";
    }

    @PostMapping("/category/add")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult bindingResult, Model model,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {

        if(categoryRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype())) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/add";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            return "category-form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = s3Service.uploadFile(imageFile);
                category.setImageUrl(imageUrl);
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
        model.addAttribute("imageUrl", category.getImageUrl());
        return "category-form";
    }

    @PostMapping("/category/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult, Model model,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));

        if(categoryRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype()) && (!Objects.equals(existingCategory.getId(), category.getId()))) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/edit/" + id;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            return "category-form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = s3Service.uploadFile(imageFile);
                category.setImageUrl(imageUrl);
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image!");
        }

        category.setId(id);
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
        return "redirect:/admin/category/edit/" + id;
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {

        categoryRepository.deleteById(id);
        ra.addFlashAttribute("success", "Category deleted successfully!");
        return "redirect:/admin/categories";
    }
}
