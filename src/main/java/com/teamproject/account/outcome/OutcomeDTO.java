package com.teamproject.account.outcome;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutcomeDTO {
    private Long outcomeId;
    private String outcomeCategoryName;
    private Long price;
    private String memo;
    private String regDt;
    private Long memberNo;
    private Long outcomeCategoryId;

    public static OutcomeDTO toOutcomeDTO(OutcomeEntity outcomeEntity) {
        OutcomeDTO outcomeDTO = new OutcomeDTO();
        outcomeDTO.setOutcomeId(outcomeEntity.getOutcomeId());

        outcomeDTO.setOutcomeCategoryName(outcomeEntity.getOutcomeCategory().getOutcomeCategoryName());
        outcomeDTO.setPrice(outcomeEntity.getPrice());
        outcomeDTO.setMemo(outcomeEntity.getMemo());
        outcomeDTO.setRegDt(outcomeEntity.getRegDt());
        outcomeDTO.setMemberNo(outcomeEntity.getMemberNo());
        return outcomeDTO;
    }
}
