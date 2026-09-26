package com.ashu.ProductService.Server;

import com.ashu.ProductService.Model.DTO.ProductRequest;
import com.ashu.ProductService.Model.DTO.ProductResponse;
import com.ashu.ProductService.Model.ProductDetails;
import com.ashu.ProductService.Model.ProductStatus;
import com.ashu.ProductService.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public Optional<ProductResponse> getById(String id)
    {
        return productRepo.findById(Long.valueOf(id))
                .map(this::productToResponse);
    }

    public List<ProductDetails> findByKeyword(String keyword)
    {
        return productRepo.findByKeyword(keyword);
    }

    public List<ProductDetails> getAllProducts()
    {
        return productRepo.findAll();
    }
    public ResponseEntity<String> addProduct(ProductRequest request)
    {
        ProductDetails details = new ProductDetails();
        if(request!=null)
        {
            requestToProduct(request,details);
            if(request.getName()!=null) {
                productRepo.save(details);
                return ResponseEntity.ok("Product successfully added!");
            }
            else {
                return ResponseEntity.badRequest().body("Name is required");
            }
        }
        return ResponseEntity.badRequest().body("request is required");
    }

    private void requestToProduct(ProductRequest request,ProductDetails details)
    {
        details.setName(request.getName());
        details.setDescription(request.getDescription());
        details.setStockQuantity(request.getStockQuantity());
        details.setCategory(request.getCategory());
        details.setImageUrl(request.getImageUrl());
        details.setPrice(request.getPrice());
        if(request.getStatus()!=null)
        {
            details.setStatus(request.getStatus());
        }
        if(request.getStockQuantity() != null && request.getStockQuantity() <= 0)
        {
            details.setStatus(ProductStatus.OUT_OF_STOCK);
        }
    }

    private ProductResponse productToResponse(ProductDetails productDetails) {
        ProductResponse response = new ProductResponse();
        response.setName(productDetails.getName());
        response.setDescription(productDetails.getDescription());
        response.setPrice(productDetails.getPrice());
        response.setStockQuantity(productDetails.getStockQuantity());
        response.setCategory(productDetails.getCategory());
        response.setImageUrl(productDetails.getImageUrl());
        return response;
    }
}
