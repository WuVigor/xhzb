package com.xhzb.nursing.domain.vo;

import com.xhzb.nursing.domain.dto.checkin.CheckInConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckInConfigVo extends CheckInConfigDto {
    private Long id;
    private Long checkInId;
}
