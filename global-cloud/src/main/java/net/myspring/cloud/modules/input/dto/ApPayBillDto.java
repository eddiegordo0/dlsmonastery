package net.myspring.cloud.modules.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 付款单
 * Created by lihx on 2017/6/20.
 */
public class ApPayBillDto {
    //创建人
    private String creatorK3;
    //供应商编码
    private String supplierNumber;
    //部门编码
    private String departmentNumber;
    // 日期
    private LocalDate date;
    // 金额
    private BigDecimal amount;

    private ApPayBillFEntryDto apPayBillFEntryDto;

    public String getCreatorK3() {
        return creatorK3;
    }

    public void setCreatorK3(String creatorK3) {
        this.creatorK3 = creatorK3;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ApPayBillFEntryDto getApPayBillFEntryDto() {
        return apPayBillFEntryDto;
    }

    public void setApPayBillFEntryDto(ApPayBillFEntryDto apPayBillFEntryDto) {
        this.apPayBillFEntryDto = apPayBillFEntryDto;
    }

    @JsonIgnore
    public String getJson() {
        Map<String, Object> root = Maps.newLinkedHashMap();
        root.put("Creator", getCreatorK3());
        root.put("NeedUpDateFields", Lists.newArrayList());
        Map<String, Object> model = Maps.newLinkedHashMap();
        model.put("FID", 0);
        model.put("FDate", getDate());
        model.put("FBillTypeID", CollectionUtil.getMap("FNumber", "FKDLX01_SYS"));
        model.put("FCONTACTUNITTYPE", "BD_Supplier");
        model.put("FCONTACTUNIT", CollectionUtil.getMap("FNumber", getSupplierNumber()));
        model.put("FRECTUNITTYPE", "BD_Supplier");
        model.put("FRECTUNIT", CollectionUtil.getMap("FNumber", getSupplierNumber()));
        model.put("FCURRENCYID", CollectionUtil.getMap("FNumber", "PRE001"));
        model.put("FSETTLECUR", CollectionUtil.getMap("FNumber", "PRE001"));
        model.put("FSETTLERATE", 1);
        model.put("FPAYORGID", CollectionUtil.getMap("FNumber", "100"));
        model.put("FSETTLEORGID", CollectionUtil.getMap("FNumber", "100"));
        model.put("FPURCHASEDEPTID", CollectionUtil.getMap("FNumber", getDepartmentNumber()));
        model.put("FPAYTOTALAMOUNTFOR_H", getAmount());
        model.put("FREALPAYAMOUNTFOR_H", getAmount());
        model.put("FEXCHANGERATE", 1);

        List<Object> entity = Lists.newArrayList();
        Map<String, Object> detail = Maps.newLinkedHashMap();
        detail.put("FSETTLETYPEID", CollectionUtil.getMap("FNumber", getApPayBillFEntryDto().getSettleTypeNumber()));
        if (StringUtils.isNotBlank(getApPayBillFEntryDto().getBankAcntNumber())) {
            detail.put("FACCOUNTID", CollectionUtil.getMap("FNumber", getApPayBillFEntryDto().getBankAcntNumber()));
        }
        detail.put("FPAYTOTALAMOUNTFOR", getAmount());
        detail.put("FREALPAYAMOUNTFOR_D", getAmount());
        detail.put("FSETTLEPAYAMOUNTFOR", getAmount());
        detail.put("F_YLG_Base", CollectionUtil.getMap("FNumber", getApPayBillFEntryDto().getAccountNumber()));
        detail.put("FCOMMENT", getApPayBillFEntryDto().getComment());
        entity.add(detail);
        model.put("FPAYBILLENTRY", entity);
        root.put("Model", model);
        String result = ObjectMapperUtils.writeValueAsString(root);
        System.out.println(result);
        return result;
    }
}