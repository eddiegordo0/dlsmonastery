package net.myspring.future.modules.layout.web.controller;

import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.layout.domain.AdApply;
import net.myspring.future.modules.layout.dto.AdApplyDto;
import net.myspring.future.modules.layout.service.AdApplyService;
import net.myspring.future.modules.layout.web.form.AdApplyBillForm;
import net.myspring.future.modules.layout.web.form.AdApplyForm;
import net.myspring.future.modules.layout.web.form.AdApplyGoodsForm;
import net.myspring.future.modules.layout.web.query.AdApplyQuery;
import net.myspring.util.text.StringUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "layout/adApply")
public class AdApplyController {

    @Autowired
    private AdApplyService adApplyService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<AdApplyDto> findPage(Pageable pageable, AdApplyQuery adApplyQuery){
        return adApplyService.findPage(pageable,adApplyQuery);
    }

    @RequestMapping(value = "getQuery", method = RequestMethod.GET)
    public AdApplyQuery getQuery(AdApplyQuery adApplyQuery) {
        return adApplyQuery;
    }

    @RequestMapping(value = "getForm", method = RequestMethod.GET)
    public AdApplyForm getForm(AdApplyForm adApplyForm) {
        return adApplyService.getForm(adApplyForm);
    }

    @RequestMapping(value = "findOne")
    public AdApplyDto findOne(String id){
        return adApplyService.findOne(id);
    }

    @RequestMapping(value = "save")
    public RestResponse save(AdApplyForm adApplyForm){
        adApplyService.save(adApplyForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "billSave")
    public RestResponse billSave(AdApplyBillForm adApplyBillForm){
        adApplyService.billSave(adApplyBillForm);
        return new RestResponse("开单申请成功", ResponseCodeEnum.saved.name());
    }


    @RequestMapping(value = "getBillForm", method = RequestMethod.GET)
    public AdApplyBillForm getBillForm(AdApplyBillForm adApplyBillForm){
        return adApplyService.getBillForm(adApplyBillForm);
    }

    @RequestMapping(value = "findAdApplyList", method = RequestMethod.GET)
    public List<AdApplyDto> findAdApplyList(String billType){
        if(StringUtils.isBlank(billType)){
            return null;
        }
        return adApplyService.findAdApplyList(billType);
    }

    @RequestMapping(value = "getAdApplyGoodsList")
    public AdApplyGoodsForm getAdApplyGoodsList(AdApplyGoodsForm adApplyGoodsForm){
        return adApplyService.getAdApplyGoodsList(adApplyGoodsForm);
    }
    @RequestMapping(value = "goodsSave")
    public RestResponse  goodsSave(AdApplyGoodsForm adApplyGoodsForm){
        adApplyService.goodsSave(adApplyGoodsForm);
        return new RestResponse("分货成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "export", method = RequestMethod.GET)
    public String export(AdApplyQuery adApplyQuery) throws IOException {
        Workbook workbook = new SXSSFWorkbook(10000);
        return adApplyService.findSimpleExcelSheets(workbook,adApplyQuery);
    }
}
