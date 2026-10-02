package com.ashu.CartService.Service;

import com.ashu.CartService.Feing.ProductFeing;
import com.ashu.CartService.Feing.UserFeing;
import com.ashu.CartService.Model.CartDetails;
import com.ashu.CartService.Model.Product.ProductDetails;
import com.ashu.CartService.Model.User.UserDetail.UserDetails;
import com.ashu.CartService.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepo;
    @Autowired
    private ProductFeing productFeing;
    @Autowired
    private UserFeing userFeing;

    public List<CartDetails> findByUserId(String userId) {
        if(userFeing.getUserId(userId).isEmpty()){
            return null;
        }
        else
        {
            return cartRepo.findByUserId(Long.parseLong(userId));
        }
    }

    public List<CartDetails> findAllByUserId(@RequestParam String userId) {
        return cartRepo.findByUserId(Long.parseLong(userId));
    }
    public Optional<List<CartDetails>> findAllByUserName(@RequestParam String name) {
        UserDetails user= userFeing.getUserByUsername(name).getBody();
        if(user==null)
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of( cartRepo.findByUserId(user.getId()));
        }
    }

    public Optional<CartDetails> findByProduct(String productId,String userId) {
        return cartRepo.findByProductIdAndUserId(Long.parseLong(productId),Long.parseLong(userId));
    }

    public ResponseEntity<String> addToCart(String productId,String userId,Integer quantity){

        if(userFeing.getUserId(userId).isEmpty()||productFeing.getDetailsById(productId).isEmpty()||quantity==null){
            return ResponseEntity.badRequest().build();
        }
        else {
            ProductDetails product = productFeing.getDetailsById(productId).get();
            CartDetails cartDetails = new CartDetails();
            cartDetails.setUserId(Long.parseLong(userId));
            cartDetails.setProductId(Long.parseLong(productId));
            cartDetails.setQuantity(quantity);
            cartDetails.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartDetails.getQuantity())));
            cartRepo.save(cartDetails);
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        }
    }
    public ResponseEntity<List<CartDetails>> getAllCart() {

        return ResponseEntity.ok(cartRepo.findAll());
    }
}
