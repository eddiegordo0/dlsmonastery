package net.myspring.basic.modules.hr.repository

import net.myspring.basic.common.config.MyBeanPropertyRowMapper
import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.hr.domain.DutyFree
import net.myspring.basic.modules.hr.dto.DutyDto
import net.myspring.basic.modules.hr.dto.DutyFreeDto
import net.myspring.basic.modules.hr.web.query.DutyFreeQuery
import net.myspring.util.collection.CollectionUtil
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.persistence.EntityManager

/**
 * Created by lihx on 2017/5/24.
 */
interface DutyFreeRepository : BaseRepository<DutyFree, String>, DutyFreeRepositoryCustom {

    @Query("""
        SELECT
            t1.*
            FROM #{#entityName} t1
            WHERE
             t1.enabled=1
            AND  t1.freeDate=?1
            and t1.employeeId=?2
    """)
    fun findByDate(freeDate: LocalDate, employeeId: String): MutableList<DutyFree>

    @Query("""
        SELECT
            t1.*
            FROM  #{#entityName} t1
            WHERE
            t1.enabled=1
            AND t1.freeDate >= ?1
            and t1.freeDate <= ?2
            and t1.status in ?3
        """)
    fun findByDateAndStatusList(dateStart: LocalDate, dateEnd: LocalDate, statusList: MutableList<String>): MutableList<DutyFree>

    @Query("""
        SELECT
            t1.*
            FROM  #{#entityName} t1
            WHERE
            t1.enabled=1
            and t1.employeeId=?1
            and t1.freeDate >=?2
            and t1.freeDate <=?3
    """)
    fun findByEmployeeAndDate(employeeId: String, dateStart: LocalDate, dateEnd: LocalDate): MutableList<DutyFree>
}

interface DutyFreeRepositoryCustom {
    fun findByAuditable(leaderId: String, status: String, dateStart: LocalDateTime): MutableList<DutyDto>

    fun findByAccountIdAndDutyDate(dateStart: LocalDate, dateEnd: LocalDate, accountIds: MutableList<Long>): MutableList<DutyFree>

    fun findPage(pageable: Pageable, dutyFreeQuery: DutyFreeQuery): Page<DutyFreeDto>
}

class DutyFreeRepositoryImpl @Autowired constructor(val jdbcTemplate: JdbcTemplate, val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : DutyFreeRepositoryCustom {
    override fun findByAuditable(leaderId: String, status: String, dateStart: LocalDateTime): MutableList<DutyDto> {
        var paramMap = HashMap<String,Any>()
        paramMap.put("leaderId", leaderId)
        paramMap.put("status", status)
        paramMap.put("dateStart", dateStart)
        return namedParameterJdbcTemplate.query("""
            SELECT
            '免打卡' as dutyType,
            t1.free_date AS dutyDate,
            t1.remarks,
            t2.login_name as 'account.loginName',
            t2.leader_id AS 'account.leaderId' ,
            'MDK' AS 'prefix',
            t1.id
            FROM
            hr_duty_free t1 ,
            hr_account t2 ,
            hr_employee t3
            WHERE
            t1.enabled=1
            AND t1.employee_id=t3.id
            and t3.account_id=t2.id
            AND t2.leader_id= :leaderId
            AND t1.status= :status
            AND t1.created_date>= :dateStart
        """, paramMap, MyBeanPropertyRowMapper(DutyDto::class.java))
    }

    override fun findPage(pageable: Pageable, dutyFreeQuery: DutyFreeQuery): Page<DutyFreeDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByAccountIdAndDutyDate(dateStart: LocalDate, dateEnd: LocalDate, accountIds: MutableList<Long>): MutableList<DutyFree> {
        var sb = StringBuilder()
        sb.append("""
            SELECT
            fr.employee_id,
            fr.free_date as 'duty_date',
            fr.date_type
            FROM
            hr_duty_free fr
            WHERE
            fr.free_date >= :dateStart
            AND fr.free_date <= :dateEnd
        """);
        if (CollectionUtil.isNotEmpty(accountIds)) {
            sb.append(" and fr.employee_id in (:accountIds)")
        }
        var paramMap = HashMap<String, Any>()
        paramMap.put("dateStart", dateStart)
        paramMap.put("dateEnd", dateEnd)
        paramMap.put("accountIds", accountIds)
        return namedParameterJdbcTemplate.query(sb.toString(), paramMap, MyBeanPropertyRowMapper(DutyFree::class.java))
    }

}