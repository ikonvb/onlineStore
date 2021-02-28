package com.konstantinbulygin.onlinestore.controllers;

import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.ProductRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Page;
import com.konstantinbulygin.onlinestore.model.data.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
        List<Category> categories = categoryRepository.findAll();

        HashMap<Integer, String> categoriesMap = new HashMap<>();
        for (Category category : categories) {
            categoriesMap.put(category.getId(), category.getName());
        }
        model.addAttribute("categoriesMap", categoriesMap);
        model.addAttribute("products", products);
        return "admin/products/index";
    }

    @GetMapping("/add")
    public String add(Product product, Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "admin/products/add";
    }

    @PostMapping("/add")
    public String add(@Valid Product product, BindingResult bindingResult, MultipartFile file, RedirectAttributes redirectAttributes, Model model) {

        List<Category> categories = categoryRepository.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "admin/products/add";
        }

        boolean isFileOk = false;
        byte[] fileBytes = null;
        String fileName = null;
        Path path = null;
        try {
            fileBytes = file.getBytes();
            fileName = file.getOriginalFilename();
            path = Paths.get("src\\main\\resources\\static\\media\\" + fileName);

            assert fileName != null;

            if (fileName.endsWith("jpg") || fileName.endsWith("png")) {
                isFileOk = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("message", "Product added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");

        Product productExists = productRepository.findBySlug(slug);

        if (!isFileOk) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png format");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else if (productExists != null) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {
            product.setSlug(slug);
            product.setImage(fileName);
            productRepository.save(product);

            try {
                Files.write(path, fileBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/admin/products/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {

        Product product = productRepository.getOne(id);
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);

        return "admin/products/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Product product, BindingResult bindingResult, MultipartFile file, RedirectAttributes redirectAttributes, Model model) {

        Product currentProduct = productRepository.getOne(product.getId());
        List<Category> categories = categoryRepository.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            model.addAttribute("productName", currentProduct.getName());
            return "admin/products/add";
        }

        boolean isFileOk = false;
        byte[] fileBytes = null;
        String fileName = null;
        Path path = null;


        try {
            fileBytes = file.getBytes();
            fileName = file.getOriginalFilename();
            path = Paths.get("src\\main\\resources\\static\\media\\" + fileName);

            assert fileName != null;
            if (!file.isEmpty()) {
                if (fileName.endsWith("jpg") || fileName.endsWith("png")) {
                    isFileOk = true;
                }
            } else {
                isFileOk = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("message", "Product edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");

        Product productExists = productRepository.findBySlugAndIdNot(slug, product.getId());

        if (!isFileOk) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png format");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else if (productExists != null) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {
            product.setSlug(slug);

            if (!file.isEmpty()) {
                try {
                    Path path1 = Paths.get("src\\main\\resources\\static\\media\\" + currentProduct.getImage());
                    Files.delete(path1);
                    product.setImage(fileName);
                    Files.write(path, fileBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setImage(currentProduct.getImage());
            }
            productRepository.save(product);
        }
        return "redirect:/admin/products/edit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {

        try {
            Product product = productRepository.getOne(id);
            Product currentProduct = productRepository.getOne(product.getId());
            Path path1 = Paths.get("src\\main\\resources\\static\\media\\" + currentProduct.getImage());
            Files.delete(path1);
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }

}
