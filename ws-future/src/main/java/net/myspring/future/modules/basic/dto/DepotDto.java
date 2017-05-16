package net.myspring.future.modules.basic.dto;

import net.myspring.common.constant.CharConstant;
import net.myspring.common.dto.DataDto;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.util.cahe.annotation.CacheInput;

import java.math.BigDecimal;

/**
 * Created by lihx on 2017/4/17.
 */
public class DepotDto extends DataDto<Depot> {
    private String code;
    // 名称
    private String name;
    // 拼音
    private String namePinyin;
    private String depotType;

    private String clientId;
    @CacheInput(inputKey = "clients",inputInstance = "clientId",outputInstance = "name")
    private String clientName;
    private String pricesystemId;
    @CacheInput(inputKey = "pricesystems",inputInstance = "pricesystemId",outputInstance = "name")
    private String pricesystemName;

    private String contator;
    private String address;
    private String mobilePhone;
    private String areaId;
    private BigDecimal credit;
    private String areaType;

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public Boolean isDepositStore(){

        //TODO 是否定金門店
//        String isNotDepotStoreId=Global.getCompanyConfig(AccountUtils.getCompany().getId(),CompanyConfigCode.IS_NOT_DEPOSIT_STORE.getCode());
//        if(store!=null&& StringUtils.isNotBlank(isNotDepotStoreId)){
//            List<Long> storeIds= IdUtils.getIdList(isNotDepotStoreId);
//            if(storeIds.contains(store.getId())){
//                model.addAttribute("isNotDepotStore",true);
//            }
//        }
        return Boolean.TRUE;
    }
    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPricesystemId() {
        return pricesystemId;
    }

    public void setPricesystemId(String pricesystemId) {
        this.pricesystemId = pricesystemId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDepotType() {
        return depotType;
    }

    public void setDepotType(String depotType) {
        this.depotType = depotType;
    }

    public String getPricesystemName() {
        return pricesystemName;
    }

    public void setPricesystemName(String pricesystemName) {
        this.pricesystemName = pricesystemName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getFullName() {
        return namePinyin + CharConstant.SPACE + name + CharConstant.SPACE + code;
    }
}
