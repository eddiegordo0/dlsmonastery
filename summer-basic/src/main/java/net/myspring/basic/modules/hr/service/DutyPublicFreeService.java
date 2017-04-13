package net.myspring.basic.modules.hr.service;

import net.myspring.basic.common.enums.AuditTypeEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.SecurityUtils;
import net.myspring.basic.modules.hr.domain.DutyPublicFree;
import net.myspring.basic.modules.hr.dto.DutyPublicFreeDto;
import net.myspring.basic.modules.hr.mapper.DutyPublicFreeMapper;
import net.myspring.basic.modules.hr.web.form.DutyPublicFreeForm;
import net.myspring.basic.modules.hr.web.query.DutyPublicFreeQuery;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DutyPublicFreeService {

    @Autowired
    private DutyPublicFreeMapper dutyPublicFreeMapper;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private SecurityUtils securityUtils;


    public DutyPublicFreeForm save(DutyPublicFreeForm dutyPublicFreeForm) {
        dutyPublicFreeForm.setEmployeeId(securityUtils.getEmployeeId());
        dutyPublicFreeForm.setStatus(AuditTypeEnum.APPLY.getValue());
        dutyPublicFreeMapper.save(BeanUtil.map(dutyPublicFreeForm,DutyPublicFree.class));
        return dutyPublicFreeForm;
    }

    public Page<DutyPublicFreeDto> findPage(Pageable pageable, DutyPublicFreeQuery dutyPublicFreeQuery) {
        Page<DutyPublicFree> page = dutyPublicFreeMapper.findPage(pageable,dutyPublicFreeQuery);
        Page<DutyPublicFreeDto> dutyPublicFreeDtoPage= BeanUtil.map(page,DutyPublicFreeDto.class);
        cacheUtils.initCacheInput(dutyPublicFreeDtoPage.getContent());
        return dutyPublicFreeDtoPage;
    }

    public void logicDeleteOne(String id) {
        dutyPublicFreeMapper.logicDeleteOne(id);
    }

    public DutyPublicFree findOne(String id) {
        DutyPublicFree dutyPublicFree = dutyPublicFreeMapper.findOne(id);
        return dutyPublicFree;
    }

    public DutyPublicFreeDto findDto(String id) {
        DutyPublicFree dutyPublicFree =findOne(id);
        DutyPublicFreeDto publicFreeDto= BeanUtil.map(dutyPublicFree,DutyPublicFreeDto.class);
        return publicFreeDto;
    }
}
