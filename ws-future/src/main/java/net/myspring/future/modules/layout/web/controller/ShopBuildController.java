package net.myspring.future.modules.layout.web.controller;

import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.layout.dto.ShopBuildDto;
import net.myspring.future.modules.layout.service.ShopBuildService;
import net.myspring.future.modules.layout.web.form.ShopBuildDetailOrAuditForm;
import net.myspring.future.modules.layout.web.form.ShopBuildForm;
import net.myspring.future.modules.layout.web.query.ShopBuildQuery;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "layout/shopBuild")
public class ShopBuildController {

    @Autowired
    private ShopBuildService shopBuildService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<ShopBuildDto> list(Pageable pageable, ShopBuildQuery shopBuildQuery) {
        Page<ShopBuildDto> page = shopBuildService.findPage(pageable,shopBuildQuery);
        return page;
    }

    @RequestMapping(value = "getQuery")
    public ShopBuildQuery getQuery(ShopBuildQuery shopBuildQuery) {
        return shopBuildQuery;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public RestResponse save(ShopBuildForm shopBuildForm) {
        shopBuildService.save(shopBuildForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "audit")
    public RestResponse audit(ShopBuildDetailOrAuditForm shopBuildDetailOrAuditForm){
        String message = shopBuildService.audit(shopBuildDetailOrAuditForm);
        if(message != null){
            return  new RestResponse("审核失败，原因："+message, ResponseCodeEnum.removed.name(),false);
        }else{
            return new RestResponse("审核成功", ResponseCodeEnum.removed.name());
        }
    }

    @RequestMapping(value = "batchAudit")
    public RestResponse batchAudit(@RequestParam(value = "ids[]")String[] ids, Boolean pass) {
        String message  = shopBuildService.batchAudit(ids,pass);
        if(message != null){
            return  new RestResponse("批量审核部分失败，原因："+message, ResponseCodeEnum.removed.name(),false);
        }else{
            return new RestResponse("批量审核成功", ResponseCodeEnum.removed.name());
        }
    }


    @RequestMapping(value = "delete")
    public RestResponse delete(ShopBuildForm shopBuildForm) {
        shopBuildService.logicDelete(shopBuildForm.getId());
        RestResponse restResponse = new RestResponse("删除成功", ResponseCodeEnum.removed.name());
        return restResponse;
    }

    @RequestMapping(value = "findOne")
    public ShopBuildDto findOne(String id){
        return shopBuildService.findOne(id);
    }


    @RequestMapping(value = "getAuditForm")
    public ShopBuildDetailOrAuditForm auditForm(ShopBuildDetailOrAuditForm shopBuildDetailOrAuditForm){
        return shopBuildDetailOrAuditForm;
    }


    @RequestMapping(value = "export", method = RequestMethod.GET)
    public String export(ShopBuildQuery shopBuildQuery) throws IOException{
        Workbook workbook = new SXSSFWorkbook(10000);
        return shopBuildService.findSimpleExcelSheets(workbook,shopBuildQuery);
    }

}
