package com.docencia.tutorial05.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    private static final List<Map<String, String>> products = new ArrayList<>(List.of(
            Map.of("id", "1", "name", "TV", "description", "Best TV", "price", "$999.99"),
            Map.of("id", "2", "name", "iPhone", "description", "Best iPhone", "price", "$899.99"),
            Map.of("id", "3", "name", "Chromecast", "description", "Best Chromecast", "price", "$35.00"),
            Map.of("id", "4", "name", "Glasses", "description", "Best Glasses", "price", "$120.50")
    ));

    @GetMapping("/products")
    public String index(Model model) {
        model.addAttribute("title", "Products - Online Store");
        model.addAttribute("subtitle", "List of products");
        model.addAttribute("products", products);
        return "product/index";
    }

    @GetMapping("/products/{id}")
    public String show(@PathVariable String id, Model model) {
        int productId = Integer.parseInt(id) - 1;

        try {
        productId = Integer.parseInt(id) - 1;
        }catch (NumberFormatException e) {
        return "redirect:/";
        }

    if (productId < 0 || productId >= products.size()) {
        return "redirect:/";
    }

        Map<String, String> product = products.get(productId);

    // Convertir el precio a double (quitando el $)
    double priceValue = Double.parseDouble(product.get("price").replace("$", ""));

    // Pasamos priceValue al modelo
    model.addAttribute("title", product.get("name") + " - Online Store");
    model.addAttribute("subtitle", product.get("name") + " - Product Information");
    model.addAttribute("product", product);
    model.addAttribute("priceValue", priceValue);

    return "product/show";

    }

    @GetMapping("/products/create")
        public String create(Model model) {
            model.addAttribute("title", "Create Product");
            model.addAttribute("productForm", new ProductForm());
            return "product/create";
    }

    @PostMapping("/products/save")
    public String save(@Valid @ModelAttribute("productForm") ProductForm productForm,
                   BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("title", "Create Product");
        return "product/create";
    }

    Map<String, String> newProduct = new HashMap<>();
    newProduct.put("id", String.valueOf(products.size() + 1));
    newProduct.put("name", productForm.getName());
    newProduct.put("description", "Best " + productForm.getName());
    newProduct.put("price", "$" + productForm.getPrice());

    products.add(newProduct);

    model.addAttribute("title", "Product Created");
    model.addAttribute("subtitle", "The product has been created successfully");
    model.addAttribute("product", newProduct);

    return "product/created";
    }
};