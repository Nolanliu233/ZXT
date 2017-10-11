package cn.tongyuankeji.dao;


import java.sql.Timestamp;
import java.util.List;

public interface BaseDAO<T>
{
	public abstract boolean hasActiveSession();
	
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
	 * @return
	 *         总是返回一个ReadonlyTable的链，但可能是isEmpty()
	 */
	public abstract List<ReadonlyTable> call(final String spCall, final Integer pageStart, final Integer pageSize, final Object... vars);
	
	public abstract Timestamp now();

	public abstract java.util.Date today();
	
	public abstract Integer thisYear();
}
