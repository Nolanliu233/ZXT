package cn.tongyuankeji.dao;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;

import cn.tongyuankeji.common.util.Utils;

public class GenericDAOImpl implements GenericDAO
{
	protected SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession()
	{
		return sessionFactory.getCurrentSession();
	}

	public boolean hasActiveSession()
	{
		return sessionFactory != null && sessionFactory.getCurrentSession() != null;
	}

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
	@Override
	public final List<Object[]> getPage(String sql, Integer pageStart, Integer pageSize, Object... vars) throws Exception
	{
		return GenericDAOImpl.getPage(this.getSession(), sql, pageStart, pageSize, vars);
	}

	public final static List<Object[]> getPage(Session session, String sql, Integer pageStart, Integer pageSize, Object... vars) throws Exception
	{
		assert !Utils.isBlank(sql) : "getPage(String , Integer, Integer, Object...) 参数sql不能为空！";

		Query q = session.createNativeQuery(sql);
		if (vars != null && vars.length > 0)
		{
			for (int i = 0; i < vars.length; i++)
			{
				if (Utils.isContainString(sql, "var" + (i + 1), true)) // 有可能有值，没有参数名（因为外面的hql可能是StringBuffer拼出来的），跳过
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

	/**
	 * 取记录数。参见getPage()说明
	 */
	@Override
	public final int getCount(String sql, Object... vars) throws Exception
	{
		return GenericDAOImpl.getCount(this.getSession(), sql, vars);
	}

	public final static int getCount(Session session, String sql, Object... vars) throws Exception
	{
		assert !Utils.isBlank(sql) : "getCount(String, Object...) 参数sql不能为空！";

		Query q = session.createNativeQuery(sql);
		if (vars != null && vars.length > 0)
		{
			for (int i = 0; i < vars.length; i++)
			{
				if (Utils.isContainString(sql, "var" + (i + 1), true)) // 有可能有值，没有参数名（因为外面的hql可能是StringBuffer拼出来的），跳过
					q.setParameter("var" + (i + 1), vars[i]);
			}
		}

		return ((Number) q.uniqueResult()).intValue();
	}

	/*--------------------- 调用存储过程 ---------------------*/
	@Override
	public final NativeQuery createSQLQuery(String sql, Object... args)
	{
		return this.getSession().createNativeQuery(String.format(sql, args));
	}

	/**
	 * 用存储过程查多条记录，返回ReadonlyTable链，支持分页，无记录锁。
	 * 
	 * @param spCall
	 *            存储过程名称
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
	@Override
	public final List<ReadonlyTable> call(final String spCall, final Integer pageStart, final Integer pageSize, final Object... vars)
	{
		return GenericDAOImpl.call(this.getSession(), spCall, pageStart, pageSize, vars);
	}

	public final static List<ReadonlyTable> call(Session session, final String spName, final Integer pageStart, final Integer pageSize, final Object... vars)
	{
		assert !Utils.isBlank(spName) : "call(Session, String, Integer, Integer, Object...) 参数spName不能为空！";

		List<ReadonlyTable> resultOuter = session.doReturningWork(
				new ReturningWork<List<ReadonlyTable> /* objectType returned */>()
				{
					/* or object type you need to return to process */
					@Override
					public List<ReadonlyTable> execute(Connection cnn) throws SQLException
					{
						CallableStatement cstmt = null;
						List<ReadonlyTable> lstResult = null;
						ResultSet rs = null;
						int tblIdx = 0;

						// 注意：如果需要分页，SP的分页参数总是定义在最后
						String placeHolder = "";
						int idx = 0, argLength = (vars != null && vars.length > 0 ? vars.length * 2 : 0) + (pageStart != null && pageSize != null ? 4 : 0);
						if (argLength > 0)
						{
							char[] arrPlaceHolder = new char[argLength];

							for (; idx < arrPlaceHolder.length; idx += 2)
							{
								arrPlaceHolder[idx] = '?';
								arrPlaceHolder[idx + 1] = ',';
							}

							placeHolder = new String(arrPlaceHolder);
							placeHolder = placeHolder.substring(0, placeHolder.length() - 1); // remove last ','
						}

						if (AbstractDAOImpl.IS_MYSQL)
							cstmt = cnn.prepareCall(String.format("CALL %s(%s)", spName, placeHolder)); //("CALL getDict(2)");
						else
							cstmt = cnn.prepareCall(String.format("exec %s %s", spName, placeHolder));

						if (vars != null && vars.length > 0)
						{
							for (idx = 0; idx < vars.length; idx++)
								cstmt.setObject(idx + 1, vars[idx]); // JDBC参数的索引从1开始
						}

						if (pageStart != null && pageSize != null)
						{
							cstmt.setInt(idx + 1, pageStart);
							idx++;
							cstmt.setInt(idx + 1, pageSize);
						}

						//Result list that would return ALL rows of ALL result sets
						lstResult = new ArrayList<ReadonlyTable>(10);
						try
						{
							cstmt.execute();
							do
							{
								rs = cstmt.getResultSet();
								if (rs != null)
								{
									// convert java.sql.ResultSet to ReadonlyTable
									lstResult.add(new ReadonlyTable(rs, tblIdx));
									rs.close();
									rs = null;
									tblIdx++;
								}

							} while (cstmt.getMoreResults());
						}
						finally
						{
							if (rs != null)
								rs.close();

							cstmt.close();
						}

						return lstResult;
					}
				});

		return resultOuter;
	}

	/**
	 * 执行存储过程，修改数据库记录
	 * 
	 * @param spName
	 *            存储过程调用
	 * @param vars
	 *            hql中变量值。必须提供和spName中出现的 ? 相同个数，即使是空，应用null占位
	 */
	@Override
	public final void exec(final String spName, final Object... vars)
	{
		GenericDAOImpl.exec(this.getSession(), spName, vars);
	}

	public final static void exec(Session session, final String spName, final Object... vars)
	{
		assert !Utils.isBlank(spName) : "exec(Session, String, Object...) 参数spName不能为空！";

		session.doWork(
				new Work()
				{
					public void execute(Connection cnn) throws SQLException
					{
						PreparedStatement ps = null;
						String placeHolder = "";
						int idx = 0;
						if (vars != null && vars.length > 0)
						{
							char[] arrPlaceHolder = new char[vars.length * 2];

							for (; idx < arrPlaceHolder.length; idx += 2)
							{
								arrPlaceHolder[idx] = '?';
								arrPlaceHolder[idx + 1] = ',';
							}

							placeHolder = new String(arrPlaceHolder);
							placeHolder = placeHolder.substring(0, placeHolder.length() - 1); // remove last ','
						}

						if (AbstractDAOImpl.IS_MYSQL)
							ps = cnn.prepareStatement(String.format("CALL %s(%s)", spName, placeHolder)); //("CALL getDict(2)");
						else
							ps = cnn.prepareStatement(String.format("exec %s %s", spName, placeHolder));

						if (vars != null && vars.length > 0)
						{
							for (idx = 0; idx < vars.length; idx++)
								ps.setObject(idx + 1, vars[idx]); // JDBC参数的索引从1开始
						}

						try
						{
							ps.execute();
						}
						finally
						{
							ps.close();
						}
					}
				}
				);
	}

	/*--------------------- 数据库时间 ---------------------*/

	/**
	 * （数据库）当前时间，可用于Entity赋值。建议使用DateUtils操作
	 */
	public final Timestamp now()
	{
		return (Timestamp) getSession().createNativeQuery("SELECT " + AbstractDAOImpl.SQL_NOW()).uniqueResult();
	}

	/**
	 * （数据库）今天，可用于Entity赋值。建议使用DateUtils操作
	 */
	public final java.util.Date today()
	{
		return (java.util.Date) getSession().createNativeQuery("SELECT " + AbstractDAOImpl.SQL_TODAY()).uniqueResult();
	}
}