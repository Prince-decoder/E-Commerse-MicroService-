package com.ashu.OrderService.Model.Product;

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
    private String category;
    private String imageUrl;
    private Integer Quantity;
    private BigDecimal price;
}
