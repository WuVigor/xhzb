package com.xhzb.nursing.controller;

import com.xhzb.common.annotation.Log;
import com.xhzb.common.core.controller.BaseController;
import com.xhzb.common.core.domain.AjaxResult;
import com.xhzb.common.core.page.TableDataInfo;
import com.xhzb.common.enums.BusinessType;
import com.xhzb.nursing.domain.Elder;
import com.xhzb.nursing.service.IElderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nursing/elder")
@Tag(name = "老人档案相关接口")
public class ElderController extends BaseController {
    private final IElderService elderService;

    public ElderController(IElderService elderService) {
        this.elderService = elderService;
    }

    @PreAuthorize("@ss.hasPermi('nursing:elder:list') or @ss.hasPermi('nursing:checkIn:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询老人档案")
    public TableDataInfo list(Elder elder) {
        startPage();
        List<Elder> list = elderService.selectElderList(elder);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('nursing:elder:query') or @ss.hasPermi('nursing:checkIn:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(elderService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('nursing:elder:add')")
    @Log(title = "老人档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Elder elder) {
        return toAjax(elderService.save(elder));
    }

    @PreAuthorize("@ss.hasPermi('nursing:elder:edit')")
    @Log(title = "老人档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Elder elder) {
        return toAjax(elderService.updateById(elder));
    }

    @PreAuthorize("@ss.hasPermi('nursing:elder:remove')")
    @Log(title = "老人档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(elderService.removeByIds(List.of(ids)));
    }
}
