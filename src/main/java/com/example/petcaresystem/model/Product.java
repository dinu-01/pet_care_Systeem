package com.example.petcaresystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    // For product images

    // Product status
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    // Product categories
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "shop_keeper_id")

    private User shopKeeper; // User with SHOP_KEEPER role

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums
    public enum ProductStatus {
        AVAILABLE, OUT_OF_STOCK, DISCONTINUED, ON_SALE
    }

    public enum ProductCategory {
        FOOD, TOYS, GROOMING, HEALTHCARE, ACCESSORIES, BEDDING, TRAINING, OTHER
    }

    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProductStatus.AVAILABLE;
        this.stockQuantity = 0;
    }

    public Product(String name, String description, Double price, Integer stockQuantity,
                   ProductCategory productCategory, User shopKeeper) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.productCategory = productCategory;
        this.shopKeeper = shopKeeper;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
        updateStatusBasedOnStock();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public ProductCategory getProductCategory() { return productCategory; }
    public void setProductCategory(ProductCategory productCategory) { this.productCategory = productCategory; }

    public User getShopKeeper() { return shopKeeper; }
    public void setShopKeeper(User shopKeeper) { this.shopKeeper = shopKeeper; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    private void updateStatusBasedOnStock() {
        if (this.stockQuantity <= 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (this.status == ProductStatus.OUT_OF_STOCK && this.stockQuantity > 0) {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    public boolean isAvailable() {
        return this.status == ProductStatus.AVAILABLE && this.stockQuantity > 0;
    }

    public void decreaseStock(Integer quantity) {
        if (quantity > 0 && this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            updateStatusBasedOnStock();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void increaseStock(Integer quantity) {
        if (quantity > 0) {
            this.stockQuantity += quantity;
            updateStatusBasedOnStock();
            this.updatedAt = LocalDateTime.now();
        }

    }

}