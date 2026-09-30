package com.ashu.OrderService.Feing;

import com.ashu.OrderService.Model.CartDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("CARTSERVICE")
public interface CartFeing {
    @GetMapping("cart/user")
    ResponseEntity<List<CartDetails>> getAllCartItems(@RequestParam String id);
}
