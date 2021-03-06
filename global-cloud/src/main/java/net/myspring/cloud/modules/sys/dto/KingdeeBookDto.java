package net.myspring.cloud.modules.sys.dto;


import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.common.dto.DataDto;

/**
 * Created by lihx on 2017/4/5.
 */
public class KingdeeBookDto extends DataDto<KingdeeBook> {
    private String companyName;
    private String name;
    private String type;
    private String kingdeeUrl;
    private String kingdeePostUrl;
    private String kingdeeDbid;
    private Integer version;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getKingdeeDbid() {
        return kingdeeDbid;
    }

    public void setKingdeeDbid(String kingdeeDbid) {
        this.kingdeeDbid = kingdeeDbid;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
