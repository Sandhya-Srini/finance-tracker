package com.finst.financetracker.repository;

import com.finst.financetracker.entity.TransactionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> , JpaSpecificationExecutor<TransactionEntity> {
    List<TransactionEntity> findByUserId(String userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TransactionEntity> findById(Long uuid);
}

