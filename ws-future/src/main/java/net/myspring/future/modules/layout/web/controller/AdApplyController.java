package net.myspring.future.modules.layout.web.controller;

import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.layout.dto.AdApplyCountDto;
import net.myspring.future.modules.layout.dto.AdApplyDto;
import net.myspring.future.modules.layout.service.AdApplyService;
import net.myspring.future.modules.layout.web.form.*;
import net.myspring.future.modules.layout.web.query.AdApplyQuery;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;


@RestController
@RequestMapping(value = "layout/adApply")
public class AdApplyController {

    @Autowired
    private AdApplyService adApplyService;


    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public Page<AdApplyDto> findPage(Pageable pageable, AdApplyQuery adApplyQuery){
        return adApplyService.findPage(pageable,adApplyQuery);
    }

    @RequestMapping(value = "getCountQty",method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyCountDto getCountQty(AdApplyQuery adApplyQuery){
        return adApplyService.getCountQty(adApplyQuery);
    }

    @RequestMapping(value = "getQuery", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyQuery getQuery(AdApplyQuery adApplyQuery) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDateTimeUtils.getFirstDayOfMonth(today.atStartOfDay()).toLocalDate();
        adApplyQuery.setCreatedDate(firstDayOfMonth.toString()+ CharConstant.DATE_RANGE_SPLITTER+today.toString());
        return adApplyQuery;
    }

    @RequestMapping(value = "getForm", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyForm getForm(AdApplyForm adApplyForm) {
        return adApplyService.getForm(adApplyForm);
    }

    @RequestMapping(value = "getEditForm",method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyEditForm getEditForm(AdApplyEditForm adApplyEditForm){
        return adApplyEditForm;
    }

    @RequestMapping(value = "findOne")
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyDto findOne(String id){
        return adApplyService.findOne(id);
    }

    @RequestMapping(value = "save")
    @PreAuthorize("hasPermission(null,'crm:adApply:edit')")
    public RestResponse save(AdApplyForm adApplyForm){
        adApplyService.save(adApplyForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "saveConfirmQty")
    @PreAuthorize("hasPermission(null,'crm:adApply:edit')")
    public RestResponse saveConfirmQty(AdApplyEditForm adApplyEditForm){
        adApplyService.saveConfirmQty(adApplyEditForm);
        return  new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "billSave")
    @PreAuthorize("hasPermission(null,'crm:adApply:bill')")
    public RestResponse billSave(AdApplyBillForm adApplyBillForm){
        adApplyService.billSave(adApplyBillForm);
        return new RestResponse("开单申请成功", ResponseCodeEnum.saved.name());
    }


    @RequestMapping(value = "getBillForm", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyBillForm getBillForm(AdApplyBillForm adApplyBillForm){
        return adApplyService.getBillForm(adApplyBillForm);
    }

    @RequestMapping(value = "findAdApplyList", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyBillTypeChangeForm findAdApplyList(String billType){
        if(StringUtils.isBlank(billType)){
            throw new ServiceException("未选中开单类型");
        }
        return adApplyService.findAdApplyList(billType);
    }

    @RequestMapping(value = "getAdApplyGoodsList")
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public AdApplyGoodsForm getAdApplyGoodsList(AdApplyGoodsForm adApplyGoodsForm){
        return adApplyService.getAdApplyGoodsList(adApplyGoodsForm);
    }
    @RequestMapping(value = "goodsSave")
    @PreAuthorize("hasPermission(null,'crm:adApply:goods')")
    public RestResponse  goodsSave(AdApplyGoodsForm adApplyGoodsForm){
        adApplyService.goodsSave(adApplyGoodsForm);
        return new RestResponse("分货成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "export", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'crm:adApply:view')")
    public ModelAndView export(AdApplyQuery adApplyQuery) throws IOException {
        return new ModelAndView(new ExcelView(),"simpleExcelBook",adApplyService.export(adApplyQuery));
    }
}
