package cn.tongyuankeji.common.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cn.tongyuankeji.common.util.Utils;
import cn.tongyuankeji.common.util.ResponseUtils;

/**
 * 文件下载Servlet
 * 调用方式：<br />
 * FileDownload?filetype=...&fid=...
 * --- filetype: 文件类型 EnumUploadType
 * --- fid: 服务器文件名
 * 
 * @author 代平  2017-05-02 创建
 */
@Controller
public class FileDownloadServlet extends HttpServlet
{
	ServletConfig config;

	public void init(ServletConfig config) throws ServletException
	{
		this.config = config;
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String fid = req.getParameter("fid"), mimeType = null;
		String displayName = req.getParameter("displayName");
		File serverFile = null;
		FileInputStream inStream = null;
		byte[] buffer = null;
		int bytesRead = -1;
		OutputStream outStream = null;

		try
		{
			serverFile = FileUploadManager.findDownloadFile(fid);

			if (serverFile == null)
				throw new RuntimeException("找不到下载的文件!");

			inStream = new FileInputStream(serverFile);

			mimeType = req.getServletContext().getMimeType(fid);
			if (Utils.isBlank(mimeType))
				mimeType = "application/octet-stream";

			// modifies response
			resp.setContentType(mimeType);
			resp.setContentLength((int) serverFile.length());

			// forces download       
			resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", displayName));

			// response's output stream       
			outStream = resp.getOutputStream();

			buffer = new byte[4096];
			bytesRead = -1;
			while ((bytesRead = inStream.read(buffer)) != -1)
				outStream.write(buffer, 0, bytesRead);
		}
		catch (Exception ex)
		{
			ResponseUtils.renderJson(resp, ex);
		}
		finally
		{
			if (inStream != null)
				inStream.close();

			if (outStream != null)
				outStream.close();
		}
	}
}
