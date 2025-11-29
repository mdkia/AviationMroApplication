package com.aviation.mro.modules.sales.repository;

import com.aviation.mro.modules.sales.domain.model.SalesOrder;
import com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    List<SalesOrder> findByCustomerId(Long customerId);

    List<SalesOrder> findByStatus(SalesOrderStatus status);

    List<SalesOrder> findByCreatedById(Long userId);

    //    @Query("SELECT so FROM SalesOrder so WHERE so.expectedDeliveryDate < :currentDate AND so.status NOT IN ('DELIVERED', 'CANCELLED')")
    @Query("SELECT so FROM SalesOrder so WHERE so.expectedDeliveryDate < :currentDate AND so.status NOT IN " +
            "(com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus.DELIVERED, com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus.CANCELLED)")
    List<SalesOrder> findOverdueOrders(LocalDateTime currentDate);

    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate")
    List<SalesOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(so.totalAmount) FROM SalesOrder so WHERE so.status = 'DELIVERED' AND so.orderDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByOrderNumber(String orderNumber);
}
