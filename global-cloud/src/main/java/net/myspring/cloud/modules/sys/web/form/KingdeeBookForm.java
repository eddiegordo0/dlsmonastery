package net.myspring.cloud.modules.sys.web.form;

import net.myspring.cloud.common.form.DataForm;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;

/**
 * Created by lihx on 2017/4/14.
 */
public class KingdeeBookForm extends DataForm<KingdeeBook> {
    private String companyId;
    private String name;
    private String type;
    private String kingdeeUrl;
    private String kingdeePostUrl;
    private String kingdeeUsername;
    private String kingdeePassword;
    private String kingdeeDbid;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKingdeeUrl() {
        return kingdeeUrl;
    }

    public void setKingdeeUrl(String kingdeeUrl) {
        this.kingdeeUrl = kingdeeUrl;
    }

    public String getKingdeePostUrl() {
        return kingdeePostUrl;
    }

    public void setKingdeePostUrl(String kingdeePostUrl) {
        this.kingdeePostUrl = kingdeePostUrl;
    }

    public String getKingdeeUsername() {
        return kingdeeUsername;
    }

    public void setKingdeeUsername(String kingdeeUsername) {
        this.kingdeeUsername = kingdeeUsername;
    }

    public String getKingdeePassword() {
        return kingdeePassword;
    }

    public void setKingdeePassword(String kingdeePassword) {
        this.kingdeePassword = kingdeePassword;
    }

    public String getKingdeeDbid() {
        return kingdeeDbid;
    }

    public void setKingdeeDbid(String kingdeeDbid) {
        this.kingdeeDbid = kingdeeDbid;
    }
}