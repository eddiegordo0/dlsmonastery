package net.myspring.basic.modules.hr.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.hr.domain.AuditFile
import net.myspring.basic.modules.hr.dto.AuditFileDto
import net.myspring.basic.modules.hr.web.query.AuditFileQuery
import net.myspring.util.collection.CollectionUtil
import net.myspring.util.repository.MySQLDialect
import net.myspring.util.text.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/**
 * Created by lihx on 2017/5/24.
 */
interface AuditFileRepository : BaseRepository<AuditFile,String>,AuditFileRepositoryCustom{

}
interface AuditFileRepositoryCustom{
    fun findPage(pageable: Pageable, auditFileQuery: AuditFileQuery): Page<AuditFileDto>

}
class AuditFileRepositoryImpl @Autowired constructor(val jdbcTemplate: JdbcTemplate, val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) :AuditFileRepositoryCustom{
    override fun findPage(pageable: Pageable, auditFileQuery: AuditFileQuery): Page<AuditFileDto> {
        var sb = StringBuilder()
        sb.append("""
            SELECT
            t1.*,office.name as officeName,area.name as areaName,t2.id  as auditFileCollectId
            FROM hr_audit_file t1 left join hr_audit_file_collect t2 on t1.id=t2.audit_file_id AND t2.enabled=1,
            hr_account t3,sys_office office,sys_office area
            WHERE
            t1.created_by=t3.id
            and office.area_id=area.id
            and t3.office_id=office.id
            AND t1.enabled=1

        """)
        if (CollectionUtil.isNotEmpty(auditFileQuery.officeIds)) {
            sb.append(" and t1.office_id IN :officeIds ")
        }
        if (auditFileQuery.collect) {
            sb.append("and t2.account_id =:accountId ")
            if (StringUtils.isNotEmpty(auditFileQuery.accountFavoriteId)) {
                sb.append(" and t2.account_favorite_id=:accountFavoriteId ")
            }
            if (auditFileQuery.collectDateStart != null) {
                sb.append(" and t2.collect_date >=:collectDateStart ")
            }
            if (auditFileQuery.collectDateEnd != null) {
                sb.append(" and t2.collect_date < :collectDateEnd ")
            }
        }
        if (CollectionUtil.isNotEmpty(auditFileQuery.positionIdList)) {
            sb.append(" and t1.position_id in (:positionIdList)")
        }
        if (auditFileQuery.createdDateStart != null) {
            sb.append(" and t1.created_date > :createdDateStart ")
        }
        if (auditFileQuery.createdDateEnd != null) {
            sb.append(" and t1.created_date < :createdDateEnd ")
        }
        if (auditFileQuery.processTypeId != null) {
            sb.append(" and t1.process_type_id =:processTypeId ")
        }
        if (CollectionUtil.isNotEmpty(auditFileQuery.processTypeIdList)) {
            sb.append(" and t1.process_type_id in (:processTypeIdList) ")
        }
        if (auditFileQuery.id != null) {
            sb.append(" and t1.id=:id ")
        }
        if (auditFileQuery.title != null) {
            sb.append(" and t1.title like concat('%',:title,'%') ")
        }
        if (auditFileQuery.content != null) {
            sb.append(" and t1.content like concat('%',:content,'%') ")
        }
        if (auditFileQuery.officeId != null) {
            sb.append(" and office.id=:officeId ")
        }
        if (auditFileQuery.officeName != null) {
            sb.append(" and office.name like concat('%',:officeName,'%') ")
        }
        if (CollectionUtil.isNotEmpty(auditFileQuery.processTypeIdList)) {
            sb.append(" and process_type_id in (:processTypeIdList) ")
        }
        if (CollectionUtil.isNotEmpty(auditFileQuery.officeIdList)) {
            sb.append(" and office.id in (:officeIdList) ")
        }
        if(StringUtils.isNotBlank(auditFileQuery.processStatus)){
            sb.append(" and process_status like concat('%',:processStatus,'%') ")
        }
        if(StringUtils.isNotBlank(auditFileQuery.processTypeId)){
            sb.append(" and process_type_id =:processTypeId ")
        }
        if (auditFileQuery.createdByName != null) {
            sb.append("""
                and t1.created_by in(
                select id
                from hr_account
                where login_name like concat('%',:createdByName,'%')
                and enabled=1
                )
            """)
        }
        var pageableSql = MySQLDialect.getInstance().getPageableSql(sb.toString(),pageable);
        var countSql = MySQLDialect.getInstance().getCountSql(sb.toString());
        var list = namedParameterJdbcTemplate.query(pageableSql, BeanPropertySqlParameterSource(auditFileQuery), BeanPropertyRowMapper(AuditFileDto::class.java));
        var count = namedParameterJdbcTemplate.queryForObject(countSql, BeanPropertySqlParameterSource(auditFileQuery),Long::class.java);
        return PageImpl(list,pageable,count);
    }

}