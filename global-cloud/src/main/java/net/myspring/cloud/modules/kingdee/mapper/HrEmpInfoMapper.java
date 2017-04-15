package net.myspring.cloud.modules.kingdee.mapper;

import net.myspring.cloud.modules.kingdee.domain.HrEmpInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lihx on 2017/4/6.
 */
@Mapper
public interface HrEmpInfoMapper {

    String findFNumberByName(@Param("name")String Name);

    List<HrEmpInfo> findAllUser();
}