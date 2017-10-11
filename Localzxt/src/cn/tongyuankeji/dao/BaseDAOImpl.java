package cn.tongyuankeji.dao;


import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.query.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.tongyuankeji.common.util.Utils;

/**
 * 支持单主键的DAO抽象基类
 * 
 * @author 代平 2017-05-02 创建
 */
public abstract class BaseDAOImpl<T, ID extends Serializable> extends AbstractDAOImpl<T>
{
	private String idProperty = null; // 如，sysId

	/**
	 * ctor
	 * 
	 * @param clsEntity
	 *            Entity实例类，如Role.class
	 * @param idProperty
	 *            Entity主键属性名，如sysId
	 */
	protected BaseDAOImpl(Class<T> clsEntity, String idProperty)
	{
		super(clsEntity);
		this.idProperty = idProperty;
	}

	/*--------------------- 取单个T实例方法 ---------------------*/

	/**
	 * 用主键取得Entity实例，可支持记录锁。
	 * 
	 * @param id
	 *            主键值
	 * @param lock
	 *            是否加锁
	 * @param stateMin
	 *            是否要过滤状态。表中没有设计状态列的，传入null。否则，只返回状态 >= stateMin 值
	 * @return
	 *         Entity实例。注意要检查 返回值 != null
	 */
	@SuppressWarnings("deprecation")
	public final T getById(ID id, Byte stateMin, boolean lock) throws Exception
	{
		assert id != null : "getById(ID, Byte, boolean)参数id不能为空！";

		Criteria c = super.getSession().createCriteria(super.entityType).add(Restrictions.eq(this.idProperty, id));
		if (stateMin != null)
			c.add(Restrictions.ge("state", stateMin.byteValue()));
		if (lock)
			c.setLockMode(LockMode.UPGRADE);

		return (T) c.uniqueResult();
	}

	/**
	 * 用组合查询条件取得<b>第一个</b>符合条件的Entity实例，可支持记录锁。
	 * 
	 * @param criterions
	 *            查询条件。
	 * @param orders
	 *            排序，不排序的传入null。
	 * @param lock
	 *            是否加锁
	 * @return
	 *         Entity实例。注意要检查 返回值 != null
	 */
	@SuppressWarnings("deprecation")
	public final T getFirst(Criterion[] cris, Order[] orders, boolean lock) throws Exception
	{
		assert cris != null && cris.length > 0 : "getFirst(Criterion[], Order, boolean) 参数cris不能为空！";

		Criteria c = super.buildCriteria(cris, orders)
				.setFirstResult(0)
				.setMaxResults(1);

		if (lock)
			c.setLockMode(LockMode.UPGRADE);

		List result = c.list();
		if (!result.isEmpty())
			return (T) result.get(0);

		return null;
	}

	/**
	 * 参见 getFirst(Criterion[] cris, Order[] orders, boolean lock)
	 */
	@SuppressWarnings("deprecation")
	public final T getFirst(List<Criterion> cris, List<Order> orders, boolean lock) throws Exception
	{
		assert cris != null && cris.size() > 0 : "getFirst(List<Criterion>, Order, boolean) 参数cris不能为空！";

		Criteria c = super.buildCriteria(cris, orders)
				.setFirstResult(0)
				.setMaxResults(1);

		if (lock)
			c.setLockMode(LockMode.UPGRADE);

		List result = c.list();
		if (!result.isEmpty())
			return (T) result.get(0);

		return null;
	}

	/*--------------------- 取多个T实例方法 ---------------------*/
	/**
	 * 用组合查询条件取得Entity实例链，可支持记录锁。
	 * 
	 * @param criterions
	 *            查询条件
	 * @param orders
	 *            排序，不排序的传入null
	 * @param lock
	 *            是否加记录锁
	 * @return
	 *         Entity实例（链），注意要检查 返回值!=.isEmpty
	 */
	@SuppressWarnings("deprecation")
	protected final List<T> getEntityList(Criterion[] cris, Order[] orders, boolean lock) throws Exception
	{
		Criteria c = super.buildCriteria(cris, orders);

		if (lock)
			c.setLockMode(LockMode.UPGRADE);

		return c.list();
	}

	/**
	 * 参见 getEntityList(Criterion[] cris, Order[] orders, boolean lock)
	 */
	@SuppressWarnings("deprecation")
	protected final List<T> getEntityList(List<Criterion> criterions, List<Order> orders, boolean lock) throws Exception
	{
		Criteria c = super.buildCriteria(criterions, orders);

		if (lock)
			c.setLockMode(LockMode.UPGRADE);

		return c.list();
	}

	/**
	 * 用组合查询条件取得Entity实例链，支持分页，无记录锁。
	 * 
	 * @param criterions
	 *            查询条件。
	 * @param orders
	 *            排序，不排序的传入null。
	 * @param pageNo
	 *            第几页。不分页时，传入null。
	 * @param pageSize
	 *            一页装多少。不分页时，传入null。
	 * @return
	 *         Entity实例（链），注意要检查 返回值!=.isEmpty
	 */
	protected final List<T> getEntityPage(Criterion[] criterions, Order[] orders, Integer pageStart, Integer pageSize) throws Exception
	{
		Criteria c = super.buildCriteria(criterions, orders);

		if (pageStart != null && pageSize != null)
			c.setFirstResult(pageStart).setMaxResults(pageSize);

		return c.list();
	}

	/**
	 * 参见 getEntityPage(Criterion[] criterions, Order[] orders, Integer pageStart, Integer pageSize)
	 */
	protected final List<T> getEntityPage(List<Criterion> criterions, List<Order> orders, Integer pageStart, Integer pageSize) throws Exception
	{
		Criteria c = super.buildCriteria(criterions, orders);

		if (pageStart != null && pageSize != null)
			c.setFirstResult(pageStart).setMaxResults(pageSize);

		return c.list();
	}

	/**
	 * 用HQL查询语句取得Entity实例链，支持分页，无记录锁。
	 * 
	 * @param hql
	 *            查询HQL
	 * @param pageStart
	 *            起始记录index。不分页时，传入null。
	 * @param pageSize
	 *            返回多少条数据，pageStart为null时。不分页时，传入null。
	 * @param vars
	 *            hql中变量值。hql中变量应用:varN命名，N从1开始
	 * @return
	 *         Entity实例（链），注意要检查 返回值!=.isEmpty
	 */
	protected final List<T> getEntityPage(String hql, Integer pageStart, Integer pageSize, Object... vars) throws Exception
	{
		assert !Utils.isBlank(hql) : "getEntityPage(String, Integer, Integer, Object...) 参数hql不能为空！";

		Query q = super.getSession().createNativeQuery(hql).addEntity(super.entityType);
		if (vars != null && vars.length > 0)
		{
			for (int i = 0; i < vars.length; i++)
			{
				if (Utils.isContainString(hql, "var" + (i + 1), true)) // 有可能有值，没有参数名（因为外面的hql可能是StringBuffer拼出来的），跳过
					q.setParameter("var" + (i + 1), vars[i]);
			}
		}

		if (pageStart != null && pageSize != null)
		{
			q.setFirstResult(pageStart);
			q.setMaxResults(pageSize);
		}

		return q.list();
	}

	/*--------------------- 取记录数方法 ---------------------*/

	/**
	 * 用组合查询条件取得符合条件的记录条数，无记录锁。
	 * 
	 * @param criterions
	 *            查询条件。
	 */
	public final int getCount(Criterion[] criterions) throws Exception
	{
		assert criterions != null && criterions.length > 0 : "getCount(Criterion[]) 参数criterions不能为空！";

		return ((Number) super.buildCriteria(criterions, null).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	/**
	 * 参见 public final int getCount(Criterion[] criterions) throws Exception
	 */
	public final int getCount(List<Criterion> criterions) throws Exception
	{
		return ((Number) super.buildCriteria(criterions, null).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	/*--------------------- Helper ---------------------*/
	/**
	 * 取某个逻辑自增长顺序的下一个可用值，如Dict.DisplayOrder
	 * 
	 * @param displayOrderPropertyName
	 *            排序用属性名称，如Dict.displayOrder
	 * 
	 * @param criterions
	 *            在有分组时，提供分组条件，如Dict.kind。没有分组条件时，传入null.
	 * 
	 * @return
	 *         返回值根据数据库中字段设计，取.intValue(), .byteValue(), .shortValue()等
	 */
	public final Number getNextDisplayOrder(String displayOrderPropertyName, Criterion[] criterions) throws Exception
	{
		assert !Utils.isBlank(displayOrderPropertyName) : "getNextDisplayOrder(String, Criterion[]) 参数displayOrderPropertyName不能为空！";

		Criteria criteria = this.buildCriteria(criterions, null).setProjection(Projections.max(displayOrderPropertyName));
		Object nextNum = criteria.uniqueResult();
		if (nextNum == null)
			return 1;
		else
			return (Number) ((Integer)nextNum + 1);
	}

	/**
	 * 取某个逻辑自增长字段的下一个可用值，如Course.ECode
	 * 
	 * @param codePropertyName
	 *            属性名称，如eCode
	 * @param prefix
	 *            前缀，如Course的前缀是用户输入的课程简拼。无前缀时，传入null。
	 * @param length
	 *            字段长度
	 */
	protected final String getNexteCode(String eCodePropertyName, String prefix, int length) throws Exception
	{
		assert !Utils.isBlank(eCodePropertyName) : "getNexteCode(String, String, int) 参数eCodePropertyName不能为空！";
		assert length > 0 : "getNexteCode(String, String, int) 参数length应大于0！";

		String sql = null;
		Integer nextNum = 1;

		if (!Utils.isBlank(prefix))
			sql = String.format("SELECT MAX(SUBSTRING(t." + eCodePropertyName + ", %d)) FROM %s t", prefix.length() + 1, super.entityType.getName());
		else
			sql = String.format("SELECT MAX(t." + eCodePropertyName + ") FROM %s t", super.entityType.getName());

		String code = (String) getSession().createQuery(sql).uniqueResult();

		if (!Utils.isBlank(code))
			nextNum = Integer.valueOf(code) + 1;

		if (!Utils.isBlank(prefix))
			return prefix + Utils.amendZero(nextNum.toString(), length - prefix.length(), true);
		else
			return Utils.amendZero(nextNum.toString(), length, true);
	}
	
}