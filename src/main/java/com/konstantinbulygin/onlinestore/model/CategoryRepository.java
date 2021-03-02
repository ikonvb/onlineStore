package com.konstantinbulygin.onlinestore.model;

import com.konstantinbulygin.onlinestore.model.data.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByName(String name);

    Category findBySlug(String slug);
}
