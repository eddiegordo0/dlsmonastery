package net.myspring.future.modules.crm.web.query;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.query.BaseQuery;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by wangzm on 2017/6/7.
 */
public class ProductImeReportQuery {
    //销售，库存
    private String type;
    //区域，促销，型号
    private String sumType ;
    //电子保卡，核销
    private String outType;
    //区域类型
    private String areaType;
    //日期范围
    private String dateRange;
    private LocalDate date;
    //乡镇类型
    private String townType;
    //打分型号：是，否
    private boolean scoreType;
    //货品
    private List<String> productTypeIdList= Lists.newArrayList();
    private String officeId;
    private List<String> officeIdList=Lists.newArrayList();

    private List<String> sumTypeList=Lists.newArrayList();
    private List<String> outTypeList=Lists.newArrayList();
    private List<String> areaTypeList=Lists.newArrayList();
    private List<String> townTypeList=Lists.newArrayList();

    public List<String> getSumTypeList() {
        return sumTypeList;
    }

    public void setSumTypeList(List<String> sumTypeList) {
        this.sumTypeList = sumTypeList;
    }

    public List<String> getOutTypeList() {
        return outTypeList;
    }

    public void setOutTypeList(List<String> outTypeList) {
        this.outTypeList = outTypeList;
    }

    public List<String> getAreaTypeList() {
        return areaTypeList;
    }

    public void setAreaTypeList(List<String> areaTypeList) {
        this.areaTypeList = areaTypeList;
    }

    public List<String> getTownTypeList() {
        return townTypeList;
    }

    public void setTownTypeList(List<String> townTypeList) {
        this.townTypeList = townTypeList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOfficeIdList() {
        return officeIdList;
    }

    public void setOfficeIdList(List<String> officeIdList) {
        this.officeIdList = officeIdList;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getSumType() {
        return sumType;
    }

    public void setSumType(String sumType) {
        this.sumType = sumType;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getTownType() {
        return townType;
    }

    public void setTownType(String townType) {
        this.townType = townType;
    }

    public boolean getScoreType() {
        return scoreType;
    }

    public boolean isScoreType() {
        return scoreType;
    }

    public void setScoreType(boolean scoreType) {
        this.scoreType = scoreType;
    }

    public List<String> getProductTypeIdList() {
        return productTypeIdList;
    }

    public void setProductTypeIdList(List<String> productTypeIdList) {
        this.productTypeIdList = productTypeIdList;
    }

    public LocalDate getDateStart() {
        if(StringUtils.isNotBlank(getDateRange())) {
            return LocalDateUtils.parse(getDateRange().split(CharConstant.WAVE_LINE)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getDateEnd() {
        if(StringUtils.isNotBlank(getDateRange())) {
            return LocalDateUtils.parse(getDateRange().split(CharConstant.WAVE_LINE)[1]).plusDays(1);
        } else {
            return null;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
