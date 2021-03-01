package com.konstantinbulygin.onlinestore.controllers;

import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.ProductRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String index(Model model, @RequestParam(value = "page", required = false) Integer p) {

        int perPage = 6;
        int page = (p != null) ? p : 0;

        Pageable pageable = PageRequest.of(page, perPage);
        Page<Product> products = productRepository.findAll(pageable);
        List<Category> categories = categoryRepository.findAll();

        HashMap<Integer, String> categoriesMap = new HashMap<>();

        for (Category category : categories) {
            categoriesMap.put(category.getId(), category.getName());
        }

        long count = productRepository.count();
        double pageCounter = Math.ceil((double) count / (double) perPage);

        model.addAttribute("categoriesMap", categoriesMap);
        model.addAttribute("products", products);
        model.addAttribute("pageCount", (int) pageCounter);
        model.addAttribute("perPage", perPage);
        model.addAttribute("count", count);
        model.addAttribute("page", page);

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
    public String edit(@Valid Product product, BindingResult bindingResult, MultipartFile file, RedirectAttributes redirectAttributes, Model model) throws IOException {

        Product currentProduct = productRepository.getOne(product.getId());
        List<Category> categories = categoryRepository.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            model.addAttribute("productName", currentProduct.getName());
            return "admin/products/add";
        }

        boolean isFileOk = false;
        byte[] fileBytes = fileBytes = file.getBytes();
        String fileName = fileName = file.getOriginalFilename();
        Path path = Paths.get("src\\main\\resources\\static\\media\\" + fileName);


        if (!file.isEmpty()) {
            if (fileName.endsWith("jpg") || fileName.endsWith("png")) {
                isFileOk = true;
            }
        } else {
            isFileOk = true;
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
                Path path1 = Paths.get("src\\main\\resources\\static\\media\\" + currentProduct.getImage());
                Files.delete(path1);
                product.setImage(fileName);
                Files.write(path, fileBytes);
            } else {
                product.setImage(currentProduct.getImage());
            }
            productRepository.save(product);
        }
        return "redirect:/admin/products/edit/" + product.getId();
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
