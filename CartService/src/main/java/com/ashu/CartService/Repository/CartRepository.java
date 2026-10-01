package com.ashu.CartService.Repository;

import com.ashu.CartService.Model.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartDetails,Long> {

    List<CartDetails> findByUserId(Long userId);

    Optional<CartDetails> findByProductIdAndUserId(long productId,long userId);

}
