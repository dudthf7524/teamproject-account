package com.teamproject.account.income;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomeDTO {
    private Long incomeId;
    private String incomeCategoryName;
    private String price;
    private String memo;
    private String regDt;
    private Long memberNo;
    private Long incomeCategoryId;
    // Getters and Setters

    public static IncomeDTO toIncomeDTO(IncomeEntity incomeEntity) {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setIncomeId(incomeEntity.getIncomeId());
        incomeDTO.setIncomeCategoryName(incomeEntity.getIncomeCategory().getIncomeCategoryName());
        incomeDTO.setPrice(incomeEntity.getPrice());
        incomeDTO.setMemo(incomeEntity.getMemo());
        incomeDTO.setRegDt(incomeEntity.getRegDt());
        incomeDTO.setMemberNo(incomeEntity.getMemberNo());
        return incomeDTO;
    }
}
