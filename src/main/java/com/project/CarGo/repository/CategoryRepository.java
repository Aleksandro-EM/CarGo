package com.project.CarGo.repository;

import com.project.CarGo.entity.Category;
import com.project.CarGo.entity.CategorySubtype;
import com.project.CarGo.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByTypeAndSubtype(CategoryType type, CategorySubtype subtype);
    boolean existsByImageUrlAndIdNot(String imageUrl, Long id);

    @Query("SELECT DISTINCT v.category FROM Vehicle v")
    List<Category> findCategoriesWithVehicles();
}
