package ru.hogwarts.school.balance;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BalanceOperationRepository extends JpaRepository<BalanceOperation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) //TODO добавить фильтр по создателю
    @Query ("SELECT op FROM BalanceOperation  op " +
    "LEFT JOIN FETCH op.sourceFaculty " +
    "LEFT JOIN FETCH op.user " +
    "LEFT JOIN FETCH op.task " +
    "WHERE op.id IN :ids")

    List<BalanceOperation> findAllByIdInWithRelations (@Param("ids") List<Long> ids);

}