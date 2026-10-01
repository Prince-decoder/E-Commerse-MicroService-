package com.ashu.OrderService.Service;

import com.ashu.OrderService.Feing.CartFeing;
import com.ashu.OrderService.Feing.ProductFeing;
import com.ashu.OrderService.Model.Cart.CartDetails;
import com.ashu.OrderService.Model.DTO.OrderResponse;
import com.ashu.OrderService.Model.Order;
import com.ashu.OrderService.Model.Product.ProductDetails;
import com.ashu.OrderService.Model.Product.ProductResponse;
import com.ashu.OrderService.Repository.OderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OderRepository orderRepo;
    @Autowired
    private CartFeing  cartFeing;
    @Autowired
    private ProductFeing productFeing;

    public ResponseEntity<String> orderPlaced(String userId)
    {
        List<Long> itemIds = new ArrayList<>();
        List<CartDetails>  cartDetails = cartFeing.getAllCartItems(userId).getBody();
        if(cartDetails==null)
        {
            return new ResponseEntity<>("No cart",HttpStatus.NOT_FOUND);
        }
        BigDecimal total=BigDecimal.ZERO;
        for(CartDetails Details:cartDetails)
        {
            itemIds.add(Details.getId());
            total=total.add(Details.getPrice());
        }
        Order  order = new Order();
        order.setItemId(itemIds);
        order.setOrderPrice(total);
        order.setUserId(userId);
        orderRepo.save(order);
        return new ResponseEntity<>("Order Placed", HttpStatus.OK);
    }

    public Optional<OrderResponse> getOrders(String userId)
    {
        Order od= orderRepo.findByUserId(userId);
        if(od==null)
        {
            return Optional.empty();
        }
        List<Long> itemsIds= od.getItemId();
        List<ProductResponse> res= new ArrayList<>();
        for (Long i:itemsIds)
        {
            ProductDetails details=productFeing.getProductDetailsById(String.valueOf(i)).getBody();
            ProductResponse product=new ProductResponse();
            detailsToResponse(details,product);
            product.setQuantity(cartFeing.getCartByIds(i.toString(), userId).getBody().getQuantity());
            res.add(product);
        }
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderStatus(od.getOrderStatus());
        orderResponse.setProducts(res);
        orderResponse.setTotalPrice(od.getOrderPrice());
        return Optional.of(orderResponse);
    }

    private void detailsToResponse(ProductDetails details,ProductResponse response)
    {
        response.setName(details.getName());
        response.setDescription(details.getDescription());
        response.setCategory(details.getCategory());
        response.setImageUrl(details.getImageUrl());
        response.setPrice(details.getPrice());
    }
    {

    }
}
