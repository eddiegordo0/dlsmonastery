package net.myspring.tool.modules.imoo.domain;


import com.google.common.collect.Lists;
import net.myspring.common.domain.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name="imoo_plant_basic_product")
public class ImooPlantBasicProduct extends IdEntity<ImooPlantBasicProduct> {
    private String segment1;
    private String description;
    private String title;
    private String plid;
    private String plname;
    private String createdTime;
    private List<ImooProductMap> imooProductMapList = Lists.newArrayList();
    private List<String> imooProductMapIdList = Lists.newArrayList();

    public String getSegment1() {
        return segment1;
    }

    public void setSegment1(String segment1) {
        this.segment1 = segment1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlid() {
        return plid;
    }

    public void setPlid(String plid) {
        this.plid = plid;
    }

    public String getPlname() {
        return plname;
    }

    public void setPlname(String plname) {
        this.plname = plname;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<ImooProductMap> getImooProductMapList() {
        return imooProductMapList;
    }

    public void setImooProductMapList(List<ImooProductMap> imooProductMapList) {
        this.imooProductMapList = imooProductMapList;
    }

    public List<String> getImooProductMapIdList() {
        return imooProductMapIdList;
    }

    public void setImooProductMapIdList(List<String> imooProductMapIdList) {
        this.imooProductMapIdList = imooProductMapIdList;
    }
}