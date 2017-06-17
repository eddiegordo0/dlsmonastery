package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.domain.CnBankAcnt;
import net.myspring.cloud.modules.kingdee.repository.CnBankAcntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 银行账户
 * Created by lihx on 2017/6/16.
 */
@Service
@KingdeeDataSource
public class CnBankAcntService {
    @Autowired
    private CnBankAcntRepository cnBankAcntRepository;

    public List<CnBankAcnt> findAll(){
        return cnBankAcntRepository.findAll();
    }
}