package cn.tongyuankeji.entity.information;

import cn.tongyuankeji.common.annotation.EntityDesc;

public class InformationTag implements java.io.Serializable{

	// Fields

		private Integer sysId;
		private Integer informationId;
		private Integer tagId;
		private String remarks;

		// Constructors

		/** default constructor */
		public InformationTag() {
		}

		/** minimal constructor */
		public InformationTag(Integer informationId, Integer tagId) {
			this.informationId = informationId;
			this.tagId = tagId;
		}

		/** full constructor */
		public InformationTag(Integer informationId, Integer tagId, String remarks) {
			this.informationId = informationId;
			this.tagId = tagId;
			this.remarks = remarks;
		}

		@EntityDesc(always = true)
		public Integer getSysId() {
			return this.sysId;
		}

		public void setSysId(Integer sysId) {
			this.sysId = sysId;
		}

		@EntityDesc(always = true)
		public Integer getInformationId() {
			return this.informationId;
		}

		public void setInformationId(Integer informationId) {
			this.informationId = informationId;
		}

		@EntityDesc(always = true)
		public Integer getTagId() {
			return this.tagId;
		}

		public void setTagId(Integer tagId) {
			this.tagId = tagId;
		}

		@EntityDesc(always = true)
		public String getRemarks() {
			return this.remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
}
