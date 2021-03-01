package com.konstantinbulygin.onlinestore.config;

import com.konstantinbulygin.onlinestore.model.PageRepository;
import com.konstantinbulygin.onlinestore.model.data.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class Util {

    private final PageRepository pageRepository;

    public Util(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @ModelAttribute
    public void sharedData(Model model) {

        List<Page> pages = pageRepository.findAll();
        model.addAttribute("commonPages", pages);

    }
}
