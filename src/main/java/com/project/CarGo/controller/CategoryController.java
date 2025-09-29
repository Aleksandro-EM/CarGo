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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/admin/category/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("types", CategoryType.values());
        model.addAttribute("subtypes", CategorySubtype.values());
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-add";
    }

    @PostMapping("/admin/category/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult bindingResult, Model model,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", CategoryType.values());
            model.addAttribute("subtypes", CategorySubtype.values());
            return "category-add";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Category was added successfully!");
        return "redirect:/admin/category/add";
    }
}
