package net.myspring.tool.common.domain;

import net.myspring.common.domain.DataEntity;
import net.myspring.tool.common.utils.SecurityUtils;

import javax.persistence.Column;

/**
 * Created by liuj on 2017/4/12.
 */
public class CompanyEntity<T> extends DataEntity<T> {

    @Column(updatable = false)
    private String companyId= SecurityUtils.getCompanyId();

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

}
