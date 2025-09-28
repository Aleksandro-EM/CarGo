package com.project.CarGo.controller;

import com.project.CarGo.entity.Category;
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
        if(!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }

        model.addAttribute("categories", CategoryType.values());
        return "category-add";
    }

    @PostMapping("/admin/category/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        // Validate subtype matches enum
        if (category.getType() != null && !category.getType().isSubtype(category.getSubtype())) {
            bindingResult.rejectValue("subtype", "error.subtype",
                    "Invalid subtype for selected type.");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/categories/add";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/categories/add";
    }
}
