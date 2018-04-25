package com.eric.grace.upms.modules.sys.service.impl;

import com.eric.grace.dao.common.service.impl.CommonServiceImpl;
import com.eric.grace.service.exception.enums.GraceExceptionEnum;
import com.eric.grace.service.result.ResponseVo;
import com.eric.grace.service.result.ResultUtil;
import com.eric.grace.upms.common.constant.SysConstant;
import com.eric.grace.upms.common.utils.PasswordHash;
import com.eric.grace.upms.common.utils.SpringContextHolder;
import com.eric.grace.upms.modules.sys.controller.dto.RequestPassword;
import com.eric.grace.upms.modules.sys.controller.dto.RequestUser;
import com.eric.grace.upms.modules.sys.entity.SysUser;
import com.eric.grace.upms.modules.sys.mapper.SysUserMapper;
import com.eric.grace.upms.modules.sys.service.ISysUserRoleService;
import com.eric.grace.upms.modules.sys.service.ISysUserService;
import com.eric.grace.upms.modules.sys.service.ITokenService;
import com.eric.grace.utils.common.RandomUtil;
import com.eric.grace.utils.common.StrUtil;
import com.eric.grace.utils.crypto.digest.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * SysUserServiceImpl: 用户业务实现类
 *
 * @author: MrServer
 * @since: 2018/4/18 下午3:07
 */
@Service
public class SysUserServiceImpl extends CommonServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ITokenService tokenService;


    /**
     * 根据用户名查找账号
     *
     * @param username
     * @return
     */
    @Override
    public SysUser selectUserByUsername(String username) {
        return SpringContextHolder.getBean(SysUserMapper.class).selectUserByUsername(username);
    }


    /**
     * 创建用户
     *
     * @param requestUser
     * @return
     */
    public ResponseVo saveEntity(RequestUser requestUser) {
        if (null == requestUser || null == requestUser.getSysUser()) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR);
        }

        SysUser sysUser = requestUser.getSysUser();

        // 验证账号
        if (StrUtil.isBlank(sysUser.getUsername())) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR);
        }

        //验证当前用户是否存在
        if (null != findSysUserByUsername(sysUser.getUsername())) {
            return ResultUtil.error(GraceExceptionEnum.BUSINESS_FAILE.getCode(), "当期用户已存在");
        }

        sysUser.setId(RandomUtil.simpleUUID());
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        //初始化密码
        String passwordValid = DigestUtil.md5Hex(DigestUtil.md5Hex(SysConstant.DEFAULT_PASSWORD));
        sysUser.setPassword(PasswordHash.createHash(passwordValid));
        //设置数据为正常状态
        sysUser.setDelFlag(SysConstant.DEFAULT_DEL_FLAG_NO);

        super.insert(sysUser);
        if (null != requestUser.getRoles() && !StrUtil.isBlank(requestUser.getRoles())) {
            sysUserRoleService.saveUserRoles(requestUser.getSysUser().getId(), requestUser.getRoles());
        }
        return ResultUtil.success(GraceExceptionEnum.BUSIONESS_SUCCESS, sysUser);
    }


    /**
     * 重置密码
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseVo resetPassword(String userId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        //初始化密码
        String passwordValid = DigestUtil.md5Hex(SysConstant.DEFAULT_PASSWORD);
        sysUser.setPassword(PasswordHash.createHash(passwordValid));

        return ResultUtil.success(GraceExceptionEnum.BUSIONESS_SUCCESS);
    }

    /**
     * 更新密码
     *
     * @param requestPassword
     * @return
     */
    @Override
    public ResponseVo EditPassword(RequestPassword requestPassword) {

        if (null != requestPassword) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "请求参数不正确");
        }
        // 判空
        if (StrUtil.isBlank(requestPassword.getId()) || StrUtil.isBlank(requestPassword.getPassword()) || StrUtil.isBlank(requestPassword.getOldPassword()) || StrUtil.isBlank(requestPassword.getRepassword())) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "请求参数不正确");
        }

        // 验证新密码
        if (!requestPassword.getPassword().equals(requestPassword.getRepassword())) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "两次输入的密码不一致");
        }

        // 验证旧密码是否正确
        SysUser sysUser = selectById(requestPassword.getId());
        if (null != sysUser && checkPw(sysUser.getPassword(), requestPassword.getOldPassword())) {
            //设置新密码
            String passwordValid = DigestUtil.md5Hex(requestPassword.getPassword());
            sysUser.setPassword(PasswordHash.createHash(passwordValid));
            sysUser.setPassword(passwordValid);
            updateById(sysUser);
            return ResultUtil.success(GraceExceptionEnum.BUSIONESS_SUCCESS);
        } else {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "当前用户不存在");
        }
    }


    /***
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ResponseVo login(String username, String password) {

        // 数据验证
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "用户名密码或者密码不能为空");
        }
        //根据用户名获取当前用户
        SysUser sysUser = findSysUserByUsername(username);

        // 验证用户是否存在
        if (null == sysUser) {
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "当前用户不存在");
        }
        // 验证密码
        if(!checkPw(sysUser.getPassword(),password)){
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "用户名或者密码不正确");
        }

        //账号锁定
        if(sysUser.getUserStatus() == 0){
            return ResultUtil.error(GraceExceptionEnum.PARAMS_ERROR.getCode(), "账号已被锁定,请联系管理员");
        }


        // 生成token
        return tokenService.createToken(sysUser.getId());
    }


    /**
     * 根据用户名查找用户基本
     *
     * @param username
     * @return
     */
    public SysUser findSysUserByUsername(String username) {
        return SpringContextHolder.getBean(SysUserMapper.class).selectUserByUsername(username);
    }


    /**
     * 检测密码
     *
     * @param oriPassWord 正确的原始密码
     * @param passWord    待检测密码
     * @return
     */
    public static boolean checkPw(String oriPassWord, String passWord) {
        //MD5 两次加密后解密  --> 加盐处理
        return PasswordHash.validatePassword(DigestUtil.md5Hex(passWord), oriPassWord);
    }


}