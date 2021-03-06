package net.myspring.future.modules.layout.domain;

import net.myspring.future.common.domain.DataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name="crm_ad_goods_order")
public class AdGoodsOrder extends DataEntity<AdGoodsOrder> {
    private String storeId;
    private String outShopId;
    private String shopId;
    private BigDecimal amount;
    private String outCode;
    private String billType;
    private LocalDate billDate;
    private String billRemarks;
    private Integer smallQty;
    private Integer mediumQty;
    private Integer largeQty;
    private Integer version = 0;
    private String processInstanceId;
    private String simpleProcessId;
    private String processStatus;
    private String businessId;
    private Boolean splitBill;
    private Boolean isUrgent;

    private String expressOrderId;
    private String employeeId;
    private String processTypeId;
    private String processFlowId;
    private String cloudSynId;

    private String parentId;

    private String processPositionId;
    private String investInCause;
    private String billAddress;

    public String getBillAddress() {
        return billAddress;
    }

    public void setBillAddress(String billAddress) {
        this.billAddress = billAddress;
    }

    public String getInvestInCause() {
        return investInCause;
    }

    public void setInvestInCause(String investInCause) {
        this.investInCause = investInCause;
    }

    public String getProcessPositionId() {
        return processPositionId;
    }

    public void setProcessPositionId(String processPositionId) {
        this.processPositionId = processPositionId;
    }

    public String getSimpleProcessId() {
        return simpleProcessId;
    }

    public void setSimpleProcessId(String simpleProcessId) {
        this.simpleProcessId = simpleProcessId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getOutShopId() {
        return outShopId;
    }

    public void setOutShopId(String outShopId) {
        this.outShopId = outShopId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOutCode() {
        return outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getBillRemarks() {
        return billRemarks;
    }

    public void setBillRemarks(String billRemarks) {
        this.billRemarks = billRemarks;
    }

    public Integer getSmallQty() {
        return smallQty;
    }

    public void setSmallQty(Integer smallQty) {
        this.smallQty = smallQty;
    }

    public Integer getMediumQty() {
        return mediumQty;
    }

    public void setMediumQty(Integer mediumQty) {
        this.mediumQty = mediumQty;
    }

    public Integer getLargeQty() {
        return largeQty;
    }

    public void setLargeQty(Integer largeQty) {
        this.largeQty = largeQty;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Boolean getSplitBill() {
        return splitBill;
    }

    public void setSplitBill(Boolean splitBill) {
        this.splitBill = splitBill;
    }

    public Boolean getUrgent() {
        return isUrgent;
    }

    public void setUrgent(Boolean urgent) {
        isUrgent = urgent;
    }

    public String getExpressOrderId() {
        return expressOrderId;
    }

    public void setExpressOrderId(String expressOrderId) {
        this.expressOrderId = expressOrderId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(String processTypeId) {
        this.processTypeId = processTypeId;
    }

    public String getProcessFlowId() {
        return processFlowId;
    }

    public void setProcessFlowId(String processFlowId) {
        this.processFlowId = processFlowId;
    }

    public String getCloudSynId() {
        return cloudSynId;
    }

    public void setCloudSynId(String cloudSynId) {
        this.cloudSynId = cloudSynId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

}
