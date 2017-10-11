package cn.tongyuankeji.entity.dict;

/**
 * Region entity. @author MyEclipse Persistence Tools
 */

public class Region implements java.io.Serializable {

	// Fields

	private Integer sysId;
	private String code;
	private String title;
	private String fatherCode;

	// Constructors

	/** default constructor */
	public Region() {
	}

	/** full constructor */
	public Region(String code, String title, String fatherCode) {
		this.code = code;
		this.title = title;
		this.fatherCode = fatherCode;
	}

	// Property accessors

	public Integer getSysId() {
		return this.sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFatherCode() {
		return this.fatherCode;
	}

	public void setFatherCode(String fatherCode) {
		this.fatherCode = fatherCode;
	}

}