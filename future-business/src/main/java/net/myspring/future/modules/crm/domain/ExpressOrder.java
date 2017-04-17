package net.myspring.future.modules.crm.domain;


import com.google.common.collect.Lists;
import net.myspring.common.domain.DataEntity;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.ExpressCompany;
import net.myspring.future.modules.layout.domain.AdGoodsOrder;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="crm_express_order")
public class ExpressOrder extends DataEntity<ExpressOrder> {
    private String extendId;
    private String extendType;
    private Integer version = 0;
    private String contator;
    private String address;
    private String mobilePhone;
    private String fromDepotId;
    private String toDepotId;
    private Integer expressPrintQty;
    private LocalDateTime expressPrintDate;
    private LocalDateTime outPrintDate;
    private BigDecimal shouldGet;
    private BigDecimal shouldPay;
    private BigDecimal realGet;
    private BigDecimal realPay;
    private LocalDate printDate;
    private String outCode;
    private String extendBusinessId;
    private Boolean selfPickup;
    private String shipType;
    private String expressCodes;
    private Integer totalQty;
    private Integer mobileQty;
    private String areaName;
    private BigDecimal weight;
    private List<AdGoodsOrder> adGoodsOrderList = Lists.newArrayList();
    private List<String> adGoodsOrderIdList = Lists.newArrayList();
    private List<Express> expressList = Lists.newArrayList();
    private List<String> expressIdList = Lists.newArrayList();
    private ExpressCompany expressCompany;
    private String expressCompanyId;
    private List<GoodsOrder> goodsOrderList = Lists.newArrayList();
    private List<String> goodsOrderIdList = Lists.newArrayList();
    private List<StoreAllot> storeAllotList = Lists.newArrayList();
    private List<String> storeAllotIdList = Lists.newArrayList();
    private Depot fromDepot;
    private Depot toDepot;

    public String getExtendId() {
        return extendId;
    }

    public void setExtendId(String extendId) {
        this.extendId = extendId;
    }

    public String getExtendType() {
        return extendType;
    }

    public void setExtendType(String extendType) {
        this.extendType = extendType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContator() {
        return contator;
    }

    public void setContator(String contator) {
        this.contator = contator;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFromDepotId() {
        return fromDepotId;
    }

    public void setFromDepotId(String fromDepotId) {
        this.fromDepotId = fromDepotId;
    }

    public String getToDepotId() {
        return toDepotId;
    }

    public void setToDepotId(String toDepotId) {
        this.toDepotId = toDepotId;
    }

    public Integer getExpressPrintQty() {
        return expressPrintQty;
    }

    public void setExpressPrintQty(Integer expressPrintQty) {
        this.expressPrintQty = expressPrintQty;
    }

    public LocalDateTime getExpressPrintDate() {
        return expressPrintDate;
    }

    public void setExpressPrintDate(LocalDateTime expressPrintDate) {
        this.expressPrintDate = expressPrintDate;
    }

    public LocalDateTime getOutPrintDate() {
        return outPrintDate;
    }

    public void setOutPrintDate(LocalDateTime outPrintDate) {
        this.outPrintDate = outPrintDate;
    }

    public BigDecimal getShouldGet() {
        return shouldGet;
    }

    public void setShouldGet(BigDecimal shouldGet) {
        this.shouldGet = shouldGet;
    }

    public BigDecimal getShouldPay() {
        return shouldPay;
    }

    public void setShouldPay(BigDecimal shouldPay) {
        this.shouldPay = shouldPay;
    }

    public BigDecimal getRealGet() {
        return realGet;
    }

    public void setRealGet(BigDecimal realGet) {
        this.realGet = realGet;
    }

    public BigDecimal getRealPay() {
        return realPay;
    }

    public void setRealPay(BigDecimal realPay) {
        this.realPay = realPay;
    }

    public LocalDate getPrintDate() {
        return printDate;
    }

    public void setPrintDate(LocalDate printDate) {
        this.printDate = printDate;
    }

    public String getOutCode() {
        return outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }

    public String getExtendBusinessId() {
        return extendBusinessId;
    }

    public void setExtendBusinessId(String extendBusinessId) {
        this.extendBusinessId = extendBusinessId;
    }

    public Boolean getSelfPickup() {
        return selfPickup;
    }

    public void setSelfPickup(Boolean selfPickup) {
        this.selfPickup = selfPickup;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public String getExpressCodes() {
        return expressCodes;
    }

    public void setExpressCodes(String expressCodes) {
        this.expressCodes = expressCodes;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getMobileQty() {
        return mobileQty;
    }

    public void setMobileQty(Integer mobileQty) {
        this.mobileQty = mobileQty;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public List<AdGoodsOrder> getAdGoodsOrderList() {
        return adGoodsOrderList;
    }

    public void setAdGoodsOrderList(List<AdGoodsOrder> adGoodsOrderList) {
        this.adGoodsOrderList = adGoodsOrderList;
    }

    public List<String> getAdGoodsOrderIdList() {
        return adGoodsOrderIdList;
    }

    public void setAdGoodsOrderIdList(List<String> adGoodsOrderIdList) {
        this.adGoodsOrderIdList = adGoodsOrderIdList;
    }

    public List<Express> getExpressList() {
        return expressList;
    }

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
    }

    public List<String> getExpressIdList() {
        return expressIdList;
    }

    public void setExpressIdList(List<String> expressIdList) {
        this.expressIdList = expressIdList;
    }

    public ExpressCompany getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(ExpressCompany expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getExpressCompanyId() {
        return expressCompanyId;
    }

    public void setExpressCompanyId(String expressCompanyId) {
        this.expressCompanyId = expressCompanyId;
    }

    public List<GoodsOrder> getGoodsOrderList() {
        return goodsOrderList;
    }

    public void setGoodsOrderList(List<GoodsOrder> goodsOrderList) {
        this.goodsOrderList = goodsOrderList;
    }

    public List<String> getGoodsOrderIdList() {
        return goodsOrderIdList;
    }

    public void setGoodsOrderIdList(List<String> goodsOrderIdList) {
        this.goodsOrderIdList = goodsOrderIdList;
    }

    public List<StoreAllot> getStoreAllotList() {
        return storeAllotList;
    }

    public void setStoreAllotList(List<StoreAllot> storeAllotList) {
        this.storeAllotList = storeAllotList;
    }

    public List<String> getStoreAllotIdList() {
        return storeAllotIdList;
    }

    public void setStoreAllotIdList(List<String> storeAllotIdList) {
        this.storeAllotIdList = storeAllotIdList;
    }

    public Depot getFromDepot() {
        return fromDepot;
    }

    public void setFromDepot(Depot fromDepot) {
        this.fromDepot = fromDepot;
    }

    public Depot getToDepot() {
        return toDepot;
    }

    public void setToDepot(Depot toDepot) {
        this.toDepot = toDepot;
    }
}
