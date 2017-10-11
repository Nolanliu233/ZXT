package cn.tongyuankeji.action.baseurl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tongyuankeji.common.annotation.ACLMapping;
import cn.tongyuankeji.common.annotation.ActionArg;
import cn.tongyuankeji.common.annotation.ActionArgs;
import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.common.util.EnumConverterImpl;
import cn.tongyuankeji.common.util.ResponseUtils;
import cn.tongyuankeji.common.web.SchemaBase;
import cn.tongyuankeji.entity.basicData.DataArea;
import cn.tongyuankeji.entity.basicData.DataLevel;
import cn.tongyuankeji.entity.basicData.DataType;
import cn.tongyuankeji.manager.basicData.DataAreaService;
import cn.tongyuankeji.manager.basicData.DataLevelService;
import cn.tongyuankeji.manager.basicData.DataTypeService;

@Controller
public class BaseDataAction {
	@Autowired
	private DataTypeService dataTypeService;

	@RequestMapping(value = "/getDataTypeList.ofc")
	public void getDataTypeList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			ResponseUtils.renderJson(resp, "成功", dataTypeService.getJSONDataTypeList());
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

	@RequestMapping(value = "/addDataType.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "dataTypeManage", opr = "edit")
	@ActionArgs(value = { @ActionArg(name = "typeName", header = "级别名"),@ActionArg(name = "remarks", header = "备注",required = false) })
	public void addDataType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			DataType dataType = new DataType();
			dataType.setTypeName((String) args.get("typeName"));
			dataType.setRemarks((String) args.get("remarks"));
			dataType.setState(EnumGenericState.active.byteValue());
			dataTypeService.saveDataType(dataType);
			ResponseUtils.renderSuccessJson(resp, "添加成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

	@RequestMapping(value = "/updateDataType.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "dataTypeManage", opr = "edit")
	@ActionArgs(value = {
			@ActionArg(name = "typeID", header = "id", target_type=Integer.class), 
			@ActionArg(name = "typeName", header = "类名"),
			@ActionArg(name = "remarks", header = "备注",required = false) })
	public void updateDataType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			DataType dataType = dataTypeService.getDataTypeById((Integer) args.get("typeID"));
			dataType.setTypeName((String) args.get("typeName"));
			dataType.setRemarks((String) args.get("remarks"));
			dataTypeService.saveDataType(dataType);
			ResponseUtils.renderSuccessJson(resp, "修改成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

	@RequestMapping(value = "/deleteDataType.ofc", method = RequestMethod.POST)
	@ACLMapping(subsystem = "backoffice", module = "info_manage", page = "dataTypeManage", opr = "edit")
	@ActionArgs(value = { @ActionArg(name = "typeID", header = "id", target_type=Integer.class) })
	public void deleteDataType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> args = null;
		try {
			args = SchemaBase.validate(req, this, EnumConverterImpl.getInstance());
			DataType dataType = dataTypeService.getDataTypeById((Integer) args.get("typeID"));
			dataType.setState(EnumGenericState.deleted.byteValue());
			dataTypeService.saveDataType(dataType);
			ResponseUtils.renderSuccessJson(resp, "删除成功");
		} catch (Exception e) {
			ResponseUtils.renderJson(resp, e);
		}
	}

}
