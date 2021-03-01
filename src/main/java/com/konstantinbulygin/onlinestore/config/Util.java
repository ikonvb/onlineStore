package com.konstantinbulygin.onlinestore.config;

import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.PageRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class Util {

    private final PageRepository pageRepository;
    private final CategoryRepository categoryRepository;

    public Util(PageRepository pageRepository, CategoryRepository categoryRepository) {
        this.pageRepository = pageRepository;
        this.categoryRepository = categoryRepository;
    }

    @ModelAttribute
    public void sharedData(Model model) {

        List<Page> pages = pageRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("commonPages", pages);
        model.addAttribute("commonCategories", categories);

    }
}
