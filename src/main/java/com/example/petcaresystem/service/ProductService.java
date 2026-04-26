package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {

    // CRUD Operations
    Product createProduct(Product product, Long shopKeeperId);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);

    // Query Operations
    List<Product> getProductsByShopKeeper(Long shopKeeperId);
    List<Product> getProductsByCategory(Product.ProductCategory category);
    List<Product> getProductsByStatus(Product.ProductStatus status);
    List<Product> getAvailableProducts();
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice);
    List<Product> getLowStockProducts(Integer threshold);
    List<Product> getProductsOnSale();

    // Inventory Management
    Product updateProductStock(Long productId, Integer newStock);
    Product decreaseProductStock(Long productId, Integer quantity);
    Product increaseProductStock(Long productId, Integer quantity);

    // Business Logic
    boolean validateShopKeeperRole(Long shopKeeperId);
    Map<String, Long> getProductCountByCategory();
    List<Product> getProductsSortedByPrice(String sortOrder);
}