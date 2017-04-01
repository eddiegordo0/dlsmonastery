package net.myspring.basic.modules.sys.mapper;

import net.myspring.basic.common.mybatis.MyMapper;
import net.myspring.basic.modules.sys.domain.DictMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Mapper
public interface DictMapMapper extends MyMapper<DictMap,String> {

    List<String> findDistinctCategory();

    Page<DictMap> findPage(Pageable pageable, @Param("p")Map<String,Object> map);


}
