package cn.tongyuankeji.entity.basicData;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.entity.BaseEntity;

public class DataType extends BaseEntity implements java.io.Serializable{

	// Fields

		private Integer typeId;
		private String typeName;
		private Byte state;
		private Integer orderBy;
		private String remarks;

		// Constructors

		/** default constructor */
		public DataType() {
		}
		/** full constructor */
		public DataType(Integer typeId, String typeName, byte state, Integer orderBy, String remarks) {
			this.typeId = typeId;
			this.typeName = typeName;
			this.state = state;
			this.orderBy = orderBy;
			this.remarks = remarks;
		}

		// Property accessors

		@EntityDesc(always = true,enumType = EnumGenericState.class)
		public Byte getState() {
			return state;
		}

		public void setState(Byte state) {
			this.state = state;
		}

		@EntityDesc(always = true)
		public Integer getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(Integer orderBy) {
			this.orderBy = orderBy;
		}

		@EntityDesc(always = true)
		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		@EntityDesc(always = true)
		public Integer getTypeId() {
			return this.typeId;
		}

		public void setTypeId(Integer typeId) {
			this.typeId = typeId;
		}
		@EntityDesc(always = true)
		public String getTypeName() {
			return this.typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
}
