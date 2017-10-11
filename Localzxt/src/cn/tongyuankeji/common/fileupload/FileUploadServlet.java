package cn.tongyuankeji.common.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cn.tongyuankeji.common.parameters.ConstantBase;
import cn.tongyuankeji.common.util.FileUtils;
import cn.tongyuankeji.common.util.JsonUtils;
import cn.tongyuankeji.common.util.Utils;

/**
 * 文件上传Servlet
 * 
 * 调用方式：<br />
 * FileUpload?filetype=...&isuser=...&recordId=... <br />
 * --- filetype: 文件类型 EnumUploadType <br />
 * --- isstudent: 1 - 用户调用, 0 - 其它子系统调用<br />
 * --- recordId: 主记录主键（sys_id, group_sign）。 filetype != 用户PID | 用户THUMB | PDFILE | ASSESSITEM 时必填<br />
 * 有主记录的，会触发postHandler.postUpload()；没有主记录的，上传到临时目录，业务逻辑需要自己去复制到目标路径
 *  * 
 * @author 代平 2017-05-02 创建
 */
@Controller
public class FileUploadServlet extends HttpServlet
{
	ServletConfig config;
	
	public void init(ServletConfig config) throws ServletException
	{
		this.config = config;
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		JSONObject o = new JSONObject();
		FileUploadSetting setting = null;
		JSONObject result = null;
		PrintWriter w = null;
		
		resp.setContentType("text/html; charset=utf-8");
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		
		try
		{
			setting = FileUploadManager.parseRequest(req);
			
			result = doUpload(req, setting);
			o.put(ConstantBase.JSON_KEY_OK, true);
			o.put(ConstantBase.JSON_KEY_DATA, result);
			w = resp.getWriter();
			w.write(o.toString());
		}
		catch (Exception ex)
		{
			o.put(ConstantBase.JSON_KEY_OK, false);
			o.put(ConstantBase.JSON_KEY_ERR, ex.getMessage());
			w = resp.getWriter();
			w.write(o.toString());
		}
		finally
		{
			if (w != null)
				w.close();
		}
	}
	
	private JSONObject doUpload(HttpServletRequest req, FileUploadSetting setting) throws Exception
	{
		JSONArray afile = new JSONArray();
		JSONObject result = new JSONObject(), ofile = null;
		
		if (!ServletFileUpload.isMultipartContent(req))
			throw new RuntimeException("FileUploadServlet只能用于上传文件");
		
		// 创建生产FileItem的DiskFileItemFactory工厂的实例
		DiskFileItemFactory facoty = new DiskFileItemFactory();
		
		// 设置缓冲区大小
		facoty.setSizeThreshold(FileUploadManager.Threshold_SIZE);
		
		// 设置文件存放临时路径
		facoty.setRepository(new File(System.getProperty("java.io.tmpdir")));
				
		ServletFileUpload upload = new ServletFileUpload(facoty);
		
		// 解决中文乱码
		upload.setHeaderEncoding("utf-8");
		
		// // 设置上传文件的大小,如果上传文件大于设置的大小，则要手动抛出SizeLimitExceededException 异常
		upload.setSizeMax(FileUploadManager.MAX_FILE_SIZE); // maximum size of request (include file + form data)
		
		upload.setFileSizeMax(setting.maxSize); // maximum size of upload file
		
		String savePath = null;
		if (setting.doPostHandle)
			savePath = FileUploadManager.getSavePathByType(setting.fileType, true);
		else
			savePath = FileUploadManager.getSaveTmpPath();
		
		String clientFile = null;
		String[] arrClientFile = null;
		File saveAsFile = null;
		
		// 解析request请求,返回集合[集合中存放FileItem]
		List<FileItem> formItems = upload.parseRequest(req);
		
		if (formItems == null || formItems.isEmpty())
			throw new RuntimeException("没有可上传的文件！");
		
		for (FileItem item : formItems)
		{
			if (!item.isFormField())
			{
				// 文件名
				clientFile = new File(item.getName()).getName();
				arrClientFile = Utils.splitFilename(clientFile);
				
				setting.clientFile = arrClientFile[0];
				setting.ext = arrClientFile[1];
				setting.sizeKB = FileUtils.Byte2KB(item.getSize()).intValue();
				
				if (setting.validExts == null
						|| setting.validExts.length == 0
						|| !Utils.isContainString(setting.ext, true, setting.validExts))
					throw new RuntimeException("不支持上传此类型文件！");
				
				setting.file = UUID.randomUUID().toString().replace("-", "");
				
				saveAsFile = new File(savePath + setting.file + "." + setting.ext);
				
				item.write(saveAsFile);
				
				ofile = new JSONObject();
				if (setting.doPostHandle)
					ofile.put("fid", FileUploadManager.getAbsoluteFilename(setting.fileType, setting.file));
				else
					ofile.put("fid", FileUploadManager.getTmpFilename(setting.file, setting.ext));
				ofile.put("fileName", Utils.concat('.', setting.file, setting.ext));
				ofile.put("size", item.getSize());
				ofile.put("displayName", item.getName());
				JsonUtils.put(ofile, "uploadedAt", new Timestamp((new java.util.Date()).getTime()));
				
				if (setting.doPostHandle)
				{
					try
					{
						setting.postHandler.postUpload(req, setting);
					}
					catch (Exception ex)
					{
						saveAsFile.delete();
						continue;
					}
				}
				
				afile.add(ofile);
			}
		}
		
		result.put(ConstantBase.JSON_KEY_OK, afile);
		
		return result;
	}
}
