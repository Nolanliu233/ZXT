package cn.tongyuankeji.dao.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.user.UserDatatype;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用户和消息口多对多关联
 * @author 代平 2017-09-02 创建
 */
@Repository
public class UserDataTypeDAOImpl extends BaseDAOImpl<UserDatatype, Integer> implements UserDataTypeDAO {

	protected UserDataTypeDAOImpl() {
		super(UserDatatype.class, "sysId");
	}

	@Override
	public UserDatatype getById(int userId, int datatypeId, Boolean lock) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.eq("datatypeId", datatypeId));
		return super.getFirst(criterions, null, lock);
	}

	@Override
	public List<UserDatatype> getByUserId(int userId, Boolean lock) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		return super.getEntityList(criterions, null, lock);
	}

	@Override
	public List<UserDatatype> getByDataTypeId(int dataTypeId, Boolean lock) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("datatypeId", dataTypeId));
		return super.getEntityList(criterions, null, lock);
	}

	@Override
	public void saveOrUpdate(UserDatatype userDataType) throws Exception {
		getSession().save(userDataType);
	}

	@Override
	public void delete(UserDatatype userDataType) throws Exception {
		getSession().delete(userDataType);
	}

	@Override
	public JSONArray getJSONByUserId(int userId, Boolean lock) throws Exception {
		String[] resultFeild = new String[]{"sysId","userId","dataTypeId","dataTypeName"};
		JSONArray jsonArray = new JSONArray();
		List<Object[]> list = (List<Object[]>) super.createSQLQuery(String.format(
				"select i.sys_id, i.user_id, i.datatype_id,t.TypeName "
				+ "from user_datatype AS i left join datatype AS t on i.datatype_id = t.TypeID "
				+"where i.user_id = %d",
				userId
				)).getResultList();
		for (Object[] objects : list) {
			JSONObject jsonObject = new JSONObject();
			for(int i=0;i<objects.length;i++){
				jsonObject.put(resultFeild[i], objects[i]);
			}
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

}
