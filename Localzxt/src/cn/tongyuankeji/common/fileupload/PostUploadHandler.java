package cn.tongyuankeji.common.fileupload;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传完成后需要进行的额外处理
 * 
 * @author 代平 2017-05-02 创建
 */

public interface PostUploadHandler
{
	public abstract void postUpload(HttpServletRequest req, FileUploadSetting setting) throws Exception;
}
