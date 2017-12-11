package com.publiccms.views.directive.sys;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publiccms.common.base.AbstractTemplateDirective;
import com.publiccms.common.handler.RenderHandler;
import com.publiccms.common.tools.CommonUtils;
import com.publiccms.entities.sys.SysRoleMoudle;
import com.publiccms.logic.service.sys.SysRoleMoudleService;
import com.publiccms.logic.service.sys.SysRoleService;

/**
 *
 * SysRoleMoudleDirective
 * 
 */
@Component
public class SysRoleMoudleDirective extends AbstractTemplateDirective {

    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        Integer[] roleIds = handler.getIntegerArray("roleIds");
        Integer moudleId = handler.getInteger("moudleId");
        if (CommonUtils.notEmpty(roleIds)) {
            if (CommonUtils.notEmpty(moudleId)) {
                SysRoleMoudle entity = service.getEntity(roleIds, moudleId);
                handler.put("object", entity).render();
            } else {
                Integer[] moudleIds = handler.getIntegerArray("moudleIds");
                if (CommonUtils.notEmpty(moudleIds)) {
                    Map<String, Boolean> map = new LinkedHashMap<>();
                    if (sysRoleService.showAllMoudle(roleIds)) {
                        for (Integer id : moudleIds) {
                            map.put(String.valueOf(id), true);
                        }
                    } else {
                        for (SysRoleMoudle entity : service.getEntitys(roleIds, moudleIds)) {
                            map.put(String.valueOf(entity.getId().getMoudleId()), true);
                        }
                    }
                    handler.put("map", map).render();
                }
            }
        }
    }

    @Override
    public boolean needAppToken() {
        return true;
    }

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMoudleService service;

}