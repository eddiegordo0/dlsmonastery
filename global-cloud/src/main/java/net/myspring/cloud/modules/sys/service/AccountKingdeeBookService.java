package net.myspring.cloud.modules.sys.service;

import net.myspring.cloud.common.dataSource.annotation.LocalDataSource;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.dto.AccountKingdeeBookDto;
import net.myspring.cloud.modules.sys.repository.AccountKingdeeBookRepository;
import net.myspring.cloud.modules.sys.repository.KingdeeBookRepository;
import net.myspring.cloud.modules.sys.web.form.AccountKingdeeBookForm;
import net.myspring.cloud.modules.sys.web.query.AccountKingdeeBookQuery;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lihx on 2017/5/9.
 */
@Service
@LocalDataSource
public class AccountKingdeeBookService {
    @Autowired
    private AccountKingdeeBookRepository accountKingdeeBookRepository;
    @Autowired
    private KingdeeBookRepository kingdeeBookRepository;

    public Page<AccountKingdeeBookDto> findPage(Pageable pageable, AccountKingdeeBookQuery accountKingdeeBookQuery){
        Page<AccountKingdeeBookDto> page = accountKingdeeBookRepository.findPage(pageable,accountKingdeeBookQuery);
        return page;
    }

    public AccountKingdeeBook findByAccountId(String accountId){
        return accountKingdeeBookRepository.findByAccountId(accountId);
    }

    public AccountKingdeeBookQuery getQueryProperty(){
        AccountKingdeeBookQuery accountKingdeeBookQuery = new AccountKingdeeBookQuery();
        List<String> nameList = kingdeeBookRepository.findNames();
        List<String> typeList = kingdeeBookRepository.findTypes();
        accountKingdeeBookQuery.setKingdeeBookNameList(nameList);
        accountKingdeeBookQuery.setKingdeeBookTypeList(typeList);
        return accountKingdeeBookQuery;
    }

    public AccountKingdeeBookForm getForm(AccountKingdeeBookForm accountKingdeeBookForm){
        if (StringUtils.isNotBlank(accountKingdeeBookForm.getId())){
            AccountKingdeeBook accountKingdeeBook =  accountKingdeeBookRepository.findOne(accountKingdeeBookForm.getId());
            accountKingdeeBookForm = BeanUtil.map(accountKingdeeBook,AccountKingdeeBookForm.class);
        }
        return accountKingdeeBookForm;
    }

    public AccountKingdeeBook save(AccountKingdeeBookForm accountKingdeeBookForm){
        AccountKingdeeBook accountKingdeeBook;
        if (accountKingdeeBookForm.isCreate()){
            accountKingdeeBook = BeanUtil.map(accountKingdeeBookForm,AccountKingdeeBook.class);
            accountKingdeeBookRepository.save(accountKingdeeBook);
        }else{
            accountKingdeeBook = accountKingdeeBookRepository.findOne(accountKingdeeBookForm.getId());
            ReflectionUtil.copyProperties(accountKingdeeBookForm,accountKingdeeBook);
            accountKingdeeBookRepository.save(accountKingdeeBook);
        }
        return accountKingdeeBook;
    }

    public void logicDelete(String id) {
        accountKingdeeBookRepository.logicDelete(id);
    }
}
