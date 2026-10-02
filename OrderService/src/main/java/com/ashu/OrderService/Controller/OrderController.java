package com.ashu.OrderService.Controller;

import com.ashu.OrderService.Feing.CartFeing;
import com.ashu.OrderService.Model.DTO.OrderResponse;
import com.ashu.OrderService.Model.Order;
import com.ashu.OrderService.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("place")
    public ResponseEntity<String>  placeOrder(@RequestParam String userId)
    {
        return orderService.orderPlaced(userId);
    }

    @GetMapping("user")
    public ResponseEntity<OrderResponse> getOrders(@RequestParam String userId)
    {
        return orderService.getOrders(userId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
