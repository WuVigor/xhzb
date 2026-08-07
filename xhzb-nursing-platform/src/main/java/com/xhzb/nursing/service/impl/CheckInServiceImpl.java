package com.xhzb.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhzb.common.exception.base.BaseException;
import com.xhzb.nursing.domain.*;
import com.xhzb.nursing.domain.dto.checkin.*;
import com.xhzb.nursing.domain.vo.CheckInConfigVo;
import com.xhzb.nursing.domain.vo.CheckInDetailVo;
import com.xhzb.nursing.domain.vo.RoomVo;
import com.xhzb.nursing.mapper.CheckInMapper;
import com.xhzb.nursing.service.*;
import com.xhzb.nursing.util.CodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;

/**
 * 入住管理Service业务层处理
 *
 * @author itheima
 * @date 2026-08-06
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService
{
    private final CheckInMapper checkInMapper;
    private final IHealthAssessmentService healthAssessmentService;
    private final IHealthAssessmentReportService healthAssessmentReportService;
    private final IElderService elderService;
    private final IBedService bedService;
    private final ICheckInConfigService checkInConfigService;
    private final IContractService contractService;
    private final IRoomService roomService;

    public CheckInServiceImpl(CheckInMapper checkInMapper,
                              IHealthAssessmentService healthAssessmentService,
                              IHealthAssessmentReportService healthAssessmentReportService,
                              IElderService elderService,
                              IBedService bedService,
                              ICheckInConfigService checkInConfigService,
                              IContractService contractService,
                              IRoomService roomService) {
        this.checkInMapper = checkInMapper;
        this.healthAssessmentService = healthAssessmentService;
        this.healthAssessmentReportService = healthAssessmentReportService;
        this.elderService = elderService;
        this.bedService = bedService;
        this.checkInConfigService = checkInConfigService;
        this.contractService = contractService;
        this.roomService = roomService;
    }

    /**
     * 查询入住管理
     *
     * @param id 入住管理主键
     * @return 入住管理
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住管理列表
     *
     * @param checkIn 入住管理
     * @return 入住管理
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住管理
     *
     * @param checkIn 入住管理
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        return save(checkIn)? 1 : 0;
    }

    /**
     * 修改入住管理
     *
     * @param checkIn 入住管理
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        return updateById(checkIn)? 1 : 0;
    }

    /**
     * 批量删除入住管理
     *
     * @param ids 需要删除的入住管理主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))? 1 : 0;
    }

    /**
     * 删除入住管理信息
     *
     * @param id 入住管理主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id)? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long apply(CheckInApplyDto dto) {
        if (dto == null || dto.getHealthAssessmentId() == null
                || dto.getCheckInElderDto() == null
                || dto.getCheckInConfigDto() == null
                || dto.getCheckInContractDto() == null) {
            throw new BaseException("入住申请信息不完整");
        }

        HealthAssessment assessment = healthAssessmentService.getById(dto.getHealthAssessmentId());
        if (assessment == null) {
            throw new BaseException("健康评估记录不存在");
        }
        if (!Integer.valueOf(1).equals(assessment.getEvaluationProgress())) {
            throw new BaseException("评估未完成的老人，不能入住");
        }

        CheckInElderDto elderDto = dto.getCheckInElderDto();
        if (elderDto.getIdCardNo() == null || elderDto.getIdCardNo().isBlank()) {
            throw new BaseException("老人身份证号不能为空");
        }
        Elder elder = elderService.getOne(new LambdaQueryWrapper<Elder>()
                .eq(Elder::getIdCardNo, elderDto.getIdCardNo())
                .orderByDesc(Elder::getId)
                .last("limit 1"));
        if (elder != null && Integer.valueOf(1).equals(elder.getStatus())) {
            throw new BaseException("老人已入住");
        }

        CheckInConfigDto configDto = dto.getCheckInConfigDto();
        if (configDto.getBedId() == null) {
            throw new BaseException("请选择入住床位");
        }
        Bed bed = bedService.getById(configDto.getBedId());
        if (bed == null) {
            throw new BaseException("所选床位不存在");
        }
        if (!Integer.valueOf(0).equals(bed.getBedStatus())) {
            throw new BaseException("所选床位已不可入住，请重新选择");
        }
        bed.setBedStatus(1);
        bedService.updateById(bed);

        if (elder == null) {
            elder = new Elder();
        }
        BeanUtil.copyProperties(elderDto, elder);
        elder.setCoreSuggestion(elderDto.getCoreSuggestion() == null ? null
                : String.valueOf(elderDto.getCoreSuggestion()));
        if (elderDto.getNationDetail() != null && !elderDto.getNationDetail().isBlank()) {
            elder.setNation(elderDto.getNationDetail());
        }
        if (elderDto.getReligiousBeliefDetail() != null && !elderDto.getReligiousBeliefDetail().isBlank()) {
            elder.setReligiousBelief(elderDto.getReligiousBeliefDetail());
        }
        elder.setStatus(1);
        elder.setBedId(bed.getId());
        elder.setBedNumber(bed.getBedNumber());
        elderService.saveOrUpdate(elder);

        CheckInContractDto contractDto = dto.getCheckInContractDto();
        Contract contract = new Contract();
        BeanUtil.copyProperties(contractDto, contract);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        contract.setContractNumber("HT" + CodeGenerator.generateContractNumber());
        contract.setStartDate(configDto.getStartDate());
        contract.setEndDate(configDto.getEndDate());
        contract.setStatus(0);
        contract.setSortOrder(0);
        contract.setThirdPartyName(contract.getThirdPartyName() == null ? "" : contract.getThirdPartyName());
        contract.setThirdPartyPhone(contract.getThirdPartyPhone() == null ? "" : contract.getThirdPartyPhone());
        contractService.save(contract);

        CheckIn checkIn = new CheckIn();
        checkIn.setElderName(elder.getName());
        checkIn.setElderId(elder.getId());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setStartDate(configDto.getStartDate());
        checkIn.setEndDate(configDto.getEndDate());
        checkIn.setNursingLevelName(configDto.getNursingLevelName());
        checkIn.setBedNumber(bed.getBedNumber());
        checkIn.setStatus(0);
        checkIn.setSortOrder(0);
        checkIn.setRemark(JSONUtil.toJsonStr(dto.getElderFamilyDtoList() == null
                ? Collections.emptyList() : dto.getElderFamilyDtoList()));
        save(checkIn);

        CheckInConfig config = new CheckInConfig();
        BeanUtil.copyProperties(configDto, config);
        config.setCheckInId(checkIn.getId());
        config.setSortOrder(0);
        config.setDeposit(defaultAmount(config.getDeposit()));
        config.setNursingFee(defaultAmount(config.getNursingFee()));
        config.setBedFee(defaultAmount(config.getBedFee()));
        config.setInsurancePayment(defaultAmount(config.getInsurancePayment()));
        config.setGovernmentSubsidy(defaultAmount(config.getGovernmentSubsidy()));
        config.setOtherFees(defaultAmount(config.getOtherFees()));
        checkInConfigService.save(config);

        assessment.setElderId(elder.getId());
        assessment.setCheckInStatus(1);
        healthAssessmentService.updateById(assessment);
        healthAssessmentReportService.update(new LambdaUpdateWrapper<HealthAssessmentReport>()
                .eq(HealthAssessmentReport::getHealthAssessmentId, dto.getHealthAssessmentId().toString())
                .set(HealthAssessmentReport::getCheckInStatus, 1));
        return checkIn.getId();
    }

    @Override
    public CheckInDetailVo detail(Long id) {
        CheckIn checkIn = getById(id);
        if (checkIn == null) {
            throw new BaseException("入住记录不存在");
        }
        Elder elder = elderService.getById(checkIn.getElderId());
        CheckInElderDto elderVo = new CheckInElderDto();
        if (elder != null) {
            BeanUtil.copyProperties(elder, elderVo);
        }

        List<ElderFamilyDto> families = Collections.emptyList();
        if (checkIn.getRemark() != null && !checkIn.getRemark().isBlank()) {
            families = JSONUtil.toList(checkIn.getRemark(), ElderFamilyDto.class);
        }

        CheckInConfig config = checkInConfigService.getOne(new LambdaQueryWrapper<CheckInConfig>()
                .eq(CheckInConfig::getCheckInId, id)
                .orderByDesc(CheckInConfig::getId)
                .last("limit 1"));
        CheckInConfigVo configVo = new CheckInConfigVo();
        if (config != null) {
            BeanUtil.copyProperties(config, configVo);
        }
        configVo.setStartDate(checkIn.getStartDate());
        configVo.setEndDate(checkIn.getEndDate());
        if (elder != null && elder.getBedId() != null) {
            Bed bed = bedService.getById(elder.getBedId());
            if (bed != null) {
                configVo.setBedId(bed.getId());
                configVo.setBedNumber(bed.getBedNumber());
                configVo.setRoomId(bed.getRoomId());
                RoomVo room = roomService.getRoomById(bed.getRoomId());
                if (room != null) {
                    configVo.setCode(room.getCode());
                    configVo.setFloorId(room.getFloorId() == null ? null : Long.valueOf(room.getFloorId()));
                    configVo.setFloorName(room.getFloorName());
                }
            }
        }

        Contract contract = contractService.getOne(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getElderId, checkIn.getElderId())
                .orderByDesc(Contract::getId)
                .last("limit 1"));

        CheckInDetailVo result = new CheckInDetailVo();
        result.setCheckInElderVo(elderVo);
        result.setElderFamilyVoList(families);
        result.setCheckInConfigVo(configVo);
        result.setContract(contract);
        return result;
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
