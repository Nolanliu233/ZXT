package cn.tongyuankeji.entity.structure;

import java.sql.Timestamp;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumGenderKind;
import cn.tongyuankeji.common.parameters.EnumLogOperatorKind;
import cn.tongyuankeji.common.parameters.EnumPersonState;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;

/**
 * SiteUser entity. @author MyEclipse Persistence Tools
 */

public class BackUser extends BaseEntity implements java.io.Serializable, Person {

	// Fields
	public static final String SYSTEM_ADMIN_NAME = "system_admin";

	private Integer sysId;
	private Integer ownerGovId;
	private Byte state;
	private String loginName;
	private String userName;
	private String mobilePhone;
	private String email;
	private Timestamp createdAt;
	private String createdByFullname;
	private String pwd;
	private Byte gender;
	private String securityQ;
	private String securityA;
	private String pidNumber;
	private String otherPhone;
	private Integer roleId;
	private String jobTitle;
	private String thumbFile;
	private String remarks;
	private Timestamp recentLoginAt;
	private String recentLoginIp;

	// Constructors

	/** default constructor */
	public BackUser() {
	}
	
	public BackUser(Government gov) {
		this.ownerGovId = gov.getSysId();
	}

	/** minimal constructor */
	public BackUser(Integer ownerGovId, Byte state, String userName, String mobilePhone, Timestamp createdAt,
			String createdByFullname, String pwd, String realName, Byte gender, String otherPhone) {
		this.ownerGovId = ownerGovId;
		this.state = state;
		this.userName = userName;
		this.mobilePhone = mobilePhone;
		this.createdAt = createdAt;
		this.createdByFullname = createdByFullname;
		this.pwd = pwd;
		this.loginName = realName;
		this.gender = gender;
		this.otherPhone = otherPhone;
	}

	/** full constructor */
	public BackUser(Integer ownerGovId, Byte state, String userName, String mobilePhone, String email,
			Timestamp createdAt, String createdByFullname, String pwd, String loginName, Byte gender, String securityQ,
			String securityA, String pidNumber, String otherPhone, Integer roleId, String jobTitle, String thumbFile,
			String remarks, Timestamp recentLoginAt, String recentLoginIp) {
		this.ownerGovId = ownerGovId;
		this.state = state;
		this.userName = userName;
		this.mobilePhone = mobilePhone;
		this.email = email;
		this.createdAt = createdAt;
		this.createdByFullname = createdByFullname;
		this.pwd = pwd;
		this.loginName = loginName;
		this.gender = gender;
		this.securityQ = securityQ;
		this.securityA = securityA;
		this.pidNumber = pidNumber;
		this.otherPhone = otherPhone;
		this.roleId = roleId;
		this.jobTitle = jobTitle;
		this.thumbFile = thumbFile;
		this.remarks = remarks;
		this.recentLoginAt = recentLoginAt;
		this.recentLoginIp = recentLoginIp;
	}

	// Property accessors

	@EntityDesc(always = true)
	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	@EntityDesc(always = true)
	public Integer getOwnerGovId() {
		return this.ownerGovId;
	}

	public void setOwnerGovId(Integer ownerGovId) {
		this.ownerGovId = ownerGovId;
	}

	@EntityDesc(always = true, makeString = true, enumType = EnumPersonState.class)
	public Byte getState() {
		return this.state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	@EntityDesc(always = true)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@EntityDesc()
	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@EntityDesc()
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@EntityDesc()
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@EntityDesc()
	public String getCreatedByFullname() {
		return this.createdByFullname;
	}

	public void setCreatedByFullname(String createdByFullname) {
		this.createdByFullname = createdByFullname;
	}

	@EntityDesc()
	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@EntityDesc(always = true)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@EntityDesc(makeString = true, enumType = EnumGenderKind.class)
	public Byte getGender() {
		return this.gender;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
	}

	@EntityDesc()
	public String getSecurityQ() {
		return this.securityQ;
	}

	public void setSecurityQ(String securityQ) {
		this.securityQ = securityQ;
	}

	@EntityDesc()
	public String getSecurityA() {
		return this.securityA;
	}

	public void setSecurityA(String securityA) {
		this.securityA = securityA;
	}

	@EntityDesc()
	public String getPidNumber() {
		return this.pidNumber;
	}

	public void setPidNumber(String pidNumber) {
		this.pidNumber = pidNumber;
	}

	@EntityDesc()
	public String getOtherPhone() {
		return this.otherPhone;
	}

	public void setOtherPhone(String otherPhone) {
		this.otherPhone = otherPhone;
	}

	@EntityDesc()
	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	@EntityDesc()
	public String getJobTitle() {
		return this.jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	@EntityDesc()
	public String getThumbFile() {
		return this.thumbFile;
	}

	public void setThumbFile(String thumbFile) {
		this.thumbFile = thumbFile;
	}

	@EntityDesc()
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@EntityDesc(shortDate = true)
	public Timestamp getRecentLoginAt() {
		return this.recentLoginAt;
	}

	public void setRecentLoginAt(Timestamp recentLoginAt) {
		this.recentLoginAt = recentLoginAt;
	}

	@EntityDesc()
	public String getRecentLoginIp() {
		return this.recentLoginIp;
	}

	public void setRecentLoginIp(String recentLoginIp) {
		this.recentLoginIp = recentLoginIp;
	}

	@Override
	public Integer getOperatorId() {
		return this.sysId;
	}

	@Override
	public Byte getOperatorKind() {
		return EnumLogOperatorKind.backuser.byteObject();
	}

	@Override
	public String getKeyName() {
		// TODO Auto-generated method stub
		return this.getUserName().substring(0, 1) + "_" + this.getMobilePhone();
	}
	
	public Integer[] getDataTypes() {
		return null;
	}
}