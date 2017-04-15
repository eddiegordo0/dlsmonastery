package net.myspring.future.modules.crm.service;

import net.myspring.future.modules.crm.domain.ImeSaleReport;
import net.myspring.future.modules.crm.mapper.ImeSaleReportMapper;
import net.myspring.future.modules.crm.mapper.ImeStockReportMapper;
import net.myspring.future.modules.crm.model.InventoryMainModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ImeSaleReportService {

    @Autowired
    private ImeSaleReportMapper imeSaleReportMapper;
    @Autowired
    private ImeStockReportMapper imeStockReportMapper;

    public ImeSaleReport findOne(String id){
        ImeSaleReport imeSaleReport=imeSaleReportMapper.findOne(id);
        return imeSaleReport;
    }

    //按照机构汇总
    public InventoryMainModel findOfficeInventoryMainModel(Map<String, Object> map) {
        return null;
    }

    //按照型号汇总
    public InventoryMainModel findProductInventoryMainModel(Map<String, Object> map) {
        return null;
    }



    private void setPercentage(InventoryMainModel inventoryMainModel) {

    }

    private String division(Integer totalQty, Integer qty) {
        if (qty == 0 || totalQty == 0) {
            return "0.00";
        }
        BigDecimal percent = new BigDecimal(qty).multiply(new BigDecimal(100)).divide(new BigDecimal(totalQty),2, BigDecimal.ROUND_HALF_UP);
        return percent.toString();
    }
}