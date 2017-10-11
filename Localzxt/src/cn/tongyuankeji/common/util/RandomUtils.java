package cn.tongyuankeji.common.util;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.web.RunSettingImpl;

/**
 * 随机数工具类
 * 
 * @author 代平 2017-05-02 创建
 */
public class RandomUtils
{
	/**
	 * 获得原始数组data的索引int[]
	 */
	public static <T> int[] getIndexes(T[] data)
	{
		assert data != null : "getIndexes(T[])参数data不能为空！";
		
		int[] result = new int[data.length];
		for (int i = 0; i < result.length; i++)
			result[i] = i;
		return result;
	}
	
	/**
	 * 将原始数组data的索引随机打乱。返回值应作为原始数组的索引。<br />
	 * 如，原始数组int[] aaa = { 1, 5, 101, 23 }<br />
	 * 原始索引indexes[] = { 0, 1, 2, 3 }。<br />
	 * 打乱后索引result = { 3, 2, 0, 1 }。<br />
	 * 返回后，在原始数组上取值 aaa[result[X]], x从0~3
	 * 
	 * @param indexes
	 *            原始数组的索引
	 * @return 打乱后的索引
	 */
	public static <T> int[] makeIndex(T[] data)
	{
		assert data != null : "makeIndex(T[])参数data不能为空！";
		
		int[] indexes = RandomUtils.getIndexes(data);
		
		return breakOrder(indexes);
	}
	
	public static <T> int[] getIndexes(List lst)
	{
		assert lst != null : "getIndexes(List)参数lst不能为空！";
		
		int[] result = new int[lst.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = i;
		return result;
	}
	
	public static int[] makeIndex(List lst)
	{
		assert lst != null : "makeIndex(List)参数lst不能为空！";
		
		int[] indexes = RandomUtils.getIndexes(lst);
		return breakOrder(indexes);
	}
	
	/**
	 * 生成一组随机数字(0-9之间)的字符串，长度有length控制
	 */
	public static String getRandomNum(int length)
	{
		assert length > 0 : "getRandomNum(int)参数length必修大于0！";
		
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		
		for (int i = 0; i < length; i++)
		{
			sb.append(String.valueOf(random.nextInt(10)));
		}
		
		return sb.toString();
	}
	
	/*--------------------- 验证码图片生成 -------------------------------*/
	/**
	 * 产生动态验证码 图片，或手机验证码
	 * 
	 * @param req
	 * 
	 * @param length
	 *            需要多长的文字
	 * 
	 * @param g
	 *            Graphics对象(参见ValiCodeMaker)。不需要产生图片时，传入null
	 */
	public static String setValiCode(HttpServletRequest req, int length, Graphics g)
	{
		assert req != null : "setValiCode(HttpServletRequest, int, Graphics)参数req不能为空！";
		
		Random rand = new Random();
		int start = 0, size = (g == null) ? RandomUtils.RANDOM_NUM_BASE.length() : RandomUtils.RANDOM_CHAR_BASE.length();
		String tmpStr = null;
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < length; i++)
		{
			start = rand.nextInt(size);
			
			if (g != null)
			{
				tmpStr = RandomUtils.RANDOM_CHAR_BASE.substring(start, start + 1);
				
				// Generate a random color (foreground, so darker)
				g.setColor(RandomUtils.getRandColor(10, 150));
				
				// This calligraphy picture
				g.drawString(tmpStr, 13 * i + 6 + rand.nextInt(5), 14 + rand.nextInt(6));
			}
			else
				tmpStr = RandomUtils.RANDOM_NUM_BASE.substring(start, start + 1);
			
			sb.append(tmpStr);
		}
		
		// Authentication code stored in the session
		req.getSession().setAttribute(ConstantBase.VALI_CODE, sb.toString());
		Long exp = System.currentTimeMillis() + (new Long(RunSettingImpl.getVERIFICATION_CODE_SECOND()) * 1000L);
		req.getSession().setAttribute(ConstantBase.VALI_CODE_EXPIRE, exp);
		
		return sb.toString();
	}
	
	/**
	 * 产生随机颜色
	 */
	public static Color getRandColor(int fc, int bc)
	{
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
	
	/*--------------------- private helper -------------------------------*/
	private static int[] breakOrder(int[] indexes)
	{
		Random r = new Random();
		int n = indexes.length, k, tmp;
		while (n > 1)
		{
			n--;
			k = r.nextInt(n);
			tmp = indexes[k];
			indexes[k] = indexes[n];
			indexes[n] = tmp;
		}
		
		return indexes;
	}
	
	/**
	 * 在min~max之间（含）取一个随机数
	 * 
	 * @param min
	 *            最小值
	 * 
	 * @param max
	 *            最大值
	 */
	public static int makeInRange(int min, int max)
	{
		return new Random().nextInt(max + 1 - min) + min;
	}
	
	/**
	 * 生产长度为length的随机字母数字混合字符串
	 * 
	 * @param length
	 *            指定字符串长度
	 */
	public static String getCharacterAndNumber(int length)
	{
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++)
		{
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum))
			{
				// 取得大写字母还是小写字母
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			}
			// 数字
			else if ("num".equalsIgnoreCase(charOrNum))
			{
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}
	
	/**
	 * 获取一个UUID，不含 "-"
	 */
	public static String get32UUID()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	private final static String RANDOM_CHAR_BASE = "abcdefghijklmnopqrstuvwxyz0123456789";
	
	private final static String RANDOM_NUM_BASE = "0123456789";
	
}
