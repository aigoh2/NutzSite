package io.nutz.nutzsite.module.sys.controllers;

import io.nutz.nutzsite.common.base.Result;
import io.nutz.nutzsite.module.sys.models.Menu;
import io.nutz.nutzsite.module.sys.models.Role;
import io.nutz.nutzsite.module.sys.services.MenuService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Hamming_Yu on 2019/1/1.
 */
@IocBean
@At("/sys/menu")
public class MenuController {
    private static final Log log = Logs.get();

    @Inject
    MenuService menuService;

    @At("")
    @Ok("th:/sys/menu/menu.html")
    public void index(HttpServletRequest req) {

    }

    @At
    @Ok("json")
    public Object list(@Param("menuName") String menuName, HttpServletRequest req) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(menuName)) {
            cnd.and("menu_name", "like", "%" + menuName + "%");
        }
        return menuService.query(cnd);
    }

    @At("/add/?")
    @Ok("th:/sys/menu/add.html")
    public void add(@Param("id") String id, HttpServletRequest req) {
        Menu menu = null;
        if (!Strings.isBlank(id)) {
            menu = menuService.fetch(id);
        }
        if (menu == null) {
            menu = new Menu();
            menu.setId("0");
            menu.setMenuName("主目录");
        }
        req.setAttribute("menu", menu);
    }

    @At
    @POST
    @Ok("json")
    public boolean checkMenuNameUnique(Menu menu) {
//        return menuService.checkMenuNameUnique(menu);
        return true;
    }

    @At
    @POST
    @Ok("json")
    public Object addDo(@Param("..") Menu menu, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            menuService.save(menu, parentId);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("th:/sys/menu/edit.html")
    public void edit(String id, HttpServletRequest req) {
        Menu menu = menuService.fetch(id);
        if (menu != null) {
            req.setAttribute("menu", menu);
            Menu parentMenu = menuService.fetch(menu.getParentId());
            if (parentMenu != null) {
                menu.setParentName(parentMenu.getMenuName());
            } else {
                req.setAttribute("parentId", "0");
            }
        }
    }

    @At
    @POST
    @Ok("json")
    public Object editDo(@Param("..") Menu menu, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            if (menu != null && Strings.isEmpty(menu.getParentId())) {
                menu.setParentId("0");
            }
            menuService.update(menu);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/remove/?")
    @Ok("json")
    public Object remove(String id, HttpServletRequest req) {
        try {
            menuService.delete(id);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    /**
     * 选择菜单树
     */
    @At("/selectTree/?")
    @Ok("th:/sys/menu/tree.html")
    public void selectTree(String id, HttpServletRequest req) {
        req.setAttribute("menu", menuService.fetch(id));
    }

    /**
     * 加载所有菜单列表树
     */
    @At
    @Ok("json")
    public List<Map<String, Object>> menuTreeData() {
        List<Map<String, Object>> tree = menuService.menuTreeData();
        return tree;
    }

    /**
     * 加载角色菜单列表树
     *
     * @param roleId
     * @return
     */
    @At
    @Ok("json")
    public List<Map<String, Object>> roleMenuTreeData(String roleId) {
        List<Map<String, Object>> tree = menuService.roleMenuTreeData(roleId);
        return tree;
    }

}