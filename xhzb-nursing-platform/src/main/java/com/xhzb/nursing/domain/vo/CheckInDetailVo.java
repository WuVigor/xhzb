package com.xhzb.nursing.domain.vo;

import com.xhzb.nursing.domain.Contract;
import com.xhzb.nursing.domain.dto.checkin.CheckInElderDto;
import com.xhzb.nursing.domain.dto.checkin.ElderFamilyDto;
import lombok.Data;

import java.util.List;

@Data
public class CheckInDetailVo {
    private CheckInElderDto checkInElderVo;
    private List<ElderFamilyDto> elderFamilyVoList;
    private CheckInConfigVo checkInConfigVo;
    private Contract contract;
}
