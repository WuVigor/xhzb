package com.xhzb.nursing.domain.dto.checkin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CheckInContractDto {
    private String contractName;
    private String agreementPath;
    private String thirdPartyPhone;
    private String thirdPartyName;
    private String elderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signDate;
}
