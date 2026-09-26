package com.ashu.ProductService.Repository;

import com.ashu.ProductService.Model.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductDetails,Long> {

    @Query("SELECT p FROM products p WHERE p.stockQuantity >0 AND LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    List<ProductDetails> findByKeyword(@Param("keyword") String keyword);
}
