package com.ashu.OrderService.Controller;

import com.ashu.OrderService.Feing.CartFeing;
import com.ashu.OrderService.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("place")
    public ResponseEntity<String>  placeOrder(@RequestParam String userId)
    {
        return orderService.orderPlaced(userId);
    }
}
