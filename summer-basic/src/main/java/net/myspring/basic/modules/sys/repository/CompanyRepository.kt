package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.Company
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable

/**
 * Created by haos on 2017/5/24.
 */
interface CompanyRepository :BaseRepository<Company,String>{
    @CachePut(key="#id")
    fun save(company: Company): Company

    @Cacheable
    override  fun findOne(id: String): Company

}