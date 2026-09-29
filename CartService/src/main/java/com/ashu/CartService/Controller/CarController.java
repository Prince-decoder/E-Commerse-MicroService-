package com.ashu.CartService.Controller;

import com.ashu.CartService.Model.CartDetails;
import com.ashu.CartService.Model.CartRequest;
import com.ashu.CartService.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CarController {
    @Autowired
    CartService cartService;

    @GetMapping("all")
    public ResponseEntity<List<CartDetails>> getAllCart() {
        return cartService.getAllCart();
    }

    @PostMapping("add")
    public ResponseEntity<String> addToCart(@RequestBody CartRequest cartRequest) {

        return cartService.addToCart(cartRequest.getProductId(),cartRequest.getUserId(),cartRequest.getQuantity());
    }
    @GetMapping("user")
    public ResponseEntity<List<CartDetails>> getAllCartItems(@RequestParam String id)
    {
        if(cartService.findByUserId(id).isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else
            return ResponseEntity.ok(cartService.findByUserId(id));
    }
    @GetMapping("name")
    public ResponseEntity<List<CartDetails>> getAllItemsByName(@RequestParam String name)
    {
        if(cartService.findAllByUserName(name).isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return ResponseEntity.ok(cartService.findAllByUserName(name).get());
        }
//        return cartService.findAllByUserName(name)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("user/{id}")
    public ResponseEntity<List<CartDetails>> getCartById(@PathVariable String id) {
        return new ResponseEntity<>(cartService.findByUserId(id), HttpStatus.OK);
    }
}
