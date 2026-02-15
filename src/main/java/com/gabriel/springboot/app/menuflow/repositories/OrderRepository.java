package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTableSessionId(Long tableSessionId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByTableSessionIdAndStatus(Long tableSessionId, OrderStatus status);

    List<Order> findByStatusIn(List<OrderStatus> statuses);

    @Query("SELECT DISTINCT o from Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.dish d LEFT JOIN FETCH o.invoice i WHERE o.id = :orderId")
    Optional<Order> findOrderWithDetailsById(@Param("orderId") Long orderId);
}
