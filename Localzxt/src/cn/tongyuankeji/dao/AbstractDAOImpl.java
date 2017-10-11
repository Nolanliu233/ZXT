package cn.tongyuankeji.dao;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import cn.tongyuankeji.common.annotation.EntityDesc;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.RunSettingImpl;
import cn.tongyuankeji.entity.BaseEntity;
import cn.tongyuankeji.entity.Trace;

/**
 * 所有BaseXXXDAOImpl的基类
 * 
 * @author 代平 2017-06-10 创建
 */
public abstract class AbstractDAOImpl<T>
{
	static final Logger log = LogManager.getLogger(AbstractDAOImpl.class.getName());

	/**
	 * 在SiteInitializer.ctor()中设置
	 * 使用MySql数据库时，设为true；使用sql server时，设为false
	 */
	public static void setIS_MYSQL(boolean iS_MYSQL)
	{
		AbstractDAOImpl.IS_MYSQL = iS_MYSQL;
		log.info("AbstractDAOImpl initialize database: {}", (AbstractDAOImpl.IS_MYSQL ? "MySql" : "Sql Server"));
	}

	protected static Boolean IS_MYSQL = true;

	public static void setLOG_TABLE_NAME(String lOG_TABLE_NAME)
	{
		AbstractDAOImpl.LOG_TABLE_NAME = lOG_TABLE_NAME;
		log.info("AbstractDAOImpl initialize log table: {}", AbstractDAOImpl.LOG_TABLE_NAME);
	}

	protected static String LOG_TABLE_NAME = "";

	protected SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession()
	{
		return this.sessionFactory.getCurrentSession();
	}

	public boolean hasActiveSession()
	{
		return this.sessionFactory != null && this.sessionFactory.getCurrentSession() != null;
	}

	/**
	 * 从session缓存中去掉entity和数据库的关联，避免hibernate自动将实例的更改应用到数据库
	 */
	protected void detach(BaseEntity entity)
	{
		Session session = this.sessionFactory.getCurrentSession();
		if (session != null)
			session.evict(entity);
	}

	protected final static int LOG_BODY_LENGTH = 500;

	protected Class<T> entityType = null; // 如，Role.class

	/**
	 * ctor
	 * 
	 * @param clsEntity
	 *            Entity实例类，如Role.class
	 */
	protected AbstractDAOImpl(Class<T> clsEntity)
	{
		assert BaseEntity.class.isAssignableFrom(clsEntity) : "AbstractDAOImpl ctor: clsEntity必须是BaseEntity派生类！";

		this.entityType = clsEntity;
	}

	/*--------------------- 非Entity方法 ---------------------*/

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
	public final List<Object[]> getPage(String sql, Integer pageStart, Integer pageSize, Object... vars) throws Exception
	{
		return GenericDAOImpl.getPage(this.getSession(), sql, pageStart, pageSize, vars);
	}

	/*--------------------- 调用存储过程 ---------------------*/
	public final NativeQuery createSQLQuery(String sql, Object... args)
	{
		return this.getSession().createNativeQuery((args != null && args.length > 0) ? String.format(sql, args) : sql);
	}

	public final Query createQuery(String hql, Object... args)
	{
		return this.getSession().createQuery((args != null && args.length > 0) ? String.format(hql, args) : hql);
	}

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
	public final List<ReadonlyTable> call(final String spCall, final Integer pageStart, final Integer pageSize, final Object... vars)
	{
		return GenericDAOImpl.call(this.getSession(), spCall, pageStart, pageSize, vars);
	}
	
	public final void exec(final String spName, final Object... vars) 
	{
		GenericDAOImpl.exec(this.getSession(), spName, vars);
	}

	/*--------------------- private helper ---------------------*/

	protected Criteria buildCriteria(Criterion[] criterions, Order[] orders) throws Exception
	{
		Criteria criteria = this.getSession().createCriteria(entityType);

		if (criterions != null && criterions.length > 0)
		{
			for (int i = 0; i < criterions.length; i++)
				criteria.add(criterions[i]);
		}

		if (orders != null && orders.length > 0)
		{
			for (int i = 0; i < orders.length; i++)
				criteria.addOrder(orders[i]);
		}
		return criteria;
	}

	protected Criteria buildCriteria(List<Criterion> criterions, List<Order> orders) throws Exception
	{
		Criteria criteria = this.getSession().createCriteria(entityType);

		if (criterions != null && criterions.size() > 0)
		{
			for (Criterion c : criterions)
				criteria.add(c);
		}

		if (orders != null && orders.size() > 0)
		{
			for (Order o : orders)
				criteria.addOrder(o);
		}
		return criteria;
	}

	/*--------------------- 日志 ---------------------*/
	// SiteLog使用MyISAM，不支持transaction，所以记录日志产生的错误不抛出异常

	/**
	 * 后台管理功能添加日志。
	 * 
	 * @param trace
	 *            日志对象。
	 * @param entrySaved
	 *            被保存的实例。
	 */
	public final void addTrace(Trace trace, BaseEntity entrySaved)
	{
		assert entrySaved != null : "addTrace(Trace, T) 参数 T 不能为空！";

		EntityDesc ann = null;
		String body = this.entityType.getSimpleName();
		StringBuffer sb = null;
		Method[] ms = null;
		String fieldName = null;
		Object val = null;

		if (trace == null)
			return;

		// build system log info
		if (RunSettingImpl.isENTITY_LOG_ON())
		{
			sb = new StringBuffer();
			ms = entrySaved.getClass().getMethods();

			for (int i = 0; i < ms.length; i++)
			{
				fieldName = ms[i].getName();
				if (!fieldName.startsWith("get"))
					continue;

				fieldName = BaseEntity.getterMethodName2FieldName(fieldName);

				if ((ann = ms[i].getAnnotation(EntityDesc.class)) != null && ann.traceOn())
				{
					if (sb.length() > 0)
						sb.append(",");
					sb.append(fieldName);
					sb.append("=");
					try
					{
						val = ms[i].invoke(entrySaved);
					}
					catch (Exception ex)
					{
						log.warn("addTrace invokation failed", ex);
					}

					if (val != null)
						sb.append(val.toString());
				}

				// 系统日志最多可记录500字
				if (sb.length() >= AbstractDAOImpl.LOG_BODY_LENGTH - body.length() - 1)
					break;
			}

			sb.insert(0, this.entityType.getSimpleName() + ":");

			if (sb.length() > AbstractDAOImpl.LOG_BODY_LENGTH)
				body = sb.substring(0, AbstractDAOImpl.LOG_BODY_LENGTH - 1);
			else
				body = sb.toString();
		}

		this.addTraceImpl(trace, body);
	}

	/**
	 * 添加用户日志。使用前，应先设置Trace.setFriendlyBody()。如果需要，应设置Trace.setActionRecord()
	 * 
	 * @param trace
	 *            日志对象。
	 * @param entryName
	 *            被保存的实例名称。
	 */
	protected final void addTrace(Trace trace)
	{
		assert !Utils.isBlank(trace.getFriendlyBody()) : "调用addTrace(Trace)前，应先设置FriendlyBody";
		
		if (trace == null)
			return;

		this.addTraceImpl(trace, null);
	}

	protected void addTraceImpl(Trace trace, String body)
	{
		if (trace == null)
			return;

		assert trace.getDomainType() > 0 : "你是不是忘记了调用Trace.setDomain()？";

		try
		{
			//TODO 代平 自动记录日志功能，表数据需更改后启用
			/*this.createSQLQuery("INSERT INTO %s (OperatorKind, OwnerGovId, OperatorId, DomainType, DomainTable, DomainKey, ActionAt, ActionMethod, ActionIP, ActionURI, Body, FriendlyBody)"
					+ " VALUES (%d, %d, %d, %d, %d, :k, %s, :m, :p, :u, :b, :f);",
					AbstractDAOImpl.LOG_TABLE_NAME,					
					trace.getOperatorKind(),
					trace.getOwnerGovId(),
					trace.getOperatorId(),
					trace.getDomainType(),					
					trace.getDomainTable(),					
					AbstractDAOImpl.SQL_NOW())
					.setString("k", trace.getDomainKey())
					.setString("m", trace.getActionMethod())
					.setString("p", trace.getActionIP())
					.setString("u", trace.getActionURI())
					.setString("b", body) // 系统日志最多可记录500字
					.setString("f", trace.getFriendlyBody())
					.executeUpdate();*/
		}
		catch (Exception ex)
		{
			// 记录日志时发生的错误可忽略
			log.warn("Failed to add trace", ex);
		}
	}

	/*--------------------- 数据库时间 ---------------------*/

	/**
	 * （数据库）当前时间，可用于Entity赋值。建议使用DateUtils操作
	 */
	public final Timestamp now()
	{
		return (Timestamp) getSession().createSQLQuery("SELECT " + AbstractDAOImpl.SQL_NOW()).uniqueResult();
	}

	/**
	 * （数据库）今天，可用于Entity赋值。建议使用DateUtils操作
	 */
	public final java.util.Date today()
	{
		return (java.util.Date) getSession().createSQLQuery("SELECT " + AbstractDAOImpl.SQL_TODAY()).uniqueResult();
	}

	/**
	 * （数据库）今年
	 */
	public final Integer thisYear()
	{
		return ((Number) getSession().createSQLQuery("SELECT "
				+ (AbstractDAOImpl.IS_MYSQL ? "YEAR(CURRENT_DATE())" : "CONVERT(INT, SUBSTRING(CONVERT(VARCHAR(10), GETDATE(), 120), 1, 4))")).uniqueResult()).intValue();
	}

	public final static String SQL_NOW()
	{
		return (AbstractDAOImpl.IS_MYSQL ? "CURRENT_TIMESTAMP()" : "GETDATE()");
	}

	public final static String SQL_TODAY()
	{
		return (AbstractDAOImpl.IS_MYSQL ? "CURRENT_DATE()" : "CONVERT(DATE, GETDATE())");
	}
}