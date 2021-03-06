package net.myspring.future.modules.crm.web.query;

import net.myspring.future.common.query.BaseQuery;

import java.util.List;

/**
 * Created by zhangyf on 2017/5/15.
 */
public class PriceChangeImeQuery extends BaseQuery {
    private String status;
    private Boolean isCheck;
    private Boolean hasImage;
    private String shopId;
    private String ime;
    private String officeId;
    private String priceChangeName;
    private String productId;
    private String createdBy;

    private List<String> statusList;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getPriceChangeName() {
        return priceChangeName;
    }

    public void setPriceChangeName(String priceChangeName) {
        this.priceChangeName = priceChangeName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
