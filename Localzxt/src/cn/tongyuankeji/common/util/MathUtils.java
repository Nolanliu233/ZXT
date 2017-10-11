package cn.tongyuankeji.common.util;

public class MathUtils
{
	public static Double divide(int v1, int v2, int scale)
	{
		Double mult;
		switch (scale)
		{
			case 0:
				mult = 1D;
				break;
			case 1:
				mult = 10D;
				break;
			default:
				mult = 100D;
				break;
		}

		if (v2 == 0)
			return null;

		return Math.round((v1 * mult) / v2) / mult;
	}
	
	public static Double divide(Double v1, int scale)
	{
		if(v1 == null){
			return null;
		} else {
			Double mult;
			switch (scale)
			{
				case 0:
					mult = 1D;
					break;
				case 1:
					mult = 10D;
					break;
				default:
					mult = 100D;
					break;
			}

			return Math.round(v1 * mult) / mult;
		}
	}
	
	/********************* 对象比较 *********************/
	public static boolean isEqual(Number n1, Number n2)
	{
		if (n1 == null && n2 == null)
			return true;

		if ((n1 == null && n2 != null)
				|| (n1 != null && n2 == null))
			return false;

		if (n1 != null)
		{
			return n1.equals(n2);
		}
		else
		{
			return (n2.equals(n1));
		}
	}
}
