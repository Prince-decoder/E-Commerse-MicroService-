package com.ashu.OrderService.Feing;

import com.ashu.OrderService.Model.Product.ProductDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PRODUCTSERVICE")
public interface ProductFeing {

    @GetMapping("product/details")
    ResponseEntity<ProductDetails> getProductDetailsById(@RequestParam String id);

}
