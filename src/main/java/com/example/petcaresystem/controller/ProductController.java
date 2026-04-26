package com.example.petcaresystem.controller;

import com.example.petcaresystem.model.Product;
import com.example.petcaresystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value = "api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create a new product
    @PostMapping("/create/{shopKeeperId}")
    public ResponseEntity<?> createProduct(
            @PathVariable Long shopKeeperId,
            @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product, shopKeeperId);
            return ResponseEntity.ok(createdProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get products by shop keeper
    @GetMapping("/shop-keeper/{shopKeeperId}")
    public ResponseEntity<List<Product>> getProductsByShopKeeper(@PathVariable Long shopKeeperId) {
        List<Product> products = productService.getProductsByShopKeeper(shopKeeperId);
        return ResponseEntity.ok(products);
    }

    // Get products by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable Product.ProductCategory category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // Get products by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Product>> getProductsByStatus(
            @PathVariable Product.ProductStatus status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(products);
    }

    // Get available products (in stock)
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    // Search products by name
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    // Get products by price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    // Get low stock products
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    // Get products on sale
    @GetMapping("/on-sale")
    public ResponseEntity<List<Product>> getProductsOnSale() {
        List<Product> products = productService.getProductsOnSale();
        return ResponseEntity.ok(products);
    }

    // Update product
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update product stock
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer newStock) {
        try {
            Product updatedProduct = productService.updateProductStock(id, newStock);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Decrease product stock
    @PutMapping("/{id}/decrease-stock")
    public ResponseEntity<?> decreaseProductStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.decreaseProductStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Increase product stock
    @PutMapping("/{id}/increase-stock")
    public ResponseEntity<?> increaseProductStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.increaseProductStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete product (soft delete - mark as discontinued)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body("Product deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get product count by category
    @GetMapping("/stats/category-count")
    public ResponseEntity<Map<String, Long>> getProductCountByCategory() {
        Map<String, Long> categoryCount = productService.getProductCountByCategory();
        return ResponseEntity.ok(categoryCount);
    }

    // Get products sorted by price
    @GetMapping("/sorted")
    public ResponseEntity<List<Product>> getProductsSortedByPrice(@RequestParam(defaultValue = "asc") String sort) {
        List<Product> products = productService.getProductsSortedByPrice(sort);
        return ResponseEntity.ok(products);
    }
}