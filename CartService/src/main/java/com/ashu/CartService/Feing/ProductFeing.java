package com.ashu.CartService.Feing;

import com.ashu.CartService.Model.Product.ProductDetails;
import com.ashu.CartService.Model.Product.dto.ProductRequest;
import com.ashu.CartService.Model.Product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient("PRODUCTSERVICE")
public interface ProductFeing {

    @GetMapping("product/search")
    public List<ProductDetails> findByKeyword(@RequestParam String  keyword);

    @GetMapping("product/details")
    public Optional<ProductDetails> getDetailsById(@RequestParam String id);

    @GetMapping("product/all")
    public List<ProductDetails> getAllProducts();
    @PostMapping("product/add")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest request);
}