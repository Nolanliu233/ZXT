package cn.tongyuankeji.dao;


import java.sql.Timestamp;
import java.util.List;

import org.hibernate.query.NativeQuery;

public interface GenericDAO
{
	public abstract boolean hasActiveSession();
	
	/**
	 * 用SQL查多条记录，一般用于没有对应的Entity类时（如，join），返回Object[]的链，支持分页，无记录锁。
	 * 
	 * @param sql
	 *            SQL语句。如有变量，在sql中用:var。<br />
	 *            注意：索引从1开始，如var1, var2, ...<br />
	 *            如, SELECT * FROM course WHERE SysId = :var1
	 * 
	 * @param pageStart
	 *            开始页。如不需分页，传入null。
	 * 
	 * @param pageSize
	 *            本页选多少条记录。如不需分页，传入null。
	 * @param vars
	 *            hql中变量值
	 */
	public abstract List<Object[]> getPage(String sql, Integer pageStart, Integer pageSize, Object... vars) throws Exception;
	
	/**
	 * 取记录数。参见getPage()说明
	 */
	public abstract int getCount(String sql, Object... vars) throws Exception;
	
	public abstract NativeQuery createSQLQuery(String sql, Object... args);
	
	/**
	 * 用存储过程查多条记录，返回ReadonlyTable链，支持分页，无记录锁。
	 * 
	 * @param spCall
	 *            存储过程调用，如有变量，使用 ? 占位，如，getCourseWaterfall(?,?)<br />
	 *            如果使用分页，最后两个 ?,? 预留给_pageStart和_pageSize
	 * 
	 * @param pageStart
	 *            开始页。如不需分页，传入null。
	 * 
	 * @param pageSize
	 *            本页选多少条记录。如不需分页，传入null。
	 * 
	 * @param vars
	 *            hql中变量值。必须提供和spCall中出现的 ? 相同个数，即使是空，应用null占位
	 * 
	 * @return 总是返回一个ReadonlyTable的链，但可能是isEmpty()
	 */
	public abstract List<ReadonlyTable> call(final String spCall, final Integer pageStart, final Integer pageSize, final Object... vars);
	
	/**
	 * 执行存储过程，修改数据库记录
	 * 
	 * @param spName
	 *            存储过程调用
	 * @param vars
	 *            hql中变量值。必须提供和spName中出现的 ? 相同个数，即使是空，应用null占位
	 */
	public abstract void exec(final String spName, final Object... vars);
	
	public abstract Timestamp now();
	
	public abstract java.util.Date today();
}