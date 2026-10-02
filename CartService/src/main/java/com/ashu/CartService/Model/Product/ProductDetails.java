package com.ashu.CartService.Model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {
    private Long id;
    private String name;
    private String description;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private BigDecimal price;
    private ProductStatus status=ProductStatus.AVAILABLE;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
