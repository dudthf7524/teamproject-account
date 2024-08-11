package com.teamproject.account.outcome;

import com.teamproject.account.income.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutcomeRepository extends JpaRepository<OutcomeEntity, Long> {
    List<OutcomeEntity> findAllByRegDtContainsOrderByRegDtAsc(String regDt);
}
