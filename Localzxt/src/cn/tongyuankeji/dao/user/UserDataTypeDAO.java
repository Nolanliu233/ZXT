package cn.tongyuankeji.dao.user;

import java.util.List;

import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.user.UserDatatype;
import net.sf.json.JSONArray;

public interface UserDataTypeDAO extends BaseDAO<UserDatatype> {

	public UserDatatype getById(int userId, int datatypeId, Boolean lock) throws Exception;
	
	public List<UserDatatype> getByUserId(int userId, Boolean lock) throws Exception;

	public List<UserDatatype> getByDataTypeId(int dataTypeId, Boolean lock) throws Exception;
	
	public void saveOrUpdate(UserDatatype userDataType) throws Exception;
	
	public void delete(UserDatatype userDataType) throws Exception;
	
	public JSONArray getJSONByUserId(int userId,Boolean lock) throws Exception;
}
