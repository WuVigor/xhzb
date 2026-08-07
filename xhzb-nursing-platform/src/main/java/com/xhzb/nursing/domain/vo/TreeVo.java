package com.xhzb.nursing.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeVo {
    private String label;
    private Long value;
    private List<TreeVo> children = new ArrayList<>();
}
