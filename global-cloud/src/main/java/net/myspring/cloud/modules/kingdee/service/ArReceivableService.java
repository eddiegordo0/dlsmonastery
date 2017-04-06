package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.mapper.ArReceivableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lihx on 2017/4/6.
 */
@Service
@KingdeeDataSource
public class ArReceivableService {
    @Autowired
    private ArReceivableMapper arReceivableMapper;

    public String findFBillNoByfSourceBillNo(String outStockBillNo){
        return arReceivableMapper.findFBillNoByfSourceBillNo(outStockBillNo);
    }
}
