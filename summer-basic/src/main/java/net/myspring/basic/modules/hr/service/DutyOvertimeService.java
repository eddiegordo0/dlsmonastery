package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.enums.AuditTypeEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.SecurityUtils;
import net.myspring.basic.modules.hr.domain.DutyOvertime;
import net.myspring.basic.modules.hr.dto.DutyOvertimeDto;
import net.myspring.basic.modules.hr.mapper.DutyOvertimeMapper;
import net.myspring.basic.modules.hr.web.form.DutyOvertimeForm;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanMapper;
import net.myspring.util.time.LocalDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.PAData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DutyOvertimeService {

    @Autowired
    private DutyOvertimeMapper dutyOvertimeMapper;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private SecurityUtils securityUtils;


    public Page<DutyOvertimeDto> findPage(Pageable pageable, Map<String, Object> map) {
        Page<DutyOvertime> page = dutyOvertimeMapper.findPage(pageable, map);
        Page<DutyOvertimeDto> dutyOvertimeDtoPage=BeanMapper.convertPage(page,DutyOvertimeDto.class);
        cacheUtils.initCacheInput(dutyOvertimeDtoPage.getContent());
        return dutyOvertimeDtoPage;
    }

    public DutyOvertime save(DutyOvertimeForm dutyOvertimeForm) {
        dutyOvertimeForm.setLeftHour(dutyOvertimeForm.getHour());
        dutyOvertimeForm.setStatus(AuditTypeEnum.APPLY.getValue());
        dutyOvertimeForm.setEmployeeId(securityUtils.getEmployeeId());
        dutyOvertimeMapper.save(dutyOvertimeForm);
        return dutyOvertimeForm;
    }

    public List<DutyOvertime> findByDutyDate(String employeeId, LocalDate dutyDate) {
        List<DutyOvertime> dutyOvertimes = dutyOvertimeMapper.findByDutyDate(employeeId, dutyDate);
        return dutyOvertimes;
    }

    public List<DutyOvertime> findByDutyDateAndStatus(String employeeId, LocalDate dutyDateStart, LocalDate dutyDateEnd, String status) {
        List<DutyOvertime> dutyOvertimes = dutyOvertimeMapper.findByDutyDateAndStatus(employeeId, dutyDateStart, dutyDateEnd, status);
        return dutyOvertimes;
    }

    public void logicDeleteOne(String id) {
        dutyOvertimeMapper.logicDeleteOne(id);
    }


    public DutyOvertime findOne(String id) {
        DutyOvertime dutyOvertime = dutyOvertimeMapper.findOne(id);
        return dutyOvertime;
    }

    public DutyOvertimeDto findDto(String id) {
        DutyOvertime dutyOvertime =findOne(id);
        DutyOvertimeDto dutyOvertimeDto= BeanMapper.convertDto(dutyOvertime,DutyOvertimeDto.class);
        cacheUtils.initCacheInput(dutyOvertimeDto);
        return dutyOvertimeDto;
    }

    public Double getAvailableHour(String employeeId, LocalDateTime currentDate) {
        Double overtimeHour = 0.0;
        if (currentDate == null) {
            return overtimeHour;
        }
        LocalDateTime dateStart = LocalDateTimeUtils.getFirstDayOfThisMonth(currentDate.minusMonths(3));
        LocalDateTime dateEnd = currentDate;
        List<DutyOvertime> overtimeList = dutyOvertimeMapper.findByIdAndDate(employeeId, dateStart, dateEnd, AuditTypeEnum.PASS.getValue());
        if (CollectionUtil.isNotEmpty(overtimeList)) {
            for (DutyOvertime dutyOvertime : overtimeList) {
                overtimeHour = overtimeHour + dutyOvertime.getLeftHour();
            }
        }
        return overtimeHour;
    }

    public Double getExpiredHour(String employeeId, LocalDateTime currentDate){
        Double overtimeHour = 0.0;
        if (currentDate == null) {
            return overtimeHour;
        }
        LocalDateTime dateStart = LocalDateTimeUtils.getFirstDayOfThisMonth(currentDate.minusMonths(3));
        LocalDateTime dateEnd =  LocalDateTimeUtils.getLastDayOfThisMonth(currentDate.minusMonths(3));
        List<DutyOvertime> overtimeList = dutyOvertimeMapper.findByIdAndDate(employeeId, dateStart, dateEnd, AuditTypeEnum.PASS.getValue());
        if (CollectionUtil.isNotEmpty(overtimeList)) {
            for (DutyOvertime dutyOvertime : overtimeList) {
                overtimeHour = overtimeHour + dutyOvertime.getLeftHour();
            }
        }
        return overtimeHour;
    }
}
