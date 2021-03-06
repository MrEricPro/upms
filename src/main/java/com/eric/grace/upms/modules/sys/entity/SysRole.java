package com.eric.grace.upms.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.eric.grace.dao.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * SysRole: 系统角色实体类
 *
 * @author: MrServer
 * @since: 2018/4/18 下午3:16
 */
@TableName("t_sys_role")
@ApiModel(value = "系统角色")
public class SysRole extends BaseModel<SysRole> {

    @ApiModelProperty(value = "角色类型")
    @TableField("role_type_id")
    private String roleTypeId;

    @ApiModelProperty(value = "角色编码")
    @TableField("role_code")
    @NotBlank(message="角色编码不能为空")
    private String roleCode;

    @ApiModelProperty(value = "角色名称")
    @TableField("role_name")
    @NotBlank(message="角色名称不能为空")
    private String roleName;

    @ApiModelProperty(value = "角色描述")
    @TableField("role_desc")
    private String roleDesc;

    @ApiModelProperty(value = "角色图标")
    @TableField("role_icon")
    private String roleIcon;

    @ApiModelProperty(value = "父角色")
    @TableField("parent_id")
    private String parentId;

    @ApiModelProperty(value = "所属部门")
    @TableField("dept_id")
    private String deptId;

    @TableField(exist = false)
    private List<String> menuIdList;

    @TableField(exist = false)
    private List<String> deptIdList;

    @TableField(exist = false)
    private String deptName;




    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getRoleIcon() {
        return roleIcon;
    }

    public void setRoleIcon(String roleIcon) {
        this.roleIcon = roleIcon;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    public List<String> getMenuIdList() {
        return menuIdList;
    }

    public void setMenuIdList(List<String> menuIdList) {
        this.menuIdList = menuIdList;
    }

    public List<String> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<String> deptIdList) {
        this.deptIdList = deptIdList;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}