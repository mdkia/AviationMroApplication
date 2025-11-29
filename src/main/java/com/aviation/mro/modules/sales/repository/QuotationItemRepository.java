package com.aviation.mro.modules.sales.repository;

import com.aviation.mro.modules.sales.domain.model.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {

    List<QuotationItem> findByQuotationId(Long quotationId);

    List<QuotationItem> findByPartId(Long partId);
}
