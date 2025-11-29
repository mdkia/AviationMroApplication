package com.aviation.mro.modules.warehouse.repository;


import com.aviation.mro.modules.warehouse.domain.enums.WithdrawalStatus;
import com.aviation.mro.modules.warehouse.domain.model.StockWithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockWithdrawalRepository extends JpaRepository<StockWithdrawalRequest, Long> {

    // درخواست‌های در انتظار تأیید
    List<StockWithdrawalRequest> findByStatus(WithdrawalStatus status);

    // درخواست‌های یک کاربر خاص
    List<StockWithdrawalRequest> findByRequestedBy(String requestedBy);

    // درخواست‌های یک آیتم موجودی خاص
    @Query("SELECT swr FROM StockWithdrawalRequest swr WHERE swr.inventoryItem.id = :inventoryItemId")
    List<StockWithdrawalRequest> findByInventoryItemId(@Param("inventoryItemId") Long inventoryItemId);

    // درخواست‌های در انتظار تأیید برای مدیران انبار
    @Query("SELECT swr FROM StockWithdrawalRequest swr WHERE swr.status = 'PENDING' ORDER BY swr.requestDate DESC")
    List<StockWithdrawalRequest> findPendingApprovals();

    // شمارش درخواست‌های در انتظار تأیید
    @Query("SELECT COUNT(swr) FROM StockWithdrawalRequest swr WHERE swr.status = 'PENDING'")
    Long countPendingApprovals();
}