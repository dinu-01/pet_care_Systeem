package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.Product;
import com.example.petcaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by shop keeper
    List<Product> findByShopKeeper(User shopKeeper);
    List<Product> findByShopKeeperId(Long shopKeeperId);

    // Find products by category
    List<Product> findByProductCategory(Product.ProductCategory category);

    // Find products by status
    List<Product> findByStatus(Product.ProductStatus status);

    // Find available products (in stock and available)
    List<Product> findByStatusAndStockQuantityGreaterThan(Product.ProductStatus status, Integer quantity);

    // Find products by name (search)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products by price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Find products with low stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.stockQuantity > 0")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // ✅ Dedicated method for products on sale
    @Query("SELECT p FROM Product p WHERE p.status = 'ON_SALE'")
    List<Product> findOnSaleProducts();

    // Find products by brand
    List<Product> findByBrandContainingIgnoreCase(String brand);

    // Find available products sorted by price
    List<Product> findByStatusOrderByPriceAsc(Product.ProductStatus status);
    List<Product> findByStatusOrderByPriceDesc(Product.ProductStatus status);

    // Count products by category
    @Query("SELECT p.productCategory, COUNT(p) FROM Product p GROUP BY p.productCategory")
    List<Object[]> countProductsByCategory();
}
