package cn.tongyuankeji.entity.user;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.entity.BaseEntity;

/**
 * UserDatatype entity. @author MyEclipse Persistence Tools
 */

public class UserDatatype extends BaseEntity implements java.io.Serializable {


    // Fields    

     private Integer sysId;
     private Integer userId;
     private Integer datatypeId;
     private Float sroce;


    // Constructors

    /** default constructor */
    public UserDatatype() {
    }

    
    /** full constructor */
    public UserDatatype(Integer userId, Integer datatypeId, Float sroce) {
        this.userId = userId;
        this.datatypeId = datatypeId;
        this.sroce = sroce;
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
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

	@EntityDesc(always = true)
    public Integer getDatatypeId() {
        return this.datatypeId;
    }
    
    public void setDatatypeId(Integer datatypeId) {
        this.datatypeId = datatypeId;
    }

    public Float getSroce() {
        return this.sroce;
    }
    
    public void setSroce(Float sroce) {
        this.sroce = sroce;
    }
}