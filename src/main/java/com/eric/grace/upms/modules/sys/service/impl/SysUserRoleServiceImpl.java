package com.eric.grace.upms.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.eric.grace.dao.common.service.impl.CommonServiceImpl;
import com.eric.grace.upms.common.constant.SysConstant;
import com.eric.grace.upms.common.utils.SpringContextHolder;
import com.eric.grace.upms.modules.sys.entity.SysRole;
import com.eric.grace.upms.modules.sys.entity.SysUserRole;
import com.eric.grace.upms.modules.sys.mapper.SysRoleMapper;
import com.eric.grace.upms.modules.sys.mapper.SysUserRoleMapper;
import com.eric.grace.upms.modules.sys.service.ISysUserRoleService;
import com.eric.grace.utils.collection.CollUtil;
import com.eric.grace.utils.common.ArrayUtil;
import com.eric.grace.utils.common.RandomUtil;
import com.eric.grace.utils.common.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SysUserRoleServiceImpl: 用户与角色对应关系
 *
 * @author: MrServer
 * @since: 2018/4/18 下午3:54
 */
@Service
public class SysUserRoleServiceImpl extends CommonServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    private static Logger logger = LoggerFactory.getLogger(SysUserRoleServiceImpl.class);

    /**
     * 根据用户Id 给用户分配角色
     *
     * @param userId
     * @param roleIds 角色数组
     */
    @Override
    @Transactional
    public void saveUserRoles(String userId, List<String> roleIds, String updateUserId) {
        // role 不为空 且 用户ID 不为空 执行角色分配
        if (roleIds.size() > 0 && !StrUtil.isBlank(userId)) {

            //删除旧角色
            SpringContextHolder.getBean(SysUserRoleMapper.class).deleteRolesByUserId(userId);

            // 插入新角色
            ArrayList userRoles = CollUtil.newArrayList();
            for (String roleId : roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(RandomUtil.simpleUUID());
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                sysUserRole.setCreateUserId(updateUserId);
                sysUserRole.setUpdateUserId(updateUserId);
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setUpdateTime(new Date());
                sysUserRole.setDelFlag(SysConstant.DEFAULT_DEL_FLAG_NO);
                userRoles.add(sysUserRole);
            }
            super.insertBatch(userRoles);

//            List<SysRole> sysRoles = SpringContextHolder.getBean(SysRoleMapper.class).selectBatchIds(roleIds);
//            StringBuffer str = new StringBuffer();
//            for (int i = 0; i < sysRoles.size(); i++) {
//                str.append(sysRoles.get(i).getRoleName());
//                if (i != sysRoles.size() - 1) {
//                    str.append(",");
//                }
//            }
//            return str.toString();
        }
      //  return null;
    }


    /**
     * 根据用户Id 获取用户的角色列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> queryRoleIdList(String userId) {
        return SpringContextHolder.getBean(SysUserRoleMapper.class).queryRoleIdList(userId);
    }


}