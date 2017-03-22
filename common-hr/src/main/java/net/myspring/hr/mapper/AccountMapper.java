package net.myspring.hr.mapper;

import net.myspring.hr.domain.Account;
import net.myspring.mybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuj on 2017/3/19.
 */
@Mapper
public interface AccountMapper extends CrudMapper<Account,String> {
}
