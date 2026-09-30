package com.ashu.OrderService.Service;

import com.ashu.OrderService.Feing.CartFeing;
import com.ashu.OrderService.Model.CartDetails;
import com.ashu.OrderService.Model.Order;
import com.ashu.OrderService.Repository.OderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OderRepository orderRepo;
    @Autowired
    private CartFeing  cartFeing;

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
}
