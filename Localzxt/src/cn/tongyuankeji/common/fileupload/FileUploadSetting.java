package cn.tongyuankeji.common.fileupload;

import cn.tongyuankeji.common.parameters.EnumUploadType;

/**
 * 用于文件上传FileUploadManager解析request
 * 
 * @author 代平 2017-05-02 创建
 */
public class FileUploadSetting
{
	public EnumUploadType fileType;
	/**
	 * 主记录主键（sys_id, group_sign）。 filetype != 用户PID | 用户THUMB | HOMEIMAGE | PDFILE | ASSESSITEM 时必填<br />
	 * 有主记录的，会触发postHandler.postUpload()；没有主记录的，上传到临时目录，业务逻辑需要自己去复制到目标路径
	 */
	public Object recordId;
	
	public String file;
	public String ext;
	
	public String clientFile;
	public Integer sizeKB; // 文件大小KB
	
	protected long maxSize;
	protected String[] validExts;
	protected PostUploadHandler postHandler;
	protected boolean doPostHandle; // 是否调用postHandler.postUpload()
}
