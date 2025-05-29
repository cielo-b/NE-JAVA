package com.app.NE.repositories;

import com.app.NE.enums.EDeductionName;
import com.app.NE.models.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDeductionRepository extends JpaRepository<Deduction, UUID> {
    boolean existsByDeductionName(EDeductionName name);

    Optional<Deduction> findByCode(String code);

    Deduction findByDeductionName(EDeductionName eDeductionName);
}
