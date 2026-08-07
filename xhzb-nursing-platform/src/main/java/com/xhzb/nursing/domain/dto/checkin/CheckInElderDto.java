package com.xhzb.nursing.domain.dto.checkin;

import lombok.Data;

@Data
public class CheckInElderDto {
    private String name;
    private String image;
    private String idCardNo;
    private Integer sex;
    private String phone;
    private String birthday;
    private Integer age;
    private String address;
    private String idCardNationalEmblemImg;
    private String idCardPortraitImg;
    private String nation;
    private String nationDetail;
    private String educationLevel;
    private String socialSecurityCard;
    private String livingSituation;
    private String religiousBelief;
    private String religiousBeliefDetail;
    private String economicSource;
    private String maritalStatus;
    private String medicalPaymentMethod;
    private Object coreSuggestion;
}
