package net.myspring.basic.modules.hr.web.query;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.hr.dto.PositionDto;
import org.joda.time.LocalDateTime;

import java.util.List;

/**
 * Created by lihx on 2017/4/7.
 */
public class DutySignQuery {
    private LocalDateTime dutyDateStart;
    private LocalDateTime dutyDateEnd;
    private String createdBy;
    private String address;
    private String employeeName;
    private String officeName;
    private String positionName;
    private List<String> officeIds;
    private List<Office> officeList= Lists.newArrayList();
    private List<PositionDto> positionList= Lists.newArrayList();

    public List<Office> getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List<Office> officeList) {
        this.officeList = officeList;
    }

    public List<PositionDto> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PositionDto> positionList) {
        this.positionList = positionList;
    }

    public LocalDateTime getDutyDateStart() {
        return dutyDateStart;
    }

    public void setDutyDateStart(LocalDateTime dutyDateStart) {
        if(dutyDateStart == null) {
            this.dutyDateStart = LocalDateTime.now().minusMonths(1);
        }else{
            this.dutyDateStart = dutyDateStart;
        }
    }

    public LocalDateTime getDutyDateEnd() {
        return dutyDateEnd;
    }

    public void setDutyDateEnd(LocalDateTime dutyDateEnd) {
        if(dutyDateEnd == null){
            this.dutyDateEnd = LocalDateTime.now();
        }else{
            this.dutyDateEnd = dutyDateEnd;
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public List<String> getOfficeIds() {
        return officeIds;
    }

    public void setOfficeIds(List<String> officeIds) {
        this.officeIds = officeIds;
    }
}
