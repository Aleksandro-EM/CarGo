package com.project.CarGo.repository;

import com.project.CarGo.entity.Category;
import com.project.CarGo.entity.CategorySubtype;
import com.project.CarGo.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByTypeAndSubtype(CategoryType type, CategorySubtype subtype);
    boolean existsByImageUrlAndIdNot(String imageUrl, Long id);
}
