package com.aviation.mro.modules.accounting.repository;

import com.aviation.mro.modules.accounting.domain.model.Account;
import com.aviation.mro.modules.accounting.domain.enums.AccountType;
import com.aviation.mro.modules.accounting.domain.enums.AccountCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountCode(String accountCode);

    List<Account> findByAccountType(AccountType accountType);

    List<Account> findByCategory(AccountCategory category);

    List<Account> findByParentAccountId(Long parentAccountId);

    List<Account> findByIsParentTrue();

    List<Account> findByIsActiveTrue();

    @Query("SELECT a FROM Account a WHERE a.parentAccount IS NULL")
    List<Account> findRootAccounts();

    @Query("SELECT a FROM Account a WHERE a.accountType IN :accountTypes AND a.isActive = true")
    List<Account> findByAccountTypes(List<AccountType> accountTypes);

    @Query("SELECT COALESCE(SUM(a.currentBalance), 0) FROM Account a WHERE a.accountType = :accountType")
    Double getTotalBalanceByAccountType(AccountType accountType);

    boolean existsByAccountCode(String accountCode);
}
