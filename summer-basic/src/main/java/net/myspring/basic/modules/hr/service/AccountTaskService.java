package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.AccountTask;
import net.myspring.basic.modules.hr.dto.AccountTaskDto;
import net.myspring.basic.modules.hr.mapper.AccountTaskMapper;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanMapper;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AccountTaskService {

    @Autowired
    private AccountTaskMapper accountTaskMapper;
    @Autowired
    private CacheUtils cacheUtils;

    public AccountTask findByNameAndExtendId(String name, String extendId){
        return accountTaskMapper.findByNameAndExtendId(name,extendId);
    }

    public Page<AccountTaskDto> findPage(Pageable pageable, Map<String,Object> paramMap){
        Page<AccountTask> page=accountTaskMapper.findPage(pageable,paramMap);
        Page<AccountTaskDto> accountTaskDtoPage= BeanMapper.convertPage(page,AccountTaskDto.class);
        cacheUtils.initCacheInput(accountTaskDtoPage.getContent());
        return accountTaskDtoPage;
    }

    public List<AccountTaskDto> findByPositionAndOfficeIds(String positionId,List<String> officeIds){
        List<AccountTaskDto> accountTaskDtoList= Lists.newArrayList();
        if(CollectionUtil.isNotEmpty(officeIds)&& StringUtils.isNotBlank(positionId)){
            List<AccountTask> accountTasks=accountTaskMapper.findByPositionAndOfficeIds(positionId,officeIds);
            accountTaskDtoList=BeanMapper.convertDtoList(accountTasks,AccountTaskDto.class);
        }
        return accountTaskDtoList;
    }

    public void logicDeleteOne(String id) {
        accountTaskMapper.logicDeleteOne(id);
    }
}
