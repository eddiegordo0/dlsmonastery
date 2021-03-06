package net.myspring.basic.modules.sys.web.controller;

import net.myspring.basic.modules.sys.domain.MenuCategory;
import net.myspring.basic.modules.sys.dto.MenuCategoryDto;
import net.myspring.basic.modules.sys.service.BackendModuleService;
import net.myspring.basic.modules.sys.service.MenuCategoryService;
import net.myspring.basic.modules.sys.web.form.MenuCategoryForm;
import net.myspring.basic.modules.sys.web.query.MenuCategoryQuery;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "sys/menuCategory")
public class MenuCategoryController {

    @Autowired
    private MenuCategoryService menuCategoryService;
    @Autowired
    private BackendModuleService backendModuleService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'sys:menuCategory:view')")
    public Page<MenuCategoryDto> list(Pageable pageable, MenuCategoryQuery menuCategoryQuery){
        Page<MenuCategoryDto> page = menuCategoryService.findPage(pageable,menuCategoryQuery);
        return page;
    }

    @RequestMapping(value = "delete")
    @PreAuthorize("hasPermission(null,'sys:menuCategory:delete')")
    public RestResponse delete(MenuCategoryDto menuCategoryDto) {
        if(CollectionUtil.isNotEmpty(menuCategoryDto.getMenuList())){
            return new RestResponse("菜单分类删除失败，请先删除下属菜单",null);
        }
        menuCategoryService.logicDelete(menuCategoryDto.getId());
        RestResponse restResponse =new RestResponse("删除成功", ResponseCodeEnum.removed.name());
        return restResponse;
    }

    @RequestMapping(value = "save")
    @PreAuthorize("hasPermission(null,'sys:menuCategory:edit')")
    public RestResponse save(MenuCategoryForm menuCategoryForm) {
        menuCategoryService.save(menuCategoryForm);
        return new RestResponse("保存成功",ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "findOne")
    public MenuCategoryDto findOne(MenuCategoryDto menuCategoryDto){
        menuCategoryDto = menuCategoryService.findOne(menuCategoryDto);
        return menuCategoryDto;
    }

    @RequestMapping(value = "getForm")
    public MenuCategoryForm findForm(MenuCategoryForm menuCategoryForm){
        menuCategoryForm.getExtra().put("backendModuleList",backendModuleService.findAll());
        return menuCategoryForm;
    }

    @RequestMapping(value = "getQuery")
    public MenuCategoryQuery getQuery(MenuCategoryQuery menuCategoryQuery){
        return menuCategoryQuery;
    }
}
