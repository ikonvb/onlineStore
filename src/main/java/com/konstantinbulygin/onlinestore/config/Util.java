package com.konstantinbulygin.onlinestore.config;

import com.konstantinbulygin.onlinestore.model.Cart;
import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.PageRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
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
    public void sharedData(Model model, HttpSession session) {

        List<Page> pages = pageRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        boolean isCartActive = false;

        if (session.getAttribute("cart") != null) {
            HashMap<Integer, Cart> cart = (HashMap<Integer, Cart>) session.getAttribute("cart");
            int size = 0;
            double total = 0;

            for (Cart value: cart.values()) {
                size += value.getQuantity();
                total += value.getQuantity() * Double.parseDouble(value.getPrice());
            }

            model.addAttribute("csize", size);
            model.addAttribute("total", total);

            isCartActive = true;
        }

        model.addAttribute("commonPages", pages);
        model.addAttribute("commonCategories", categories);
        model.addAttribute("isCartActive", isCartActive);

    }
}
