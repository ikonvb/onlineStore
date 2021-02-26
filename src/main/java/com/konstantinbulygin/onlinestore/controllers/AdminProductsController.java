package com.konstantinbulygin.onlinestore.controllers;

import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.ProductRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductsController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin/products/index";
    }

    @GetMapping("/add")
    public String add(Product product, Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "admin/products/add";
    }


}
