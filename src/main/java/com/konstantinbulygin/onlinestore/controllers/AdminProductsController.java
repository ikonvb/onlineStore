package com.konstantinbulygin.onlinestore.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.konstantinbulygin.onlinestore.model.CategoryRepository;
import com.konstantinbulygin.onlinestore.model.ProductRepository;
import com.konstantinbulygin.onlinestore.model.data.Category;
import com.konstantinbulygin.onlinestore.model.data.Product;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cloud_name}")
    private String cloudName;

    @Value("${api_key}")
    private String apiKey;

    @Value("${api_secret}")
    private String apiSecret;

    private final String secureUrl = "secure_url";

    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "korustlt", "api_key", "619614386456773", "api_secret", "kAYZ3cfRWBRsBPyX_miIRL0PKEI"));

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

        String energy = System.getenv().get("cloud_name");

        System.out.println("[[[[[[[[[[[[[[[[[" + energy + "]]]]]]]]]]]]]]]]]");

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
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png format");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else if (productExists != null) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {


//            Cloudinary cloudinary = Singleton.getCloudinary();
            //start of Cloudinary functionality
//            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                    "cloud_name", cloudName,
//                    "api_key", apiKey,
//                    "api_secret", apiSecret));

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


            product.setSlug(slug);
            product.setImage(fileName);

            product.setImageUrl(uploadResult.get(secureUrl).toString());

            productRepository.save(product);
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


                //start of Cloudinary functionality
//                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                        "cloud_name", "korustlt",
//                        "api_key", "619614386456773",
//                        "api_secret", "kAYZ3cfRWBRsBPyX_miIRL0PKEI"));

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
