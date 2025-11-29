package com.aviation.mro.modules.sales.repository;

import com.aviation.mro.modules.sales.domain.model.Customer;
import com.aviation.mro.modules.sales.domain.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByCompanyNameContainingIgnoreCase(String companyName);

    List<Customer> findByCustomerType(CustomerType customerType);

    List<Customer> findByIsActiveTrue();

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = true")
    Long countActiveCustomers();

    boolean existsByCustomerCode(String customerCode);

    boolean existsByEmail(String email);
}
