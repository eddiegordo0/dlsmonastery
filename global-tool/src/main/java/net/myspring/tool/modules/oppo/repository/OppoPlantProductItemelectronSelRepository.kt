package net.myspring.tool.modules.oppo.repository;

import net.myspring.tool.common.repository.BaseRepository
import net.myspring.tool.modules.oppo.domain.OppoPlantProductItemelectronSel;
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.LocalDate;
;

/**
 * Created by admin on 2016/10/11.
 */
interface OppoPlantProductItemelectronSelRepository:BaseRepository<OppoPlantProductItemelectronSel,String> {

    @Query("""
        select t.product_no from #{#entityName} t
        where t.product_no in ?1
        """)
    fun findProductNos(productNos: MutableList<String>): MutableList<String>

    @Query("""
        update crm_product_ime t1,#{#entityName} t2
        set t1.retail_date=t2.date_time
        where t1.ime=t2.product_no
        and t2.date_time >= :dateStart
        and t2.date_time <= :dateEnd
        and t1.company_id=1
        """)
    fun updateItemNumber()

    @Query("""
        update crm_product_ime t1,#{#entityName} t2
        set t1.retail_date=t2.date_time
        where t1.ime=t2.product_no
        and t2.date_time >= :dateStart
        and t2.date_time <= :dateEnd
        and t1.company_id=1
        """)
    fun updateProductImeRetailDate(@Param("dateStart") dateStart: LocalDate, @Param("dateEnd") dateEnd: LocalDate)

}
