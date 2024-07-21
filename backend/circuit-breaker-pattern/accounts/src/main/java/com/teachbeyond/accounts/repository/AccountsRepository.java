package com.teachbeyond.accounts.repository;

import com.teachbeyond.accounts.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByCustomerId(Long customerId);

    /**
     * @param customerId - Input CustomerId
     */
    @Transactional
    @Modifying
    void deleteByCustomerId(Long customerId);
}
