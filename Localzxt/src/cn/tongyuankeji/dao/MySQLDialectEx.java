package cn.tongyuankeji.dao;

import java.sql.Types;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * Hibernate与MySQL转义对应<br />
 * 配置在datadriver.xml中
 * 
 * @author 代平 2017-05-02 创建 TODO 0501 未完成清理，需要添加自定义的数据类型
 */

public class MySQLDialectEx extends MySQLDialect
{
	public MySQLDialectEx()
	{
		super();

		registerHibernateType(Types.TINYINT, StandardBasicTypes.BYTE.getName()); // hibernate v3 use org.hibernate.Hibernate.BYTE.getName()
				
		registerHibernateType(Types.VARCHAR, 65535, "text");
		registerHibernateType(Types.LONGVARCHAR, "string");
		registerHibernateType(Types.NULL, "string");
	}
}