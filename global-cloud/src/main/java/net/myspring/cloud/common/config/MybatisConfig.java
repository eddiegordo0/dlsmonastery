package net.myspring.cloud.common.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.DynamicDataSource;
import net.myspring.cloud.common.enums.DataSourceTypeEnum;
import net.myspring.cloud.modules.sys.domain.Company;
import net.myspring.mybatis.context.ProviderMapperAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liuj on 2017/3/19.
 */
@Configuration
public class MybatisConfig {

    @Value("${mysql.url}")
    private String url;
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;

    @Bean
    public ProviderMapperAspect providerMapperAspect() {
        return new ProviderMapperAspect();
    }
    @Bean
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        List<Company> companyList = getCompanyList();
        for (Company company:companyList) {
            targetDataSources.put(DataSourceTypeEnum.KINGDEE.name() + "_" + company.getId(),getCloudDataSource(company));
        }
        targetDataSources.put(DataSourceTypeEnum.SYS.name(),getSysDataSource());
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        return dataSource;
    }

    private DataSource getSysDataSource() {
        Properties props = new Properties();
        props.put("driverClassName", "com.mysql.jdbc.Driver");
        props.put("url", url);
        props.put("username", username);
        props.put("password", password);
        try {
            return DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Company> getCompanyList() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getSysDataSource());
        String sql = "select * from sys_company";
        List<Company> companyList = jdbcTemplate.query(sql,new BeanPropertyRowMapper<Company>(Company.class));
        return companyList;
    }

    private DataSource getCloudDataSource(Company company) {
        Properties props = new Properties();
        props.put("driverClassName", "net.sourceforge.jtds.jdbc.Driver");
        props.put("url", company.getCloudUrl());
        props.put("username", company.getCloudUsername());
        props.put("password", company.getCloudPassword());
        try {
            return DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            return null;
        }
    }
}
