package com.ashu.ProductService.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String name;
    private String description;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private BigDecimal price;
}
