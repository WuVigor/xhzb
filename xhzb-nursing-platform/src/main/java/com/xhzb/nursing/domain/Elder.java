package com.xhzb.nursing.domain;

import com.xhzb.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "老人档案")
public class Elder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String image;
    private String idCardNo;
    private Integer sex;
    private Integer status;
    private String phone;
    private String birthday;
    private String address;
    private String idCardNationalEmblemImg;
    private String idCardPortraitImg;
    private String bedNumber;
    private Long bedId;
    private String nation;
    private String educationLevel;
    private String socialSecurityCard;
    private String livingSituation;
    private String religiousBelief;
    private String economicSource;
    private String maritalStatus;
    private String medicalPaymentMethod;
    private String coreSuggestion;
}
