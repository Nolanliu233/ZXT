package cn.tongyuankeji.dao.information;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.information.InformationSources;
import net.sf.json.JSONObject;

/**
 * 
 * @author 刘磊
 * @date 2017年8月18日
 * @Description 
 */
public interface InformationSourcesDao extends BaseDAO<InformationSources>{

	/**
	 * 新增或修改信息来源记录
	 * @param info
	 */
	public void saveOrUpdateInfoSource(InformationSources info);
	
	/**
	 * 根据ID得到信息来源记录
	 * @param infoSourceId
	 * @return InformationSources
	 */
	public InformationSources getInfoSourceById(int infoSourceId, EnumContentPageState minState, Boolean islock) throws Exception;
	
	/**
	 * 分页查询信息来源列表
	 * @param nowPage(当前页)
	 * @param pageSize(每页记录数)
	 * @param strings(查询参数)
	 * @param startTime(信息查询开始时间)
	 * @param endTime(信息查询结束时间)
	 * @param orderBy (排序方式，可为空，为空默认以time排序)
	 * @return List<InformationSources>
	 */
	public List<InformationSources> getInfoSourceList(int nowPage,int pageSize,String title,Byte state,Timestamp startTime,Timestamp endTime,String orderBy) throws Exception;
	
	/**
	 * 得到查询总记录数
	 * @param title
	 * @return count
	 */
	public int getInfoSourceCount(String title,Byte state,Timestamp startTime,Timestamp endTime);

	public JSONObject getInfoZhById(int infoSourceId) throws Exception;

}
