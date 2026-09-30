package com.ashu.OrderService.Repository;

import com.ashu.OrderService.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OderRepository extends JpaRepository<Order,Long> {

}
