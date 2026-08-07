package com.xhzb.nursing.domain.dto.checkin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CheckInConfigDto {
    private Long nursingLevelId;
    private String nursingLevelName;
    private Long bedId;
    private String bedNumber;
    private Long roomId;
    private String code;
    private Long floorId;
    private String floorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime feeStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime feeEndDate;

    private BigDecimal deposit;
    private BigDecimal nursingFee;
    private BigDecimal bedFee;
    private BigDecimal insurancePayment;
    private BigDecimal governmentSubsidy;
    private BigDecimal otherFees;
}
