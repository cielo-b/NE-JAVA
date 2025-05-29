package com.app.NE.repositories;

import com.app.NE.models.PayRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPayrollRepository extends JpaRepository<PayRoll, UUID> {
    boolean existsByMonthAndYear(int month, int year);

    PayRoll findByMonthAndYear(int month, int year);
}
