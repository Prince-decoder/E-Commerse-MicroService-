package com.ashu.ProductService.Controller;

import com.ashu.ProductService.Model.DTO.ProductRequest;
import com.ashu.ProductService.Model.DTO.ProductResponse;
import com.ashu.ProductService.Model.ProductDetails;
import com.ashu.ProductService.Server.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productSer;

    @GetMapping("all")
    public ResponseEntity<List<ProductDetails>> findAll() {

        if (productSer.getAllProducts()==null)
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(productSer.getAllProducts(), HttpStatus.OK);
        }
    }

    @GetMapping("search")
    public ResponseEntity<List<ProductDetails>> findBYKey(@RequestParam String keyword) {
        if (productSer.findByKeyword(keyword)==null)
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(productSer.findByKeyword(keyword), HttpStatus.OK);
        }
    }

    @GetMapping("find")
    public ResponseEntity<ProductResponse> getProductById(@RequestParam String id) {
        return productSer.getById(id)
                .map(x->new ResponseEntity<>(x,HttpStatus.FOUND))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("add")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest request)
    {
        return productSer.addProduct(request);
    }
}
