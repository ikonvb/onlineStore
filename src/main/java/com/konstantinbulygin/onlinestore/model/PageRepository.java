package com.konstantinbulygin.onlinestore.model;

import com.konstantinbulygin.onlinestore.model.data.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Integer> {

    Page findBySlug(String slug);

    Page findBySlugAndIdNot(String slug, int id);
}
