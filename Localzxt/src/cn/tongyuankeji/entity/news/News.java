package cn.tongyuankeji.entity.news;

import java.sql.Timestamp;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.parameters.EnumNewsOrderBy;
import cn.tongyuankeji.common.parameters.EnumNewsType;
import cn.tongyuankeji.entity.BaseEntity;

public class News extends BaseEntity implements java.io.Serializable{

	private Integer newsId;
	private String title;//标题
	private String contentSummary;//内容摘要
	private String contentUrl;//正文路径
	private Integer browerCount;//浏览次数
	private Byte type;//新闻类别
	private Byte orderBy;//排序号
	private Byte state;//状态
	private Timestamp docCreateTime;//发布时间
	private Timestamp docAlterTime;//修改时间
	
	@EntityDesc(always = true)
	public Integer getNewsId() {
		return newsId;
	}
	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}
	@EntityDesc(always = true)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@EntityDesc(always = true)
	public String getContentSummary() {
		return contentSummary;
	}
	public void setContentSummary(String contentSummary) {
		this.contentSummary = contentSummary;
	}
	@EntityDesc(always = true)
	public String getContentUrl() {
		return contentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	@EntityDesc(always = true)
	public Integer getBrowerCount() {
		return browerCount;
	}
	public void setBrowerCount(Integer browerCount) {
		this.browerCount = browerCount;
	}
	@EntityDesc(always=true,makeString=true,enumType=EnumNewsType.class)
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	@EntityDesc(always = true,makeString=true,enumType=EnumNewsOrderBy.class)
	public Byte getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(Byte orderBy) {
		this.orderBy = orderBy;
	}
	@EntityDesc(always=true,makeString=true,enumType=EnumGenericState.class)
	public Byte getState() {
		return state;
	}
	public void setState(Byte state) {
		this.state = state;
	}
	@EntityDesc(always = true)
	public Timestamp getDocCreateTime() {
		return docCreateTime;
	}
	public void setDocCreateTime(Timestamp docCreateTime) {
		this.docCreateTime = docCreateTime;
	}
	public Timestamp getDocAlterTime() {
		return docAlterTime;
	}
	public void setDocAlterTime(Timestamp docAlterTime) {
		this.docAlterTime = docAlterTime;
	}
	
}
