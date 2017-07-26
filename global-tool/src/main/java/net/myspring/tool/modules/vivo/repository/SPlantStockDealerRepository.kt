package net.myspring.tool.modules.vivo.repository

import com.google.common.collect.Maps
import net.myspring.tool.modules.vivo.domain.SPlantStockDealer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils
import org.springframework.stereotype.Component

@Component
class SPlantStockDealerRepository @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

    fun deleteByAccountDate(dateStart: String, dateEnd: String):Int {
        val map = Maps.newHashMap<String,Any>()
        map.put("dateStart",dateStart)
        map.put("dateEnd",dateEnd)
        val sb = StringBuilder("""
            delete from s_PlantStockDealer_m13e00
            where AccountDate >= :dateStart
                and AccountDate < :dateEnd
        """);
        return namedParameterJdbcTemplate.update(sb.toString(),map)
    }


    fun batchSave(sPlantStockDealerM13e00List: MutableList<SPlantStockDealer>):IntArray?{
        val sb = StringBuilder()
        sb.append("""
            insert into s_PlantStockDealer_m13e00(CompanyID,DealerID,ProductID,CreatedTime,sumstock,useablestock,bad,AccountDate)
            values(:companyId,:dealerId,:productId,:createdTime,:sumStock,:useAbleStock,:bad,:accountDate)
        """)
        return namedParameterJdbcTemplate.batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(sPlantStockDealerM13e00List.toTypedArray()))
    }

    fun batchIDvivoSave(sPlantStockDealerM13e00List: MutableList<SPlantStockDealer>, agentCode: String):IntArray?{
        val sb = StringBuilder()
        sb.append("insert into s_PlantStockDealer_")
        sb.append(agentCode+" ")
        sb.append(" (CompanyID,DealerID,ProductID,CreatedTime,sumstock,useablestock,bad,AccountDate) ")
        sb.append(" values(:companyId,:dealerId,:productId,:createdTime,:sumStock,:useAbleStock,:bad,:accountDate) ")
        return namedParameterJdbcTemplate.batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(sPlantStockDealerM13e00List.toTypedArray()))
    }

}