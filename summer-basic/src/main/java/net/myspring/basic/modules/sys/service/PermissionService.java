package net.myspring.basic.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.sys.domain.Menu;
import net.myspring.basic.modules.sys.domain.MenuCategory;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.basic.modules.sys.dto.PermissionDto;
import net.myspring.basic.modules.sys.manager.MenuCategoryManager;
import net.myspring.basic.modules.sys.manager.MenuManager;
import net.myspring.basic.modules.sys.manager.PermissionManager;
import net.myspring.basic.modules.sys.mapper.MenuCategoryMapper;
import net.myspring.basic.modules.sys.mapper.MenuMapper;
import net.myspring.basic.modules.sys.mapper.PermissionMapper;
import net.myspring.basic.modules.sys.web.form.PermissionForm;
import net.myspring.basic.modules.sys.web.query.PermissionQuery;
import net.myspring.common.tree.TreeNode;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PermissionService {

    @Autowired
    private PermissionManager permissionManager;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private MenuCategoryManager menuCategoryManager;
    @Autowired
    private MenuCategoryMapper menuCategoryMapper;
    @Autowired
    private MenuManager menuManager;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private CacheUtils cacheUtils;

    public List<Permission> findByPositionId(String positionId) {
        return permissionMapper.findByPositionId(positionId);
    }

    public Permission findOne(String id) {
        Permission permission = permissionManager.findOne(id);
        return permission;
    }

    public PermissionForm findForm(PermissionForm permissionForm) {
        if (!permissionForm.isCreate()) {
            Permission permission = permissionManager.findOne(permissionForm.getId());
            permissionForm = BeanUtil.map(permission, PermissionForm.class);
            cacheUtils.initCacheInput(permissionForm);
        }
        return permissionForm;
    }

    public List<Permission> findAll() {
        return permissionMapper.findAll();
    }

    public List<Permission> findByPermissionLike(String permissionStr) {
        return permissionMapper.findByPermissionLike(permissionStr);
    }

    public Page<PermissionDto> findPage(Pageable pageable, PermissionQuery permissionQuery) {
        Page<PermissionDto> permissionDtoPage = permissionMapper.findPage(pageable, permissionQuery);
        cacheUtils.initCacheInput(permissionDtoPage.getContent());
        return permissionDtoPage;
    }

    public Permission save(PermissionForm permissionForm) {
        Permission permission;
        if (permissionForm.isCreate()) {
            permission=BeanUtil.map(permissionForm, Permission.class);
            permission=permissionManager.save(permission);
            if (CollectionUtil.isNotEmpty(permissionForm.getPositionIdList())) {
                permissionMapper.savePermissionPosition(permissionForm.getId(), permissionForm.getPositionIdList());
            }
        } else {
            permission=permissionManager.updateForm(permissionForm);
            permissionMapper.deletePermissionPosition(permissionForm.getId());
            if (CollectionUtil.isNotEmpty(permissionForm.getPositionIdList())) {
                permissionMapper.savePermissionPosition(permissionForm.getId(), permissionForm.getPositionIdList());
            }
        }
        return permission;
    }

    public TreeNode findPermissionTree(List<String> permissionIds) {
        Set<String> permissionIdSet = Sets.newHashSet();
        if (CollectionUtil.isNotEmpty(permissionIds)) {
            for (String id : permissionIds) {
                permissionIdSet.add(id);
            }
        }
        TreeNode treeNode = new TreeNode("0", "权限列表");
        List<TreeNode> list = Lists.newArrayList();
        List<MenuCategory> menuCategories = menuCategoryMapper.findAllEnabled();
        List<Menu> menus = menuMapper.findByPermissionIsNotEmpty();
        Map<String, List<Menu>> menuMap = CollectionUtil.extractToMapList(menus, "menuCategoryId");
        List<Permission> permissions = permissionMapper.findAllEnabled();
        Map<String, List<Permission>> permissionMap = CollectionUtil.extractToMapList(permissions, "menuId");
        for (MenuCategory menuCategory : menuCategories) {
            TreeNode categoryTree = new TreeNode("p" + menuCategory.getId(), menuCategory.getName());
            List<Menu> menuList = menuMap.get(menuCategory.getId());
            List<TreeNode> categorychildList = Lists.newArrayList();
            for (Menu menu : menuList) {
                TreeNode menuTree = new TreeNode("m" + menu.getId(), menu.getName());
                categorychildList.add(menuTree);
                List<TreeNode> menuChildList = Lists.newArrayList();
                List<Permission> permissionList = permissionMap.get(menu.getId());
                if (CollectionUtil.isNotEmpty(permissionList)) {
                    for (Permission permission : permissionList) {
                        TreeNode permissTree = new TreeNode(permission.getId(), permission.getName());
                        menuChildList.add(permissTree);
                    }
                }
                menuTree.setChildren(menuChildList);
            }
            categoryTree.setChildren(categorychildList);
            list.add(categoryTree);
        }
        treeNode.setChildren(list);
        treeNode.setChecked(new ArrayList<>(permissionIdSet));
        return treeNode;
    }

    public void logicDeleteOne(String id) {
        permissionMapper.logicDeleteOne(id);
    }

}
