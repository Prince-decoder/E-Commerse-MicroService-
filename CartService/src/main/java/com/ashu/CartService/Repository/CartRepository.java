package com.ashu.CartService.Repository;

import com.ashu.CartService.Model.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartDetails,Long> {

    List<CartDetails> findByUserId(Long userId);
}
