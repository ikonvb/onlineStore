package com.konstantinbulygin.onlinestore.controllers;

import com.konstantinbulygin.onlinestore.model.PageRepository;
import com.konstantinbulygin.onlinestore.model.data.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PagesController {

    private final PageRepository pageRepository;

    public PagesController(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @GetMapping
    public String home(Model model) {

        Page page = pageRepository.findBySlug("home");
        model.addAttribute("page", page);
        return "page";
    }

    @GetMapping("/{slug}")
    public String page(@PathVariable String slug, Model model) {

        Page page = pageRepository.findBySlug(slug);
        if (page == null) {
            return "redirect:/";
        }
        model.addAttribute("page", page);
        return "page";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
