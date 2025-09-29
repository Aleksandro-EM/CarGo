package com.project.CarGo.controller;

import com.project.CarGo.entity.Category;
import com.project.CarGo.entity.CategorySubtype;
import com.project.CarGo.entity.CategoryType;
import com.project.CarGo.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

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
        return "category-form";
    }

    @PostMapping("/category/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {

        if(categoryRepository.existsByTypeAndSubtype(category.getType(), category.getSubtype())) {
            redirectAttributes.addFlashAttribute("error", "This type + subtype already exists.");
            return "redirect:/admin/category/edit/" + id;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            return "category-form";
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
