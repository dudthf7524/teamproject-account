package com.teamproject.account.income;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    List<IncomeEntity> findAllByRegDtContainsOrderByRegDtAsc(String regDt);
}
