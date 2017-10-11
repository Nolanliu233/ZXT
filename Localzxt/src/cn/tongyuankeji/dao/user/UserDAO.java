package cn.tongyuankeji.dao.user;

import java.util.List;
import cn.tongyuankeji.entity.user.User;

import cn.tongyuankeji.common.parameters.EnumUserState;
import cn.tongyuankeji.dao.BaseDAO;

public interface UserDAO extends BaseDAO<User>
{
	/**
	 * 根据id查找用户
	 */
	public abstract User getById(int studentId, EnumUserState stateMin, boolean lock) throws Exception;
	
	/**
	 * 用登录字段查找用户：注册手机号、学号，不排除deleted状态的
	 * 
	 * @param loginId
	 *            注册手机号、学号
	 * @param checkECode
	 *            true - 同时检查student.ECode字段
	 */
	public abstract User getByLoginId(String loginId, boolean lock) throws Exception;
	
	/**
	 * 按Student.SysId取多个用户
	 */
	public abstract List<User> getByIds(Integer[] studentIds, EnumUserState stateMin, boolean lock) throws Exception;
	
	/**
	 * 用户注册，同时创建StudentEx。StudentEx由Manager单独保存。<br />
	 */
	public abstract void insert(User transientInstance) throws Exception;
	
	/**
	 * 用户修改个人信息保存。StudentEx更新由manager执行
	 */
	public abstract void update(User persistentInstance) throws Exception;
	
	/**
	 * 用户登录后，更新数据库最新登录XXX
	 */
	public abstract int updateLogin(User persistentInstance);
	
	/**
	 * 用户修改密码
	 * 
	 * @param oldPwdEncrypted
	 *            用户输入的现有密码（密文）
	 * @param newPwdEncrypted
	 *            新密码（密文）
	 */
	public abstract int updatePwd(User persistentInstance, String oldPwdEncrypted, String newPwdEncrypted) throws Exception;
	
	/**
	 * 上传或清空头像后，修改数据库相应字段
	 * 
	 * @param filename
	 *            用FileUploadManager上传的图片。清空时，传入null
	 * @param trace
	 *            上传时，传入null。清空时，传入SchemaBase产生的trace对象
	 */
	public abstract int updateFile(User persistentInstance, String filename, String fileExt) throws Exception;
	
	
	public abstract void deleteUser(User user) throws Exception;
}
