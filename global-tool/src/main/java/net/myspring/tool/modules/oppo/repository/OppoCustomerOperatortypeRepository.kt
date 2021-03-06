package net.myspring.tool.modules.oppo.repository;
import com.google.common.collect.Maps
import net.myspring.tool.common.repository.BaseRepository
import net.myspring.tool.modules.oppo.domain.OppoCustomer
import net.myspring.tool.modules.oppo.domain.OppoCustomerOperatortype
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

interface OppoCustomerOperatortypeRepository : BaseRepository<OppoCustomerOperatortype, String>, OppoCustomerOperatortypeRepositoryCustom {

}
interface OppoCustomerOperatortypeRepositoryCustom{
    fun findByDate(companyName: String,dateStart:String,dateEnd:String):MutableList<OppoCustomerOperatortype>
    fun deleteByCompanyNameAndDate(companyName:String,dateStart: String,dateEnd: String):Int
}
class OppoCustomerOperatortypeRepositoryImpl @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : OppoCustomerOperatortypeRepositoryCustom{

    override fun findByDate(companyName: String,dateStart:String, dateEnd:String): MutableList<OppoCustomerOperatortype> {
        val paramMap = Maps.newHashMap<String, Any>();
        paramMap.put("dateStart",dateStart);
        paramMap.put("dateEnd",dateEnd);
        paramMap.put("companyName",companyName);
        return namedParameterJdbcTemplate.query("""
            select *  from oppo_push_customer_operator_type
            where created_date >=:dateStart
              and created_date < :dateEnd
              and company_name = :companyName
         """,paramMap, BeanPropertyRowMapper(OppoCustomerOperatortype::class.java));
    }

    override fun deleteByCompanyNameAndDate(companyName:String,dateStart: String, dateEnd: String): Int {
        val map = Maps.newHashMap<String,String>()
        map.put("dateStart",dateStart)
        map.put("dateEnd",dateEnd)
        map.put("companyName",companyName)
        return namedParameterJdbcTemplate.update("""
          delete from oppo_push_customer_operator_type
          where created_date >=:dateStart
            and created_date < :dateEnd
            and company_name = :companyName
        """,map)
    }
}
