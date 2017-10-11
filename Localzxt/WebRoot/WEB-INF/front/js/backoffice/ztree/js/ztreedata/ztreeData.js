var setting = {
	check : {
		chkboxType : {
			"Y" : "ps",
			"N" : "ps"
		},
		enable : true
	},
	data : {
		simpleData : {
			enable : true
		},
	}
};

var zNodes = [
	{
		id : 1,
		pId : 0,
		name : "系统维护",
		open : true
	},
	{
		id : 2,
		pId : 1,
		name : "系统配置",

		//isParent : true,
		/* children : [
			{
				name : "修改配置"
			}
		], */
		open : true
	},
	{
		id : 3,
		pId : 2,
		name : "修改配置"
	},

	{
		id : 4,
		pId : 1,
		name : "操作日志",
		open : true
	},
	{
		id : 5,
		pId : 4,
		name : "查询"
	},
	{
		id : 6,
		pId : 0,
		name : "数据字典",
		open : true
	},
	{
		id : 7,
		pId : 6,
		name : "常用选项"
	},
	{
		id : 8,
		pId : 7,
		name : "查看"
	},
	{
		id : 9,
		pId : 7,
		name : "新增"
	},
	{
		id : 10,
		pId : 7,
		name : "编辑"
	},
	{
		id : 11,
		pId : 7,
		name : "调整顺序"
	},
	{
		id : 12,
		pId : 7,
		name : "删除"
	},
	{
		id : 13,
		pId : 6,
		name : "地区"
	},
	{
		id : 14,
		pId : 13,
		name : "查看"
	},
	{
		id : 15,
		pId : 6,
		name : "工业"
	},
	{
		id : 16,
		pId : 15,
		name : "查看"
	},
	{
		id : 17,
		pId : 6,
		name : "行业"
	},
	{
		id : 18,
		pId : 17,
		name : "查看列表、查看详细信息"
	},
	{
		id : 19,
		pId : 17,
		name : "新增"
	},
	{
		id : 20,
		pId : 17,
		name : "编辑"
	},
	{
		id : 21,
		pId : 17,
		name : "修改状态"
	},
	{
		id : 22,
		pId : 0,
		name : "组织架构"
	},
	{
		id : 23,
		pId : 22,
		name : "园区"
	},
	{
		id : 24,
		pId : 23,
		name : "查看园区列表和详细信息"
	},
	{
		id : 25,
		pId : 24,
		name : "获取园区列表"
	},
	{
		id : 26,
		pId : 24,
		name : "查看详情"
	},
	{
		id : 27,
		pId : 24,
		name : "查看所有入驻企业"
	},
	{
		id : 28,
		pId : 23,
		name : "新增、编辑、修改园区状态"
	},
	{
		id : 29,
		pId : 28,
		name : "新建园区"
	},
	{
		id : 30,
		pId : 28,
		name : "编辑园区"
	},
	{
		id : 31,
		pId : 28,
		name : "修改状态"
	},
	{
		id : 32,
		pId : 28,
		name : "修改园区下企业"
	},
	{
		id : 33,
		pId : 22,
		name : "后台权限"
	},
	{
		id : 34,
		pId : 33,
		name : "查看列表、查看详细信息"
	},
	{
		id : 35,
		pId : 33,
		name : "新增"
	},
	{
		id : 36,
		pId : 33,
		name : "编辑"
	},
	{
		id : 37,
		pId : 33,
		name : "调整权限"
	},
	{
		id : 38,
		pId : 33,
		name : "停用/启用"
	},
	{
		id : 39,
		pId : 22,
		name : "后台用户"
	},
	{
		id : 40,
		pId : 39,
		name : "查看列表、查看详细信息"
	},
	{
		id : 41,
		pId : 39,
		name : "加载后台用户列表"
	},
	{
		id : 42,
		pId : 39,
		name : "编辑后台用户时获取详细信息"
	},
	{
		id : 43,
		pId : 39,
		name : "新增、编辑、修改状态"
	},
	{
		id : 44,
		pId : 43,
		name : "新增后台用户时保存"
	},
	{
		id : 45,
		pId : 43,
		name : "编辑后台用户时保存"
	},
	{
		id : 46,
		pId : 43,
		name : "修改后台用户状态"
	},
	{
		id : 47,
		pId : 0,
		name : "园区工具"
	},
	{
		id : 48,
		pId : 47,
		name : "我的园区"
	},
	{
		id : 49,
		pId : 48,
		name : "查看我的园区详细信息"
	},
	{
		id : 50,
		pId : 49,
		name : "查看详情"
	},
	{
		id : 51,
		pId : 48,
		name : "编辑园区"
	},
	{
		id : 52,
		pId : 51,
		name : "编辑园区"
	},

];

$(document).ready(function() {
	var zTreeObj = $.fn.zTree.init($("#ACL"), setting, zNodes);
//zTreeObj.expandAll(true);
//zTreeObj.checkAllNodes(true);
});

function test() {
	var treeObj = $.fn.zTree.getZTreeObj("ACL");
	var nodes = treeObj.getCheckedNodes(true);
	var v = "";
	var v1 = "";
	var v2 = "";
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].pId != null) {
			v1 += "{id:" + nodes[i].id + "," + "pId:" + nodes[i].pId + ",name:" + nodes[i].name + "},";
		} else {
			v2 += "{id:" + nodes[i].id + ",name:" + nodes[i].name + "},";
		}

	}
	v = "{data:{" + v1 + v2 + "}}";
	var json = JSON.stringify(v);
	alert(json);
}