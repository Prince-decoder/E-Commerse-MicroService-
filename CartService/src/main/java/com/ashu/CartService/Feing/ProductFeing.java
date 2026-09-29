package com.ashu.CartService.Feing;

import com.ashu.CartService.Model.Product.ProductDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient("PRODUCTSERVICE")
public interface ProductFeing {


    @GetMapping("product/details")
    public Optional<ProductDetails> getDetailsById(@RequestParam String id);

}