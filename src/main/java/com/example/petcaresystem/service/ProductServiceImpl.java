package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Product;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.ProductRepository;
import com.example.petcaresystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Product createProduct(Product product, Long shopKeeperId) {
        // Validate shop keeper exists and has correct role
        User shopKeeper = userRepository.findById(shopKeeperId)
                .orElseThrow(() -> new RuntimeException("Shop keeper not found with id: " + shopKeeperId));

        if (!shopKeeper.getRole().equals("SHOP_KEEPER")) {
            throw new RuntimeException("User must have SHOP_KEEPER role to create products");
        }

        // Validate required fields
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("Product price must be greater than 0");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        product.setShopKeeper(shopKeeper);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Set status based on stock
        if (product.getStockQuantity() > 0) {
            product.setStatus(Product.ProductStatus.AVAILABLE);
        } else {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = getProductById(id);

        // Update fields
        if (productDetails.getName() != null) {
            existingProduct.setName(productDetails.getName());
        }

        if (productDetails.getDescription() != null) {
            existingProduct.setDescription(productDetails.getDescription());
        }

        if (productDetails.getPrice() != null && productDetails.getPrice() > 0) {
            existingProduct.setPrice(productDetails.getPrice());
        }

        if (productDetails.getProductCategory() != null) {
            existingProduct.setProductCategory(productDetails.getProductCategory());
        }

        if (productDetails.getBrand() != null) {
            existingProduct.setBrand(productDetails.getBrand());
        }

        if (productDetails.getImageUrl() != null) {
            existingProduct.setImageUrl(productDetails.getImageUrl());
        }

        if (productDetails.getStatus() != null) {
            existingProduct.setStatus(productDetails.getStatus());
        }

        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);

        // Instead of hard delete, mark as discontinued
        product.setStatus(Product.ProductStatus.DISCONTINUED);
        productRepository.save(product);

        // Or use hard delete:
        // productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByShopKeeper(Long shopKeeperId) {
        return productRepository.findByShopKeeperId(shopKeeperId);
    }

    @Override
    public List<Product> getProductsByCategory(Product.ProductCategory category) {
        return productRepository.findByProductCategory(category);
    }

    @Override
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByStatusAndStockQuantityGreaterThan(
                Product.ProductStatus.AVAILABLE, 0);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> getProductsOnSale() {
        return productRepository.findByStatus(Product.ProductStatus.ON_SALE);
    }

    @Override
    public Product updateProductStock(Long productId, Integer newStock) {
        Product product = getProductById(productId);

        if (newStock < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        // Update status based on new stock
        if (newStock > 0) {
            product.setStatus(Product.ProductStatus.AVAILABLE);
        } else {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }

        return productRepository.save(product);
    }

    @Override
    public Product decreaseProductStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        product.decreaseStock(quantity);
        return productRepository.save(product);
    }

    @Override
    public Product increaseProductStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        product.increaseStock(quantity);
        return productRepository.save(product);
    }

    @Override
    public boolean validateShopKeeperRole(Long shopKeeperId) {
        Optional<User> user = userRepository.findById(shopKeeperId);
        return user.isPresent() && user.get().getRole().equals("SHOP_KEEPER");
    }

    @Override
    public Map<String, Long> getProductCountByCategory() {
        List<Object[]> results = productRepository.countProductsByCategory();
        Map<String, Long> categoryCount = new HashMap<>();

        for (Object[] result : results) {
            Product.ProductCategory category = (Product.ProductCategory) result[0];
            Long count = (Long) result[1];
            categoryCount.put(category.toString(), count);
        }

        // Ensure all categories are included, even with zero count
        Arrays.stream(Product.ProductCategory.values()).forEach(category -> {
            categoryCount.putIfAbsent(category.toString(), 0L);
        });

        return categoryCount;
    }

    @Override
    public List<Product> getProductsSortedByPrice(String sortOrder) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
            return productRepository.findByStatusOrderByPriceDesc(Product.ProductStatus.AVAILABLE);
        } else {
            return productRepository.findByStatusOrderByPriceAsc(Product.ProductStatus.AVAILABLE);
        }
    }
}