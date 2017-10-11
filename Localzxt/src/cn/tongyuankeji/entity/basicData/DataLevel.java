package cn.tongyuankeji.entity.basicData;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.entity.BaseEntity;

public class DataLevel extends BaseEntity implements java.io.Serializable{
	// Fields

		private Integer levelId;
		private String levelName;

		// Constructors

		/** default constructor */
		public DataLevel() {
		}

		/** full constructor */
		public DataLevel(Integer levelId, String levelName) {
			this.levelId = levelId;
			this.levelName = levelName;
		}

		// Property accessors

		@EntityDesc(always = true)
		public Integer getLevelId() {
			return this.levelId;
		}

		public void setLevelId(Integer levelId) {
			this.levelId = levelId;
		}

		@EntityDesc(always = true)
		public String getLevelName() {
			return this.levelName;
		}

		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}
}
