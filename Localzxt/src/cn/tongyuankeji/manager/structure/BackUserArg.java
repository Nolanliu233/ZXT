package cn.tongyuankeji.manager.structure;

import cn.tongyuankeji.common.cache.McPerson;

import cn.tongyuankeji.common.parameters.EnumGenderKind;

/**
 * 新建/编辑 用户时带参结构<br />
 * 新建时还需要：orgId, pwdEncrypted(action接收明文，传到manager密文) <br />
 * 编辑时还需要：sysId securityQ, securityA, Pwd 单独处理
 * 
 * @author Jean 2017-07-15 创建
 */
public class BackUserArg
{
	public McPerson byUser; // 新建/编辑的人
	
	public String userName;
	public String mobilePhone;
	public String email;	
	public EnumGenderKind gender;	
	public String pidNumber;
	public String otherPhone;
	public Integer roleId;
	public String jobTitle;
	public String remarks;
}
