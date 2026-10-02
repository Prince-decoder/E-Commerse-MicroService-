package com.ashu.OrderService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus=OrderStatus.PROCESSING;
    private BigDecimal orderPrice;
    private List<Long> itemId;

    @CreationTimestamp
    private LocalDateTime orderDate;
    @UpdateTimestamp
    private LocalDateTime orderUpdate;
}
