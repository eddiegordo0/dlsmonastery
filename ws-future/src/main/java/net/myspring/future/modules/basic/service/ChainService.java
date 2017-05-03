package net.myspring.future.modules.basic.service;

import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.domain.Chain;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.ChainDto;
import net.myspring.future.modules.basic.manager.ChainManager;
import net.myspring.future.modules.basic.mapper.ChainMapper;
import net.myspring.future.modules.basic.mapper.DepotMapper;
import net.myspring.future.modules.basic.web.query.ChainQuery;
import net.myspring.future.modules.basic.web.form.ChainForm;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ChainService {

    @Autowired
    private ChainMapper chainMapper;
    @Autowired
    private DepotMapper depotMapper;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private ChainManager chainManager;

    public Chain findOne(String id) {
        Chain chain = chainMapper.findOne(id);
        return chain;
    }

    public Page<ChainDto> findPage(Pageable pageable, ChainQuery chainQuery) {
        Page<ChainDto> page = chainMapper.findPage(pageable, chainQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public void delete(ChainForm chainForm) {
        chainMapper.logicDeleteOne(chainForm.getId());
    }

    public ChainForm findForm(ChainForm chainForm){
        if(!chainForm.isCreate()){
            Chain chain=chainManager.findOne(chainForm.getId());
            chainForm= BeanUtil.map(chain,ChainForm.class);
            cacheUtils.initCacheInput(chainForm);
        }
        List<Depot> depots = depotMapper.findByChainId(chainForm.getId());
        if(!depots.isEmpty()){
            List<String> depotName = new ArrayList<>();
            for(Depot depot : depots){
                depotName.add(depot.getName());
            }
            chainForm.setDepotIdList(depotName);
        }

        return chainForm;
    }

    public Chain save(ChainForm chainForm) {
        Chain chain;
        if(chainForm.isCreate()){
            chain=BeanUtil.map(chainForm,Chain.class);
            chain=chainManager.save(chain);
        }else{
            chain=chainManager.updateForm(chainForm);
        }

        List<Depot> depotListBefore = depotMapper.findByChainId(chain.getId());
        for(Depot depot:depotListBefore){
            depot.setChainId(null);
            depotMapper.update(depot);
        }

        List<Depot> depotListNow = depotMapper.findByIds(chainForm.getDepotIdList());
        for(Depot depot:depotListNow){
            depot.setChainId(chain.getId());
            depotMapper.update(depot);
        }
        return chain;
    }
}
