package net.myspring.cloud.modules.kingdee.web.controller;

import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.kingdee.service.BdMaterialService;
import net.myspring.util.time.LocalDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by lihx on 2017/5/12.
 */
@RestController
@RequestMapping(value = "kingdee/bdMaterial")
public class BdMaterialController {
    @Autowired
    private BdMaterialService bdMaterialService;

    @RequestMapping(value = "findByName")
    public BdMaterial findByName(String name){
        return bdMaterialService.findByName(name);
    }

    @RequestMapping(value = "findByNumber")
    public BdMaterial findByNumber(String number){
        return bdMaterialService.findByNumber(number);
    }

    @RequestMapping(value = "findAll")
    public List<BdMaterial> findAll( ){
        return bdMaterialService.findAll();
    }

    @RequestMapping(value = "findByMaxModifyDate")
    public List<BdMaterial> findByMaxModifyDate(String modifyDate){
        LocalDateTime maxModifyDate = LocalDateTimeUtils.parse(modifyDate,"yyyy-MM-dd HH:mm:ss");
        return bdMaterialService.findByMaxModifyDate(maxModifyDate);
    }
}
