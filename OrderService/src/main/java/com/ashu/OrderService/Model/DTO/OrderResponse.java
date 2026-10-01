package com.ashu.OrderService.Model.DTO;

import com.ashu.OrderService.Model.OrderStatus;
import com.ashu.OrderService.Model.Product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private List<ProductResponse> products;
}
