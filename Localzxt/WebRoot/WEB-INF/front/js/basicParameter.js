//定义拼装请求URL的常量
var BPR = BPR || {};

BPR.domain = "/zxt";

BPR.PARAM_ENCODE_KEY = "20766caf0fd446c7b0b13f7fe985a8e8";

BPR.USER_COOKIE_KEY = "zxt_user_auth_key";

BPR.SITEUSER_COOKIE_KEY = "zxt_siteUser_auth_key";

BPR.COOKIE_AUTH_KEY = "_auth_key";

BPR.USER_IS_LOGIN = "user_is_login";

BPR.genericState = {
		deleted	:0,			//删除状态
		hidden	:1,			//隐藏状态
		active	:2			//正常状态
}

BPR.contentPageState = {
		deleted		:0,			//删除状态
		closed		:1,			//关闭
		no_segment	:2,			//未分词
		err_segment	:3,			//分词错误
		wait_audit	:4,			//待审核
		audit_back	:5,			//审核不通过
		active		:6			//已发布
}
BPR.sortFeildState = {
		releaseTime:0,		//爬取时间
		typeId	   :1,		//消息口
		levelId	   :2,		//等级
		areaId     :3,		//地区
		beginAt    :4,		//发布时间
		endAt      :5		//截止时间
}


BPR.userState = {
		deleted : 0,		//已删除
		closed	:1, 		// 关闭
		registered	:2, 	// 注册
		frozen	:3, 		// 冻结
		active	:4 			// 正常
}

BPR.userStates2Zh = {
		0	: "删除", 		// 删除
		1	: "关闭", 		// 关闭
		2	: "注册", 		// 注册
		3	: "冻结", 		// 冻结、暂停
		4	: "已激活" 		// 正常
}
