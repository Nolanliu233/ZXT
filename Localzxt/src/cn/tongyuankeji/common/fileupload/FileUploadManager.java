package cn.tongyuankeji.common.fileupload;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import cn.tongyuankeji.common.parameters.Constant;
import cn.tongyuankeji.common.parameters.EnumUploadType;
import cn.tongyuankeji.common.web.RunSettingExImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.util.FileUtils;
import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.web.RunSettingImpl;

/**
 * 文件上传服务类
 * 
 * @author 代平 2017-05-02 创建 代平 2017-06-19 整理
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class FileUploadManager
{
	protected static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	protected static final int Threshold_SIZE = 1 * 1024 * 1024; // 上传文件流缓冲区大小
	
	protected static String[] VALID_FILE_EXT = null;
	protected static String[] VALID_IMG_EXT = null;
	
	protected static FileUploadSetting parseRequest(HttpServletRequest req) throws Exception
	{
		EnumUploadType fileType = EnumUploadType.valueOf(Byte.valueOf(req.getParameter("filetype")));
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		FileUploadSetting setting = new FileUploadSetting();
		setting.fileType = fileType;
		String tmp = null;
		
		if (Utils.isBlank(req.getParameter("isstudent")))
			throw new RuntimeException("missing argument 'isstudent'");
		
		boolean isUser = "0".equals(req.getParameter("isstudent"));
		
		if (FileUploadManager.VALID_FILE_EXT == null)
		{
			if (!Utils.isBlank(RunSettingImpl.getUPLOAD_FILE_EXT()))
				FileUploadManager.VALID_FILE_EXT = RunSettingImpl.getUPLOAD_FILE_EXT().split(",");
		}
		
		if (FileUploadManager.VALID_IMG_EXT == null)
		{
			if (!Utils.isBlank(RunSettingImpl.getUPLOAD_IMAGE_EXT()))
				FileUploadManager.VALID_IMG_EXT = RunSettingImpl.getUPLOAD_IMAGE_EXT().split(",");
		}
		
		// -------------------- 获取post handler --------------------
		setting.maxSize = FileUploadManager.getMaxUploadSize(fileType);
		
		switch (fileType)
		{
			case pid:// 用户 证照图片
			case thumb: // 用户头像图片
				
				// 用户不使用recordId，使用缓存中登录用户id， 用户要使用，用户可以更改其他用户的头像
				if (isUser)
				{
					setting.recordId = Integer.valueOf(req.getParameter("recordId"));
				}
				setting.validExts = FileUploadManager.VALID_IMG_EXT;
				setting.doPostHandle = true;
				setting.postHandler = (PostUploadHandler) context.getBean(isUser ? "SiteUserManager" : "StudentManager");
				
				return setting;
				
			case pdfile: // 用户资源
				if (!isUser)
				{
					setting.recordId = Integer.valueOf(req.getParameter("recordId")); // pdfile.parentId。为0时，没有父级
					setting.validExts = FileUploadManager.VALID_FILE_EXT;
					setting.postHandler = (PostUploadHandler) context.getBean("StudentManager");
					setting.doPostHandle = true;
					
					return setting;
				}
				break;
			
			default:
				throw new RuntimeException("not supported filetype in post handler");
		}
		
		return setting;
	}
	
	/**
	 * 根据文件类型，限制上传大小(in bytes)
	 */
	public static long getMaxUploadSize(EnumUploadType fileType)
	{
		switch (fileType)
		{
			case pid: // 用户证照图片
				return FileUtils.KB2Byte(RunSettingExImpl.getPHOTO_FILE_KB());
				
			case thumb: // 用户头像图片
				return FileUtils.KB2Byte(RunSettingExImpl.getTHUMB_FILE_KB());
				
			case pdfile: // 用户资源
				return FileUtils.KB2Byte(RunSettingExImpl.getPDFILE_KB());
				
				
			default:
				return 0;
		}
	}
	
	/**
	 * 计算文件保存物理路径（相对于theimages.xml的docBase节点）
	 */
	public static String getSavePathByType(EnumUploadType filetype, boolean shouldCreate) throws Exception
	{
		String folderPath = null;
		
		switch (filetype)
		{
			case pid:
				folderPath = ConstantBase.getUPLOAD_PATH() + RunSettingExImpl.getPID_PATH();
				break;
			case thumb:
				folderPath = ConstantBase.getUPLOAD_PATH() + RunSettingExImpl.getTHUMB_PATH();
				break;
			case pdfile:
				folderPath = ConstantBase.getUPLOAD_PATH() + RunSettingExImpl.getPDFILE_PATH();
				break;
			default:
				throw new Exception("file type not found");
		}
		
		File uploadDir = new File(folderPath);
		if (!uploadDir.exists())// 如果文件夹不存在，则创建一个
		{
			if (shouldCreate)
			{
				if (!uploadDir.mkdirs())
					throw new RuntimeException("无法创建上传目录：" + filetype.toString());
			}
			else
				return null;
		}
		
		return folderPath;
	}
	
	/**
	 * 没有主记录时上传，获得文件保存临时物理路径AppSettingBase.UPLOAD_TMP_PATH。配合FileUploadManager.getSavePathByType()使用
	 */
	public static String getSaveTmpPath() throws Exception
	{
		String folderPath = ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_TMP_PATH();
		File uploadDir = new File(folderPath);
		if (!uploadDir.exists())
		{
			if (!uploadDir.mkdirs())
				throw new RuntimeException("无法创建上传临时目录！");
		}
		
		return folderPath;
	}
	
	protected static File findDownloadFile(String fid)
	{
		String path = null, test = null, fl = null;
		File serverFile = null;
		
		int lastFileSep = fid.lastIndexOf(ConstantBase.FILE_SEP);
		if (lastFileSep < 0)
			lastFileSep = fid.lastIndexOf('\\');
		if (lastFileSep <= 0)
			return null;
		
		path = fid.substring(0, lastFileSep + 1).replace('\\', ConstantBase.FILE_SEP);
		
		for (EnumUploadType loop : EnumUploadType.values())
		{
			test = FileUploadManager.getPathByType(loop).replace('\\', ConstantBase.FILE_SEP);
			if (path.equalsIgnoreCase(test))
			{
				// 把 http://localhost:8080/theimages/Pdfiles/ 替换成 D:/MyEclipse
				// Professional/apache-tomcat-7.0.54/webapps/theimages/Pdfiles/
				path = path.replaceAll(RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH(), ConstantBase.getUPLOAD_PATH());
				fl = path + fid.substring(lastFileSep + 1);
				break;
			}
		}
		
		if (fl != null)
			serverFile = new File(fl);
		
		return serverFile != null && serverFile.exists() ? serverFile : null;
	}
	
	/**
	 * 按文件类型，返回存储的虚拟路径
	 */
	public static String getPathByType(EnumUploadType fileType)
	{
		switch (fileType)
		{
			case pid: // 用户证照图片
				return RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH() + RunSettingExImpl.getPID_PATH();
			case thumb: // 用户头像图片
				// "http://localhost:8080/theimages/Student/Thumbs/";
				return RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH() + RunSettingExImpl.getTHUMB_PATH();
			case pdfile: // 用户资源
				return RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH() + RunSettingExImpl.getPDFILE_PATH();
			default:
				break;
		}
		
		return RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH();
	}
	
	/**
	 * 在生成给客户端的图片文件时，调用此方法获得图片存放的URL路径和文件名
	 * 
	 * @param fileType
	 *            图片类型
	 * 
	 * @param fullname
	 *            文件名 和 扩展名
	 * 
	 * @author Jean
	 */
	public static String getAbsoluteFilename(EnumUploadType fileType, String fl)
	{
		switch (fileType)
		{
		/************ 有默认图的 ******************/
			case thumb: // 用户头像图片
				if (Utils.isBlank(fl))
					return RunSettingImpl.getSiteUrl() + RunSettingImpl.getCODE_ROOT() + Constant.DEFAULT_IMG_PATH + Constant.DEFAULT_THUMB_FILE;
				else
					return FileUploadManager.getPathByType(fileType) + Utils.concat('.', fl);
				
				/************ 无默认图的 ******************/
			default:
				if (!Utils.isBlank(fl))
					return FileUploadManager.getPathByType(fileType) + Utils.concat('.', fl);
				else
					return null;
		}
	}
	
	/**
	 * 获取临时目录中的文件名
	 */
	public static String getTmpFilename(String fl, String ext)
	{
		return RunSettingImpl.getSiteUrl() + ConstantBase.FILE_SEP + RunSettingImpl.getUPLOAD_VIRTUAL_PATH() + RunSettingImpl.getUPLOAD_TMP_PATH() + Utils.concat('.', fl, ext);
	}
	
	/**
	 * PostUploadHandler中，修改数据库图片记录字段时，调用此方法，删除原来的文件<br />
	 * 此方法不抛出异常。即使fl为空也可以安全调用。
	 */
	public static void deleteFile(EnumUploadType fileType, String fl, String ext)
	{
		File f = null;
		String path = null;
		
		if (Utils.isBlank(fl) || Utils.isBlank(ext))
			return;
		
		try
		{
			path = getSavePathByType(fileType, false);
			if (path == null)
				return;
			
			path += Utils.concat('.', fl, ext);
			f = new File(path);
			if (f.exists())
				f.delete();
		}
		catch (Exception ex)
		{
		}
	}
	
	/**
	 * 将文件从临时目录移动到目标目录
	 * 
	 * @param filename
	 *            从临时目录找哪个文件名
	 * @param filetype
	 *            移动到哪里
	 */
	public static boolean moveFromTmpPath(String filename, EnumUploadType filetype) throws Exception
	{
		String srcFolder = ConstantBase.getUPLOAD_PATH() + RunSettingImpl.getUPLOAD_TMP_PATH();
		String targetFolder = getSavePathByType(filetype, true);
		
		File fl = new File(srcFolder + filename);
		if (!fl.exists())
			throw new RuntimeException("源文件不存在！");
		
		return fl.renameTo(new File(targetFolder + filename));
	}
}
