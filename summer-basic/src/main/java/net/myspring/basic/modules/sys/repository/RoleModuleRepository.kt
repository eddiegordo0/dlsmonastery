package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.RoleModule
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query


/**
 * Created by haos on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("roleModules"))
interface RoleModuleRepository : BaseRepository<RoleModule, String> {

    @Cacheable
    override fun findOne(id: String): RoleModule

    @CachePut(key = "#id")
    fun save(roleModule: RoleModule): RoleModule

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where t.enabled=1
        and t.roleId=?1
     """)
    fun findByRoleId(roleId: String): MutableList<RoleModule>

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where  t.roleId=?1
     """)
    fun findAllByRoleId(roleId: String): MutableList<RoleModule>

    @Query("""
              UPDATE  #{#entityName} t
               SET t.enabled=?1
               where t.roleId=?2
     """)
    @Modifying
    fun setEnabledByRoleId(enabled: Boolean, roleId: String): Int

    @Query("""
            UPDATE  #{#entityName} t
            SET t.enabled=?1
            where t.backendModuleId in (?2)
                and t.roleId=?3
     """)
    @Modifying
    fun setEnabledByRoleIdAndModuleIdList(enabled: Boolean, moduleIds: MutableList<String>,roleId: String): Int
}