package cn.tongyuankeji.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.util.FileUtils;
import cn.tongyuankeji.common.util.Utils;

public class FileUtils
{
	public static Double Byte2KB(Long b)
	{
		return b.doubleValue() / 1024D;
	}
	
	public static Double KB2MB(Number kb)
	{
		if (kb != null)
			return kb.doubleValue() / 1024D;
		return null;
	}
	
	public static Double KB2GB(Number kb)
	{
		if (kb != null)
			return kb.doubleValue() / 1024D / 1024D;
		return null;
	}
	
	// public static Double MB2KB(Number mb)
	// {
	// if (mb != null)
	// return mb.intValue() * 1024;
	// return null;
	// }
	
	// public static Double MB2GB(Number mb)
	// {
	// if (mb != null)
	// return mb.intValue() / 1024;
	// return null;
	// }
	
	public static Long KB2Byte(Integer kb)
	{
		if (kb != null)
			return kb.longValue() * 1024;
		return null;
	}
	
	public static String fileSizeDisplay(Integer kb)
	{
		if (kb < 1024)
			return String.format("%dKB", kb);
		else if (kb < 1024 * 1024)
			return String.format("%.2fMB", FileUtils.KB2MB(kb));
		else
			return String.format("%.2fGB", FileUtils.KB2GB(kb));
	}
	
	public static String fileSizeDisplay2(Integer bytes)
	{
		return FileUtils.fileSizeDisplay(bytes / 1024);
	}
	
	public static boolean fileExists(String file)
	{
		return new File(file).exists();
	}
	
	/**
	 * 删除文件。不抛出异常
	 * 
	 * @param filename
	 *            文件名，包含完整路径
	 */
	public static boolean deleteFile(String filename)
	{
		File fl = null;
		
		try
		{
			fl = new File(filename);
			if (fl.exists())
				return fl.delete();
		}
		catch (Exception ex)
		{
		}
		
		return false;
	}
	
	/**
	 * 删除目录，含下面的文件和子目录。不抛出异常
	 * 
	 * @param filename
	 *            文件名，包含完整路径
	 */
	public static boolean deleteFolder(String path)
	{
		File folder = null, file = null;
		String[] files = null;
		
		path = FileUtils.formalizePath(path);
		
		try
		{
			folder = new File(path);
			if (!folder.exists())
				return true;
			
			files = folder.list();
			for (String fl : files)
			{
				file = new File(path + fl);
				
				if (file.isDirectory())
					deleteFolder(path + fl);
				else
					file.delete();
			}
			
			return folder.delete();
		}
		catch (Exception ex)
		{
		}
		
		return false;
	}
	
	/**
	 * 创建子目录（允许多级同时建）。不抛出异常
	 * 
	 * @return 被创建的完整目录
	 */
	public static String createFolder(String... arrPath)
	{
		assert (arrPath != null && arrPath.length > 0);
		
		StringBuffer sb = new StringBuffer();
		for (String p : arrPath)
		{
			p = p.replace("\\", "/");
			sb.append(p);
			if (!p.endsWith("/"))
				sb.append("/");
		}
		
		File folder = null;
		
		try
		{
			folder = new File(sb.toString());
			if (folder.exists() || folder.mkdirs())
				return sb.toString();
		}
		catch (Exception ex)
		{
		}
		
		return null;
	}
	
	public static String readTextFile(String filename) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = new FileInputStream(filename);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append("\r\n");
		}
		isr.close();
		fis.close();
		
		return sb.toString();
	}
	
	/**
	 * 复制整个文件夹中的文件到目标目录，支持复制子目录。<br />
	 * 如果目标文件夹已存在，先清空里面的内容<br />
	 * 无论源文件夹是否有内容需要复制，都会创建目标文件夹<br />
	 * 
	 * @param oldPath
	 *            源文件夹路径 如：c:/fqf
	 * @param newPath
	 *            复制后路径 如：f:/fqf/ff
	 * @param copyTheseFiles
	 *            复制全部文件时，传入null；否则，只拷贝copyTheseFiles中的文件
	 * @return 有多少文件被复制了
	 */
	public static int copyFolder(String sourcePath, String targetPath, String[] copyTheseFiles) throws FileNotFoundException, IOException
	{
		String[] allFiles = null;
		sourcePath = FileUtils.formalizePath(sourcePath);
		targetPath = FileUtils.formalizePath(targetPath);
		
		{// 先删除目录（即，清空文件）
			FileUtils.deleteFolder(targetPath);
			File folder = new File(targetPath);
			if (!folder.exists())
			{
				if (FileUtils.createFolder(targetPath) == null)
					throw new RuntimeException("创建输出文件夹失败！" + targetPath);
			}
		}
		
		{ // 取得所有要复制的文件
			File folder = new File(sourcePath);
			
			// 源文件夹不存在
			if (!folder.exists())
				throw new RuntimeException("源文件夹不存在！");
			
			// 源文件夹中没有文件
			allFiles = folder.list();
			if (allFiles.length == 0)
				return 0;
		}
		
		int copyCnt = 0;
		File fileSource = null;
		byte[] buffer = new byte[1024 * 5];
		
		for (int i = 0; i < allFiles.length; i++)
		{
			if (copyTheseFiles != null && copyTheseFiles.length > 0)
			{
				if (!Utils.isContainString(allFiles[i], true, copyTheseFiles))
					continue;
			}
			
			fileSource = new File(sourcePath + allFiles[i]);
			if (fileSource.isFile())
			{
				FileUtils.copyFileImpl(fileSource, targetPath, buffer);
			}
			else if (fileSource.isDirectory())
			{
				FileUtils.copyFolder(sourcePath + allFiles[i], targetPath + allFiles[i], copyTheseFiles);
			}
			
			copyCnt++;
		}
		
		return copyCnt;
	}
	
	public static boolean copyFile(String sourceFile, String targetPath, boolean override) throws FileNotFoundException, IOException
	{
		File fileSource = new File(sourceFile);
		if (!fileSource.exists())
			throw new FileNotFoundException("源文件不存在：%s！");
		
		if (!fileSource.isFile())
			return false;
		
		targetPath = FileUtils.formalizePath(targetPath);
		
		String targetFile = targetPath + fileSource.getName();
		if (FileUtils.fileExists(targetFile))
		{
			if (!override)
				return true;
			else
				FileUtils.deleteFile(targetFile);
		}
		
		byte[] buffer = new byte[1024 * 5];
		FileUtils.copyFileImpl(fileSource, targetPath, buffer);
		return true;
	}
	
	/**
	 * 复制文件逻辑实现。失败时，目标文件被删除
	 */
	private static void copyFileImpl(File fileSource, String targetPath, byte[] buffer) throws FileNotFoundException, IOException
	{
		FileInputStream input = null;
		FileOutputStream output = null;
		File fileTarget = null;
		int len = 0;
		
		try
		{
			input = new FileInputStream(fileSource);
		}
		catch (FileNotFoundException fex)
		{
			return;
		}
		
		try
		{
			fileTarget = new File(targetPath + fileSource.getName());
			output = new FileOutputStream(fileTarget);
		}
		catch (FileNotFoundException fex)
		{
			try
			{
				input.close();
			}
			catch (IOException notused)
			{
			}
			
			throw fex;
		}
		
		try
		{
			len = 0;
			while ((len = input.read(buffer)) != -1)
				output.write(buffer, 0, len);
			
			output.flush();
			
			output.close();
			input.close();
		}
		catch (IOException iex)
		{
			try
			{
				output.close();
				input.close();
				
				fileTarget.delete();
			}
			catch (IOException notused)
			{
			}
			
			throw iex;
		}
	}
	
	public static String formalizePath(String path)
	{
		if (path.indexOf("\\") >= 0)
			path = path.replace("\\", "/");
		if (!path.endsWith("/"))
			return path + ConstantBase.FILE_SEP;
		return path;
	}

	public static List<File> getFiles(File folder)
	{
		List<File> files = new ArrayList<File>();
		iterateFolder(folder, files);
		return files;
	}

	private static void iterateFolder(File folder, List<File> files)
	{
		File flist[] = folder.listFiles();
		files.add(folder);
		if (flist == null || flist.length == 0)
		{
			files.add(folder);
		} else
		{
			for (File f : flist)
			{
				if (f.isDirectory())
				{
					iterateFolder(f, files);
				} else
				{
					files.add(f);
				}
			}
		}
	}

	/**
	 * 计算文件大小，不足1MB，显示为XXXKB；否则，显示为XXXMB
	 * @param fileSizeBytes 文件大小in bytes
	 * @author 代平
	 */
	public static String getFileSizeMB(Integer fileSizeBytes)
	{
		Double kb, mb;

		if (fileSizeBytes != null)
		{
			kb = MathUtils.divide(fileSizeBytes, 1024, 2);
			if (kb <= 1) // 不足1KB
				return kb + "KB";

			mb = MathUtils.divide(kb.intValue(), 1024, 2);
			if (mb <= 1) // 不足1MB
				return kb + "KB";
			
			return mb + "MB";
		}

		return "";
	}
	
	/**
	 * 写入内容到指定文件
	 * @param
	 * 	path:文件全路径+文件名
	 * 	content: 写入文件内容
	 * */
	public static void write(String path, String content) throws IOException {
		// 进行file的初始化...
		//TODO 文件夹不存在，自动创建文件夹
		File outputFile = new File(path);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(outputFile, false),"UTF-8");      
        BufferedWriter writer=new BufferedWriter(write);          
        writer.write(content); 
        writer.close();
	}
	
	/**
     * 功能：Java读取文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath 文件全路径+文件名
     */
    public static String readFile(String filePath) throws Exception {
    	
        StringBuffer result = new StringBuffer();
        File file=new File(filePath);
        if(!file.isFile() || !file.exists()){ //判断文件是否存在
        	throw new RuntimeException("找不到指定的文件！");
        }
        InputStreamReader read = new InputStreamReader(
        new FileInputStream(file),"UTF-8");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null){
        	result.append(lineTxt);
        }
        read.close();
        return result.toString();
    }
}
