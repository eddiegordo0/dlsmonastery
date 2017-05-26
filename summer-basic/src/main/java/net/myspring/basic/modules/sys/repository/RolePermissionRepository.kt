package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.RolePermission
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Query


/**
 * Created by haos on 2017/5/25.
 */
@CacheConfig(cacheNames = arrayOf("rolePermissions"))
interface RolePermissionRepository: BaseRepository<RolePermission, String> {
    @Cacheable
    override fun findOne(id: String): RolePermission

    @CachePut(key="#id")
    fun save(RolePermission: RolePermission): Int

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where t.enabled=1
        and t.roleId=?1
     """)
    fun findByRoleId(roleId: String): MutableList<RolePermission>

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where  t.roleId=?1
     """)
    fun findAllByRoleId(roleId: String): MutableList<RolePermission>

    @Query("""
     UPDATE  #{#entityName} t
     SET t.enabled=?1
     where t.roleId=?2
     """)
    fun setEnabledByRoleId(enabled: Boolean, roleId: String): Int

    @Query("""
         UPDATE  #{#entityName} t
         SET t.enabled=?1
         where t.permissionId in ?2
     """)
    fun setEnabledByPermissionIdList(enabled: Boolean,  permissionIdList: MutableList<String>): Int

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where  t.permissionId=?1
     """)
    fun findAllByPermissionId(permissionId: String): MutableList<RolePermission>

    @Query("""
       UPDATE  #{#entityName} t
       SET t.enabled=?1
       where t.roleId=?2
     """)
    fun setEnabledByPermissionId(enabled: Boolean, permissionId: String): Int

    @Query("""
       UPDATE  #{#entityName} t
       SET t.enabled=?1
        where t.roleId in ?2
     """)
    fun setEnabledByRoleIdList( enabled: Boolean,  roleIdList: MutableList<String>): Int

}