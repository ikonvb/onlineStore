package com.konstantinbulygin.onlinestore.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    //for Cloudinary functionality
    private final String secureUrl = "secure_url";
    private final Cloudinary cloudinary = Singleton.getCloudinary();

    public AdminProductsController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String index(Model model, @RequestParam(value = "page", required = false) Integer p) {

        //pagination functionality
        int page = (p != null) ? p : 0;

        int itemPerPage = 10;
        Pageable pageable = PageRequest.of(page, itemPerPage);
        Page<Product> products = productRepository.findAll(pageable);
        List<Category> categories = categoryRepository.findAll();

        HashMap<Integer, String> categoriesMap = new HashMap<>();

        for (Category category : categories) {
            categoriesMap.put(category.getId(), category.getName());
        }

        long count = productRepository.count();
        double pageCounter = Math.ceil((double) count / (double) itemPerPage);

        model.addAttribute("categoriesMap", categoriesMap);
        model.addAttribute("products", products);
        model.addAttribute("pageCount", (int) pageCounter);
        model.addAttribute("perPage", itemPerPage);
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

        //retrieve list of categories
        List<Category> categories = categoryRepository.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "admin/products/add";
        }

        boolean isFileOk = false;
        byte[] fileBytes = null;
        String fileName = null;

        //try to read files
        try {
            fileBytes = file.getBytes();
            fileName = file.getOriginalFilename();

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
            //if adding file has exception
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png format");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else if (productExists != null) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {

            //cloudinary specific functionality
            Map params = ObjectUtils.asMap(
                    "public_id", fileName,
                    "overwrite", true,
                    "notification_url", "https://mysite.com/notify_endpoint",
                    "resource_type", "image"
            );

            Map uploadResult = null;

            try {

                uploadResult = cloudinary.uploader().upload(fileBytes, params);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //end of Cloudinary functionality

            //save product to db
            product.setSlug(slug);
            product.setImage(fileName);
            product.setImageUrl(uploadResult.get(secureUrl).toString());
            productRepository.save(product);
        }
//        return "redirect:/admin/products/add";
        return "redirect:/admin/products";
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
        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();

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
                //todo delete image from cloudinary

                Map params = ObjectUtils.asMap(
                        "public_id", fileName,
                        "overwrite", true,
                        "notification_url", "https://mysite.com/notify_endpoint",
                        "resource_type", "image"
                );

                Map uploadResult = null;

                try {
                    uploadResult = cloudinary.uploader().upload(fileBytes, params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //end of Cloudinary functionality

                product.setImageUrl(uploadResult.get(secureUrl).toString());
                product.setImage(fileName);

            } else {
                product.setImage(currentProduct.getImage());
            }

            productRepository.save(product);
        }
        return "redirect:/admin/products/edit/" + product.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {

        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Product deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/products";
    }

}
