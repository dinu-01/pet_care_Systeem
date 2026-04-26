package com.example.petcaresystem.dto;

import com.example.petcaresystem.model.Product;
import java.time.LocalDateTime;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String brand;
    private String imageUrl;
    private Product.ProductStatus status;
    private Product.ProductCategory productCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Shop keeper info
    private Long shopKeeperId;
    private String shopKeeperName;

    // Constructors
    public ProductDTO() {}

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.brand = product.getBrand();
        this.imageUrl = product.getImageUrl();
        this.status = product.getStatus();
        this.productCategory = product.getProductCategory();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();

        this.shopKeeperId = product.getShopKeeper().getId();
        this.shopKeeperName = product.getShopKeeper().getUsername();
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
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Product.ProductStatus getStatus() { return status; }
    public void setStatus(Product.ProductStatus status) { this.status = status; }

    public Product.ProductCategory getProductCategory() { return productCategory; }
    public void setProductCategory(Product.ProductCategory productCategory) { this.productCategory = productCategory; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getShopKeeperId() { return shopKeeperId; }
    public void setShopKeeperId(Long shopKeeperId) { this.shopKeeperId = shopKeeperId; }

    public String getShopKeeperName() { return shopKeeperName; }
    public void setShopKeeperName(String shopKeeperName) { this.shopKeeperName = shopKeeperName; }
}