package net.myspring.future.modules.crm.web.query;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.query.BaseQuery;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;

import java.time.LocalDate;
import java.util.List;

public class CarrierOrderQuery extends BaseQuery{

    private String businessId;
    private String goodsOrderRemarks;
    private String goodsOrderStatus;
    private String carrierOrderStatus;
    private String depotName;
    private String code;
    private String shipDate;
    private String createdDate;
    private String remarks;
    private String notEqualStatus;
    private String notEqualStoreId;
    private List<String> goodsOrderStatusList=Lists.newArrayList();

    public String getNotEqualStatus() {
        return notEqualStatus;
    }

    public void setNotEqualStatus(String notEqualStatus) {
        this.notEqualStatus = notEqualStatus;
    }

    public String getNotEqualStoreId() {
        return notEqualStoreId;
    }

    public void setNotEqualStoreId(String notEqualStoreId) {
        this.notEqualStoreId = notEqualStoreId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getGoodsOrderRemarks() {
        return goodsOrderRemarks;
    }

    public void setGoodsOrderRemarks(String goodsOrderRemarks) {
        this.goodsOrderRemarks = goodsOrderRemarks;
    }

    public String getGoodsOrderStatus() {
        return goodsOrderStatus;
    }

    public void setGoodsOrderStatus(String goodsOrderStatus) {
        this.goodsOrderStatus = goodsOrderStatus;
    }

    public String getCarrierOrderStatus() {
        return carrierOrderStatus;
    }

    public void setCarrierOrderStatus(String carrierOrderStatus) {
        this.carrierOrderStatus = carrierOrderStatus;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShipDate() {
        return shipDate;
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<String> getGoodsOrderStatusList() {
        return goodsOrderStatusList;
    }

    public void setGoodsOrderStatusList(List<String> goodsOrderStatusList) {
        this.goodsOrderStatusList = goodsOrderStatusList;
    }

    public LocalDate getCreatedDateStart() {
        if(StringUtils.isNotBlank(createdDate)) {
            return LocalDateUtils.parse(createdDate.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getCreatedDateEnd() {
        if(StringUtils.isNotBlank(createdDate)) {
            return LocalDateUtils.parse(createdDate.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        } else {
            return null;
        }
    }

    public LocalDate getShipDateStart() {
        if(StringUtils.isNotBlank(shipDate)) {
            return LocalDateUtils.parse(shipDate.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getShipDateEnd() {
        if(StringUtils.isNotBlank(shipDate)) {
            return LocalDateUtils.parse(shipDate.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        } else {
            return null;
        }
    }
}
