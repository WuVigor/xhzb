package com.xhzb.nursing.domain.dto.checkin;

import lombok.Data;

import java.util.List;

@Data
public class CheckInApplyDto {
    private Long healthAssessmentId;
    private CheckInElderDto checkInElderDto;
    private List<ElderFamilyDto> elderFamilyDtoList;
    private CheckInConfigDto checkInConfigDto;
    private CheckInContractDto checkInContractDto;
}
