package cn.tongyuankeji.entity.user;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumLogOperatorKind;
import cn.tongyuankeji.common.parameters.EnumUserCreatedType;
import cn.tongyuankeji.common.parameters.EnumUserState;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;


/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User extends BaseEntity  implements java.io.Serializable, Person {

	// Fields

	/**
	 * 
	 */
	private Integer sysId;
	private String loginName;
	private String userName;
	private Boolean isManager;
	private String thumbFile;
	private String mobileNumber;
	private Byte state;
	private String pwd;
	private String email;
	private Integer companyId;
	private Byte userType;
	private Date expiryDate;
	private Timestamp lastLoginAt;
	private Integer[] dataTypes;

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String loginName, String userName, Boolean isManager, String mobileNumber, Byte state, String pwd,
			Integer companyId, Byte userType) {
		this.loginName = loginName;
		this.userName = userName;
		this.isManager = isManager;
		this.mobileNumber = mobileNumber;
		this.state = state;
		this.pwd = pwd;
		this.companyId = companyId;
		this.userType = userType;
	}

	/** full constructor */
	public User(String loginName, String userName, Boolean isManager, String thumbFile, String mobileNumber, Byte state,
			String pwd, String email, Integer companyId, Byte userType, Date expiryDate, Timestamp lastLoginAt) {
		this.loginName = loginName;
		this.userName = userName;
		this.isManager = isManager;
		this.thumbFile = thumbFile;
		this.mobileNumber = mobileNumber;
		this.state = state;
		this.pwd = pwd;
		this.email = email;
		this.companyId = companyId;
		this.userType = userType;
		this.expiryDate = expiryDate;
		this.lastLoginAt = lastLoginAt;
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
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@EntityDesc(always = true)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@EntityDesc(always = true)
	public Boolean getIsManager() {
		return this.isManager;
	}

	public void setIsManager(Boolean isManager) {
		this.isManager = isManager;
	}

	@EntityDesc(always = false)
	public String getThumbFile() {
		return this.thumbFile;
	}

	public void setThumbFile(String thumbFile) {
		this.thumbFile = thumbFile;
	}

	@EntityDesc(always = true)
	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@EntityDesc(always = true, makeString = true, enumType = EnumUserState.class)
	public Byte getState() {
		return this.state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	@EntityDesc(always = false)
	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@EntityDesc(always = false)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@EntityDesc(always = true)
	public Integer getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@EntityDesc(always = true, makeString = true, enumType = EnumUserCreatedType.class)
	public Byte getUserType() {
		return this.userType;
	}

	public void setUserType(Byte userType) {
		this.userType = userType;
	}

	@EntityDesc(always = false)
	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@EntityDesc(always = false)
	public Timestamp getLastLoginAt() {
		return this.lastLoginAt;
	}

	public void setLastLoginAt(Timestamp lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	@Override
	public Integer getOperatorId() {
		return this.sysId;
	}

	@Override
	public Byte getOperatorKind() {
		return EnumLogOperatorKind.user.byteObject();
	}

	@Override
	public Integer getRoleId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKeyName() {
		return this.getUserName().substring(0, 1) + "_" + (this.mobileNumber.isEmpty()?"":this.mobileNumber);
	}

	@Override
	public Integer getOwnerGovId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer[] getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(Integer[] dataTypes) {
		this.dataTypes = dataTypes;
	}

}