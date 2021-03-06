package net.myspring.basic.modules.sys.web.form;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.MenuCategory;
import net.myspring.common.form.BaseForm;
import net.myspring.basic.modules.sys.dto.BackendModuleDto;

import java.util.List;

/**
 * Created by admin on 2017/4/6.
 */

public class MenuCategoryForm extends BaseForm<MenuCategory> {
    private String name;
    private Integer sort;
    private String remarks;
    private String code;
    private String icon;
    private String backendModuleId;
    private List<BackendModuleDto> backendModuleList= Lists.newArrayList();

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBackendModuleId() {
        return backendModuleId;
    }

    public void setBackendModuleId(String backendModuleId) {
        this.backendModuleId = backendModuleId;
    }

    public List<BackendModuleDto> getBackendModuleList() {
        return backendModuleList;
    }

    public void setBackendModuleList(List<BackendModuleDto> backendModuleList) {
        this.backendModuleList = backendModuleList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
