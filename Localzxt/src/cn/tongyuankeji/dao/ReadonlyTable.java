package cn.tongyuankeji.dao;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import cn.tongyuankeji.common.util.Utils;

/**
 * 由java.sql.ResultSet创建一个内存（只读）表。<br />
 * 取值时，使用column name比column index效率更好
 * 
 * @author 代平 2017-05-08 创建
 */
public class ReadonlyTable
{
	private String tableName;
	
	// <column_name, column_index>
	private HashMap<String, Integer> columnMap;
	
	// value in java.sql.Types
	private Class<?>[] columnTypes;
	
	// 第一维 .length可能 =0
	// 第二维，应使用columnMap.value为座标
	private Object[][] rows;
	
	/**
	 * Initialize a ReadonlyTable from ResultSet. Before calling this ctor, cursor should be before the first row
	 * 
	 * @param rs
	 *            ResultSet from CallableStatement.getResultSet()
	 * 
	 * @param tableIndex
	 *            第几次循环调用CallableStatement.getResultSet()。下标应从0开始。
	 */
	public ReadonlyTable(ResultSet rs, int tableIndex) throws SQLException
	{
		assert rs != null : "DataTable ctor(rs)不能为空";
		assert tableIndex >= 0 : "ReadonlyTable(ResultSet, int, String) tableIndex should be greater than or equal to 0!";
		
		ArrayList<Object[]> tbl = new ArrayList<Object[]>();
		Object[] data = null;
		int colCnt = 0, rowIdx = 0;
		
		// set table name
		this.tableName = String.format("table%d", tableIndex);
		
		// set column name to index map
		ResultSetMetaData rsmeta = rs.getMetaData();
		colCnt = rsmeta.getColumnCount();
		
		this.columnMap = new HashMap<String, Integer>(colCnt);
		this.columnTypes = new Class<?>[colCnt];
		
		for (int i = 1; i <= colCnt; i++)
		{
			// 奇怪的是，java result set index starts with 1
			this.columnMap.put(rsmeta.getColumnLabel(i), i - 1);
			this.columnTypes[i - 1] = ReadonlyTable.jdbcJavaTypes.get(rsmeta.getColumnType(i));
		}
		
		// set data row-by-row
		while (rs.next())
		{
			data = new Object[colCnt];
			
			for (int i = 1; i <= colCnt; i++)
			{
				if (rs.getObject(i) != null)
				{
					if (this.columnTypes[i - 1] != rs.getObject(i).getClass())
					{
						if (this.columnTypes[i - 1] == Integer.class
								|| this.columnTypes[i - 1] == Long.class)// JDBC对Unsigned int，认为是Long或BigInteger
						{
							data[i - 1] = Integer.valueOf(rs.getObject(i).toString());
							continue;
						}
						else if (this.columnTypes[i - 1] == Short.class) // JDBC对Unsigned short，认为是Integer
						{
							data[i - 1] = Short.valueOf(rs.getObject(i).toString());
							continue;
						}
						else if (this.columnTypes[i - 1] == Byte.class)
						{
							data[i - 1] = Byte.valueOf(rs.getObject(i).toString());
							continue;
						}
						else if (this.columnTypes[i - 1] == Double.class
								|| this.columnTypes[i - 1] == java.math.BigDecimal.class)
						{
							data[i - 1] = Double.valueOf(rs.getObject(i).toString());
							continue;
						}						
						else
						{
							assert false : "sp 没有列出的转义 - " + this.columnTypes[i - 1].getName();
						}
					}
					
					// 其余的，可以直接赋值
					data[i - 1] = rs.getObject(i);
				}
			}
			
			tbl.add(data);
		}
		
		// convert data to Object array for faster access
		this.rows = new Object[tbl.size()][colCnt];
		for (Iterator<Object[]> itr = tbl.iterator(); itr.hasNext();)
		{
			this.rows[rowIdx] = itr.next();
			rowIdx++;
		}
	}
	
	/**
	 * 获得第rowIdx行（下标从0开始），列名称为colName的值
	 * 
	 * @param rowIdx
	 *            行下标
	 * @param colName
	 *            列名称
	 */
	public <T> T get(int rowIdx, String colName)
	{
		assert rowIdx >= 0 && rowIdx < this.rows.length : String.format("ReadonlyTable.get(int, String) row index out of bound, valid from 0 ~ %d!", this.rows.length - 1);
		assert columnMap.containsKey(colName) : "ReadonlyTable.get(int, String) does not contain column named " + colName;
		
		if (this.rows.length > 0)
			return (T) this.rows[rowIdx][columnMap.get(colName)];
		
		return null;
	}
	
	/**
	 * 返回colName列的Java类型
	 * 
	 * @param colName
	 *            列名称。Select SysId，返回SysId；Select SysId CourseId返回CourseId
	 */
	public Class<?> getColumnType(String colName)
	{
		assert this.columnMap.containsKey(colName) : String.format("getColumnType(String) does not contain column '%s'!", colName);
		
		return this.columnTypes[this.columnMap.get(colName)];
	}
	
	/**
	 * 共有多少列
	 */
	public int getColumnCount()
	{
		return this.columnMap != null ? this.columnMap.size() : 0;
	}
	
	/**
	 * 共有多少条记录
	 */
	public int getRowCount()
	{
		return this.rows != null ? this.rows.length : 0;
	}
	
	/**
	 * 返回所有列名称，按SELECT的的返回顺序
	 */
	public String[] getColumnNames()
	{
		if (this.columnMap != null)
		{
			String[] result = new String[this.columnMap.size()];
			int idx = 0;
			for (Iterator<String> itr = this.columnMap.keySet().iterator(); itr.hasNext();)
			{
				result[idx] = itr.next();
				idx++;
			}
			
			return result;
		}
		return null;
	}
	
	/**
	 * 将colName中所有值转成array，遇到null或空字符串时跳过
	 * 
	 * @param valColName
	 *            取值列名
	 * 
	 * @param type
	 *            取值列java类型，应与this.getColumnType(valColName)返回一致
	 */
	public <T> T[] select(String valColName)
	{
		assert !Utils.isBlank(valColName) : "select(String)参数valColName不能为空！";
		assert columnMap.containsKey(valColName) : String.format("select(String)没有 %s 列", valColName);
		
		boolean isString = this.getColumnType(valColName) == String.class;
		ArrayList<T> lst = new ArrayList<T>();
		T val = null;
		
		for (int i = 0; i < this.rows.length; i++)
		{
			val = (T) this.rows[i][this.columnMap.get(valColName)];
			
			if (val == null || (isString && Utils.isBlank((String) val)))
				continue;
			
			lst.add(val);
		}
		
		return Utils.list2Array(lst, null);
	}
	
	/**
	 * 将colName中与compareTo值相等的值转成array，遇到null或空字符串时跳过
	 * 
	 * @param valColName
	 *            取值列名
	 * 
	 * @param compareColName
	 *            对比列名
	 * 
	 * @param compareTo
	 *            对比值，至少要提供一个，且不能使用空字符串
	 * 
	 * @return T[]。如果没有匹配时，返回null
	 */
	public <T> T[] select(String valColName, String compareColName, Object... compareTo)
	{
		assert !Utils.isBlank(valColName) : "select(String, String, Object...)参数valColName不能为空！";
		assert columnMap.containsKey(valColName) : String.format("select(String, String, Object...)没有 %s 列", valColName);
		assert !Utils.isBlank(compareColName) : "select(String, String, Object...)参数compareColName不能为空！";
		assert compareTo != null && compareTo.length > 0 : "select(String, String, Object...)参数compareTo不能为空，至少应包含一个对比值！";
		
		boolean isValString = this.getColumnType(valColName) == String.class;
		boolean isCompareString = this.getColumnType(compareColName) == String.class;
		ArrayList<T> lst = new ArrayList<T>();
		T val = null;
		Object compare = null;
		
		for (int i = 0; i < this.rows.length; i++)
		{
			compare = this.rows[i][this.columnMap.get(compareColName)];
			val = (T) this.rows[i][this.columnMap.get(valColName)];
			
			if (compare == null || (isCompareString && Utils.isBlank((String) compare)))
				continue;
			
			if (val == null || (isValString && Utils.isBlank((String) val)))
				continue;
			
			if (Utils.isContain(compare, compareTo))
				lst.add(val);
		}
		
		return Utils.list2Array(lst, null);
	}
	
	/**
	 * 在colName上按compare筛选，返回符合条件的行index。
	 * 
	 * @param colName
	 *            列名称
	 * @param value
	 *            匹配条件，大小写敏感
	 */
	public Integer[] selectRowIndex(String colName, Object compare)
	{
		assert !Utils.isBlank(colName) : "selectRowIndex(String, Object)参数colName不能为空！";
		assert columnMap.containsKey(colName) : String.format("selectRowIndex(String, Object)没有 %s 列", colName);
		
		ArrayList<Integer> lst = new ArrayList<Integer>(this.rows.length);
		
		for (int i = 0; i < rows.length; i++)
		{
			if (this.rows[i][columnMap.get(colName)].equals(compare))
				lst.add(i);
		}
		
		return Utils.list2Array(lst, null);
	}
	
	/**
	 * 找到表中将colName中与compareTo值相等的值的行数，遇到null或空字符串时跳过
	 * 
	 * @param valColName
	 *            取值列名
	 * 
	 * @param compareColName
	 *            对比列名
	 * 
	 * @param compareTo
	 *            对比值，至少要提供一个，且不能使用空字符串
	 */
	public int selectCount(String valColName, String compareColName, Object... compareTo)
	{
		assert !Utils.isBlank(valColName) : "selectCount(String, String, Object...)参数valColName不能为空！";
		assert columnMap.containsKey(valColName) : String.format("selectCount(String, String, Object...)没有 %s 列", valColName);
		assert !Utils.isBlank(compareColName) : "selectCount(String, String, Object...)参数compareColName不能为空！";
		assert compareTo != null && compareTo.length > 0 : "selectCount(String, String, Object...)参数compareTo不能为空，至少应包含一个对比值！";
		
		boolean isValString = this.getColumnType(valColName) == String.class;
		boolean isCompareString = this.getColumnType(compareColName) == String.class;
		int cnt = 0;
		Object val = null, compare = null;
		
		for (int i = 0; i < this.rows.length; i++)
		{
			compare = this.rows[i][this.columnMap.get(compareColName)];
			val = this.rows[i][this.columnMap.get(valColName)];
			
			if (compare == null || (isCompareString && Utils.isBlank((String) compare)))
				continue;
			
			if (val == null || (isValString && Utils.isBlank((String) val)))
				continue;
			
			if (Utils.isContain(compare, compareTo))
				cnt++;
		}
		
		return cnt;
	}
	
	public String selectJoinedText(char delimiter, String... colNames)
	{
		assert colNames != null && colNames.length > 0 : "";
		
		StringBuffer sb = new StringBuffer();
		Object val = null;
		boolean[] arrIsValString = new boolean[colNames.length];
		for (int j = 0; j < colNames.length; j++)
		{
			arrIsValString[j] = this.getColumnType(colNames[j]) == String.class;
		}
		
		for (int i = 0; i < this.rows.length; i++)
		{
			for (int j = 0; j < colNames.length; j++)
			{
				val = this.rows[i][this.columnMap.get(colNames[j])];
				
				if (sb.length() > 0)
					sb.append(delimiter);
				
				if (val == null || (arrIsValString[j] && Utils.isBlank((String) val)))
					sb.append("");
				
				else
					sb.append(val.toString());
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 内存表名称
	 */
	public String getTableName()
	{
		return this.tableName;
	}
	
	public void DebugTest()
	{
		System.out.println("-------------");
		System.out.println(String.format("Columns: %d, rows: %d", this.getColumnCount(), this.getRowCount()));
		
		String[] colNames = this.getColumnNames();
		for (String k : colNames)
			System.out.print(k + " is " + this.getColumnType(k).getName() + ", ");
		System.out.println("");
		
		for (int i = 0; i < this.getRowCount(); i++)
		{
			for (String k : colNames)
				System.out.print(k + "=" + this.get(i, k).toString() + ", ");
			
			System.out.println("");
		}
		
		// for (int i = 0; i < this.getRowCount(); i++)
		// {
		// for (int j = 0; j < this.getColumnCount(); j++)
		// System.out.print(colNames[j] + "=" + this.get(i, j).toString() + ", ");
		//
		// System.out.println("");
		// }
	}
	
	// JDBC到Java的类型定义
	private static TreeMap<Integer, Class<?>> jdbcJavaTypes = null;
	
	public static Class<?> ToType(int jdbcType)
	{
		return jdbcJavaTypes.get(jdbcType);
	}
	
	static
	{
		jdbcJavaTypes = new TreeMap<Integer, Class<?>>();
		
		jdbcJavaTypes.put(new Integer(Types.LONGNVARCHAR), String.class); // -16 字符串
		jdbcJavaTypes.put(new Integer(Types.NCHAR), String.class); // -15 字符串
		jdbcJavaTypes.put(new Integer(Types.NVARCHAR), String.class); // -9 字符串
		jdbcJavaTypes.put(new Integer(Types.ROWID), String.class); // -8 字符串
		jdbcJavaTypes.put(new Integer(Types.BIT), Boolean.class); // -7 布尔
		jdbcJavaTypes.put(new Integer(Types.TINYINT), Byte.class); // -6 数字
		jdbcJavaTypes.put(new Integer(Types.BIGINT), Long.class); // -5 数字
		jdbcJavaTypes.put(new Integer(Types.LONGVARBINARY), java.sql.Blob.class); // -4 二进制
		jdbcJavaTypes.put(new Integer(Types.VARBINARY), java.sql.Blob.class); // -3 二进制
		jdbcJavaTypes.put(new Integer(Types.BINARY), java.sql.Blob.class); // -2 二进制
		jdbcJavaTypes.put(new Integer(Types.LONGVARCHAR), String.class); // -1 字符串
		// jdbcJavaTypes.put(new Integer(Types.NULL), String.class); // 0 /
		jdbcJavaTypes.put(new Integer(Types.CHAR), String.class); // 1 字符串
		jdbcJavaTypes.put(new Integer(Types.NUMERIC), java.math.BigDecimal.class); // 2 数字
		jdbcJavaTypes.put(new Integer(Types.DECIMAL), java.math.BigDecimal.class); // 3 数字
		jdbcJavaTypes.put(new Integer(Types.INTEGER), Integer.class); // 4 数字
		jdbcJavaTypes.put(new Integer(Types.SMALLINT), Short.class); // 5 数字
		jdbcJavaTypes.put(new Integer(Types.FLOAT), java.math.BigDecimal.class); // 6 数字
		jdbcJavaTypes.put(new Integer(Types.REAL), java.math.BigDecimal.class); // 7 数字
		jdbcJavaTypes.put(new Integer(Types.DOUBLE), java.math.BigDecimal.class); // 8 数字
		jdbcJavaTypes.put(new Integer(Types.VARCHAR), String.class); // 12 字符串
		jdbcJavaTypes.put(new Integer(Types.BOOLEAN), Boolean.class); // 16 布尔
		// jdbcJavaTypes.put(new Integer(Types.DATALINK), String.class); // 70 /
		jdbcJavaTypes.put(new Integer(Types.DATE), java.util.Date.class); // 91 日期
		jdbcJavaTypes.put(new Integer(Types.DATE), java.sql.Date.class);
		jdbcJavaTypes.put(new Integer(Types.TIME), java.sql.Time.class); // 92 日期
		jdbcJavaTypes.put(new Integer(Types.TIMESTAMP), java.sql.Timestamp.class); // 93 日期
		jdbcJavaTypes.put(new Integer(Types.OTHER), Object.class); // 1111 其他类型？
		// jdbcJavaTypes.put(new Integer(Types.JAVA_OBJECT), Object.class); // 2000
		// jdbcJavaTypes.put(new Integer(Types.DISTINCT), String.class); // 2001
		// jdbcJavaTypes.put(new Integer(Types.STRUCT), String.class); // 2002
		// jdbcJavaTypes.put(new Integer(Types.ARRAY), String.class); // 2003
		jdbcJavaTypes.put(new Integer(Types.BLOB), java.sql.Blob.class); // 2004 二进制
		jdbcJavaTypes.put(new Integer(Types.CLOB), java.sql.Clob.class); // 2005 大文本
		// jdbcJavaTypes.put(new Integer(Types.REF), String.class); // 2006
		// jdbcJavaTypes.put(new Integer(Types.SQLXML), String.class); // 2009
		jdbcJavaTypes.put(new Integer(Types.NCLOB), java.sql.Clob.class); // 2011 大文本
	}
}