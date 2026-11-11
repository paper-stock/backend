package pl.maropce.paperstock.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
    List<Order> findAllByAppUserIdAndOrderStatus(UUID appUserId, OrderStatus orderStatus);
    List<Order> findAllByAppUserId(UUID appUserId);
}
