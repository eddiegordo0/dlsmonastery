package net.myspring.future.common.config;

import net.myspring.future.common.utils.SecurityUtils;
import net.myspring.mybatis.context.MybatisContext;
import net.myspring.mybatis.dialect.Dialect;
import net.myspring.mybatis.dialect.MySQLDialect;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuj on 2017/3/21.
 */
public class MybatisContextImpl implements MybatisContext {
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public String getAccountId() {
        return securityUtils.getAccountId();
    }

    @Override
    public Dialect getDialect() {
        return new MySQLDialect();
    }
}
