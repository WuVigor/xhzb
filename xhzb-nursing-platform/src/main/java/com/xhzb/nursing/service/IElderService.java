package com.xhzb.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhzb.nursing.domain.Elder;

import java.util.List;

public interface IElderService extends IService<Elder> {
    List<Elder> selectElderList(Elder elder);
}
