package com.xhzb.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhzb.common.utils.StringUtils;
import com.xhzb.nursing.domain.Elder;
import com.xhzb.nursing.mapper.ElderMapper;
import com.xhzb.nursing.service.IElderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService {
    @Override
    public List<Elder> selectElderList(Elder elder) {
        LambdaQueryWrapper<Elder> query = new LambdaQueryWrapper<>();
        query.like(StringUtils.isNotEmpty(elder.getName()), Elder::getName, elder.getName())
                .eq(StringUtils.isNotEmpty(elder.getIdCardNo()), Elder::getIdCardNo, elder.getIdCardNo())
                .eq(elder.getStatus() != null, Elder::getStatus, elder.getStatus())
                .orderByDesc(Elder::getCreateTime);
        return list(query);
    }
}
