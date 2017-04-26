package net.myspring.mybatis.context;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.mybatis.dto.ColumnDto;
import net.myspring.mybatis.dto.TableDto;
import net.myspring.mybatis.form.BaseForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liuj on 2017/3/17.
 */
public class ProviderContextUtils {
    //表结构缓存
    private static Map<String, TableDto> tableDtoMap = Maps.newHashMap();
    //字段缓存
    private static Map<String, ColumnDto> columnDtoMap = Maps.newHashMap();
    //class对应的entityClass
    private static Map<String, Class> entityClassMap = Maps.newHashMap();

    public static TableDto getTableDto(Class clazz) {
        clazz = getEntityClass(clazz);
        String key = clazz.getName();
        if (!tableDtoMap.containsKey(key)) {
            Entity entity = (Entity) clazz.getAnnotation(Entity.class);
            Table table = (Table) clazz.getAnnotation(Table.class);
            if (entity != null && table != null) {
                TableDto tableDto = new TableDto();
                //获取表名
                String jdbcTable = table.name();
                if (StringUtils.isBlank(jdbcTable)) {
                    jdbcTable = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
                }
                tableDto.setJdbcTable(jdbcTable);
                List<Field> fields = Lists.newArrayList();
                getFields(fields, clazz);
                for (Field field : fields) {
                    ColumnDto columnDto = getColumnDto(field);
                    if (isJdbcColumn(field)) {
                        //检查是否是ID
                        if (field.getAnnotation(Id.class) != null) {
                            tableDto.setIdColumn(columnDto);
                        }
                        if (field.getAnnotation(CreatedBy.class) != null) {
                            tableDto.setCreatedByColumn(columnDto);
                        }
                        if (field.getAnnotation(CreatedDate.class) != null) {
                            tableDto.setCreatedDateColumn(columnDto);
                        }
                        if (field.getAnnotation(LastModifiedBy.class) != null) {
                            tableDto.setLastModifiedByColumn(columnDto);
                        }
                        if (field.getAnnotation(LastModifiedDate.class) != null) {
                            tableDto.setLastModifiedDateColumn(columnDto);
                        }
                        if (field.getAnnotation(Version.class) != null) {
                            tableDto.setVersionColumn(columnDto);
                        }
                        columnDto.setTableDto(tableDto);
                        tableDto.getColumnList().add(columnDto);
                    }
                    if(field.getAnnotation(AutoAuditing.class) != null) {
                        tableDto.setAutoAuditingColumn(columnDto);
                    }
                }
                tableDtoMap.put(key, tableDto);
            }
        }
        return tableDtoMap.get(key);
    }

    private static Boolean isJdbcColumn(Field field) {
        Class fieldClass = field.getType();
        Boolean isJdbcColumn = true;
        if ("serialVersionUID".equals(field.getName())) {
            isJdbcColumn = false;
        } else if (field.getAnnotation(Transient.class) != null || fieldClass.getAnnotation(Entity.class) != null) {
            isJdbcColumn = false;
        } else if (Collection.class.isAssignableFrom(fieldClass) || Map.class.isAssignableFrom(fieldClass)) {
            isJdbcColumn = false;
        }
        return isJdbcColumn;
    }

    public static MybatisContext getMybatisContext(Properties properties) {
        MybatisContext mybatisContext = null;
        try {
            mybatisContext = (MybatisContext) Class.forName(properties.getProperty("mybatisContext")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mybatisContext;
    }

    public static Class getEntityClass(Class clazz) {
        String key = clazz.getName();
        if (!entityClassMap.containsKey(key)) {
            Class entityClass = null;
            Annotation annotation = clazz.getAnnotation(Entity.class);
            if (annotation != null) {
                entityClass = clazz;
            } else {
                if (BaseForm.class.isAssignableFrom(clazz)) {
                    Type type = clazz.getGenericSuperclass();
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    try {
                        entityClass = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            entityClassMap.put(key, entityClass);
        }
        return entityClassMap.get(key);
    }


    private static ColumnDto getColumnDto(Field field) {
        String key = field.toString();
        if (!columnDtoMap.containsKey(key)) {
            ColumnDto columnDto = new ColumnDto();
            columnDto.setJavaInstance(field.getName());
            String jdbcColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            boolean insertable = true;
            boolean updatable = true;
            boolean nullable = false;
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (StringUtils.isNotBlank(column.name())) {
                    jdbcColumn = column.name();
                }
                insertable = column.insertable();
                updatable = column.updatable();
                nullable = column.nullable();
            }
            if (field.getAnnotation(Id.class) != null) {
                columnDto.setGeneratedValue(field.getAnnotation(GeneratedValue.class));
            }
            columnDto.setJdbcColumn(jdbcColumn);
            columnDto.setInsertable(insertable);
            columnDto.setUpdatable(updatable);
            columnDto.setNullable(nullable);
            columnDtoMap.put(key, columnDto);
        }
        return columnDtoMap.get(key);
    }

    //递归获取所有Field
    private static void getFields(List<Field> fields, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }
        clazz = clazz.getSuperclass();
        if (!clazz.getName().equals(Object.class.getName())) {
            getFields(fields, clazz);
        }
    }
}
