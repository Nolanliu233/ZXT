package cn.tongyuankeji.manager.information;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tongyuankeji.common.parameters.EnumContentPageState;
import cn.tongyuankeji.common.parameters.EnumSolarSortField;
import cn.tongyuankeji.entity.Person;
import cn.tongyuankeji.entity.information.InformationSources;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public interface InformationSourcesService {

	/**
	 * 新增信息来源记录
	 * @param infoSources
	 */
	public void saveInformationSources(InformationSources infoSources) throws Exception;
	
	/**
	 * 根据ID得到信息来源记录
	 * @param infoSourcesId
	 * @return InformationSources
	 */
	public InformationSources getInfoSourcesById(int infoSourcesId, EnumContentPageState stateMin, Boolean lock) throws Exception;
	
	/**
	 * 根据ID得到JSON格式的信息来源记录
	 * @param infoSourcesId
	 * @return JSONObject
	 */
	public JSONObject getJSONInfoSourcesById(int infoSourcesId, Boolean isReview) throws Exception;
	
	/**
	 * 后台管理获取信息符合条件的记录（调用存储过程）
	 * @param fuzzy 模糊查询关键字
	 * @param typeIds 消息口数组
	 * @param createdAtBegin 创建记录开始时间
	 * @param createdAtEnd 常见记录结束时间
	 * @param deadLineAtBegin 申报截止如期开始日期
	 * @param deadLineAtEnd 申报截止如期结束日期
	 * @param pageStart 
	 * @param pageSize
	 * */
	public JSONObject searchInfoList(String fuzzy, int[] typeIds, Date createdAtBegin, Date createdAtEnd,
			Date deadLineAtBegin, Date deadLineAtEnd, EnumSolarSortField sortFeild, Boolean isEsc, Integer pageStart, Integer pageSize) throws Exception;

	/**
	 * 得到点击次数最多的消息
	 * @param nowPage
	 * @param pageSize
	 * @param state
	 * @param orderBy
	 * @return JSONArray
	 */
	public JSONArray getHotInfos(int nowPage,int pageSize,Byte state,String orderBy) throws Exception;
	
	/**-------------------------------后台管理方法------------------------------------------------------**/

	public JSONObject searchInfoDict() throws Exception;
	
	/**
	 * 后台管理获取信息符合条件的记录（调用存储过程）
	 * @param fuzzy 模糊查询关键字
	 * @param areaId 区域ID
	 * @param levelId 等级ID
	 * @param state 信息状态
	 * @param pageStart 
	 * @param pageSize
	 * */
	public JSONObject backGetList(String fuzzy, Integer areaId, Integer levelId, EnumContentPageState state, Integer pageStart, Integer pageSize) throws Exception;

	/**
	 * 后台管理获取信息详细数据
	 * */
	public JSONObject backGetInfoDetail(Integer infoId) throws Exception;

	/**
	 * 后台管理获取信息附件列表
	 * */
	public JSONArray getInfoAttachByInfoId(Integer infoId) throws Exception;
	
	/**
	 * 后台管理删除附件
	 * */
	public void deleteAttach(Integer attachId) throws Exception;

	/**
	 * 后台管理修改附件信息
	 * */
	public void editAttach(Integer attachId, String dispalyName, String AttachUrl) throws Exception;
	

	/**
	 * 后台管理新增附件（只限于增加连接方式，上传用uploadHandler）
	 * */
	public void addAttach(Person person, Integer infoId, String dispalyName, String AttachUrl) throws Exception;
	/**
	 * 修改保存信息数据
	 * @return 
	 * */
	public Byte editInformation(Integer infoId, String title, Date releaseAt, Date beginAt, Date endAt,
			String detailSource, String content) throws Exception;

	/**
	 * 更改信息状态
	 * **/
	public void changeInfoStatus(int infoId, EnumContentPageState toStatus, String remarks) throws Exception;


}
