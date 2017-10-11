package cn.tongyuankeji.common.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import cn.tongyuankeji.common.cache.McPerson;
import cn.tongyuankeji.dao.structure.GovernmentDAO;
import cn.tongyuankeji.entity.structure.Government;
import cn.tongyuankeji.entity.user.User;
import cn.tongyuankeji.entity.structure.BackUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Person;

/**
 * 对园区相关资料的控制器
 * 
 * @author Jean 2015-11-21 创建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GovControllerImpl implements GovController
{
	@Autowired
	private GovernmentDAO govDao;
	
	/**
	 * （编辑）选择用loginUser.ownerGovId，还是desiredOwnerGovId。<br />
	 * 不是默认园区用户的，总是使用loginUser.ownerGovId <br />
	 * 是默认园区用户的，且desiredOwnerGovId不为空，就用desiredOwnerGovId，否则用登录人loginUser.ownerGovId
	 * 
	 * @param loginUser
	 *            操作人
	 * @param desiredOwnerGovId
	 *            默认园区时，提供要操作哪个园区
	 */
	public final static Integer replaceOwnerGovId(Person loginUser, Integer desiredOwnerGovId)
	{
		if (!ConstantBase.DEFAULT_OWNER_ORG_ID.equals(loginUser.getOwnerGovId()))
			return loginUser.getOwnerGovId();
		else
			return desiredOwnerGovId == null ? loginUser.getOperatorId() : desiredOwnerGovId;
	}
	
	/**
	 * （查询）选择用loginUser.ownerGovId，还是desiredOwnerGovId。<br />
	 * 不是默认园区用户的，总是使用loginUser.ownerGovId <br />
	 * 是默认园区用户的，总是返回desiredOwnerGovId（无论是否为空）
	 * 
	 * @param loginUser
	 *            操作人
	 * @param desiredOwnerGovId
	 *            默认园区时，提供要操作哪个园区
	 */
	public final static Integer searchOwnerGovId(Person loginUser, Integer desiredOwnerGovId)
	{
		if (!ConstantBase.DEFAULT_OWNER_ORG_ID.equals(loginUser.getOwnerGovId()))
			return loginUser.getOwnerGovId();
		else
			return desiredOwnerGovId;
	}
	
	/**
	 * 操作用户是默认园区用户时，可管理所有entity。否则，只能管理自己园区的
	 * 
	 * @param loginUser
	 *            操作人
	 * @param entity
	 *            被检查的实例
	 */
	public final static String isMyOwnerGov(McPerson loginUser, BaseEntity entity)
	{
		if (entity instanceof User)
			return GovControllerImpl.isMyOwnerGov(loginUser, ((User) entity).getOwnerGovId());
		else if (entity instanceof BackUser)
			return GovControllerImpl.isMyOwnerGov(loginUser, ((BackUser) entity).getOwnerGovId());
		
		else
			return "unknown entity type";
	}
	
	public final static String isMyOwnerGov(McPerson loginUser, Integer entityOwnerGovId)
	{
		if (ConstantBase.DEFAULT_OWNER_ORG_ID.equals(loginUser.getOwnerGovId()))
			return null;
		
		if (loginUser.getOwnerGovId().equals(entityOwnerGovId))
			return null;
		
		return "这不是您的园区！";
	}
	
	/**
	 * 根据当前登录用户返回他可见的机构列表
	 * 
	 * @param loginPerson
	 * @param bird
	 *            是否需返回默认园区
	 * @param govId
	 *            目标私企园区id（不能传入默认园区）。只在默认园区用户时使用。有值时，会排除其他私企园区。不使用此条件时，传入null
	 */
	
	@Override
	public JSONObject getVisibleGov(McPerson loginPerson, boolean defaultPark, Integer govId) throws Exception
	{
		JSONObject oresult = new JSONObject();
		JSONArray aresult = new JSONArray();
		List<Government> lst = null;
		
		// 私企园区用户登录时，总是只能看自己私企园区的
		if (!ConstantBase.DEFAULT_OWNER_ORG_ID.equals(loginPerson.getOwnerGovId()))
			govId = loginPerson.getOwnerGovId();
		
		if (govId != null)
		{
			// 私企用户登录的
			
			if (defaultPark)
			{
				// 默认园区+私企
				lst = govDao.getInIds(ConstantBase.DEFAULT_OWNER_ORG_ID, govId);
			}
			else
			{
				// 只需要私企自己（一般用在私企园区用户 选用户，选培训班等）
				lst = govDao.getInIds(null, govId);
			}
		}
		else
		{
			// 默认园区用户登录的
			
			// 全部园区
			// 其他私企（一般用在默认园区用户 选用户，选培训班等）
			lst = govDao.getInIds(null, null);
		}
		
		for (Government gov : lst)
		{
			// 需要排除默认园区时
			if (ConstantBase.DEFAULT_OWNER_ORG_ID.equals(gov.getSysId())
					&& !defaultPark)
				continue;
			
			this.appendGov(gov, aresult);
		}
		
		oresult.put("isDefaultGov", ConstantBase.DEFAULT_OWNER_ORG_ID.equals(loginPerson.getOwnerGovId())); // 默认园区用户？
		oresult.put("ownerGovId", loginPerson.getOwnerGovId());
		oresult.put("info", aresult);
		
		return oresult;
	}
	
	private void appendGov(Government gov, JSONArray agov)
	{
		JSONObject oentry = new JSONObject();
		oentry.put("sysId", gov.getSysId());
		oentry.put("title", gov.getTitle());
		agov.add(oentry);
	}

}
