package com.xhzb.nursing.job;

import com.xhzb.nursing.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 合同状态定时更新任务。
 */
@Slf4j
@Component("contractJob")
public class ContractJob
{
    private final IContractService contractService;

    public ContractJob(IContractService contractService)
    {
        this.contractService = contractService;
    }

    public void updateContractStatusJob()
    {
        contractService.updateContractStatus();
        log.info("定时更新合同状态成功！");
    }
}
