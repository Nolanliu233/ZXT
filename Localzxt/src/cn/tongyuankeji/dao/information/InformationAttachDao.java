package cn.tongyuankeji.dao.information;

import java.util.List;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.information.InformationAttach;
import cn.tongyuankeji.entity.information.InformationSources;

public interface InformationAttachDao extends BaseDAO<InformationSources> {
	
	public List<InformationAttach> getAttachmentsByInfoId(int infoId) throws Exception;

	public InformationAttach getById(int sysId, EnumGenericState stateMin, Boolean lock) throws Exception;
	
	public void deleteInfoAttach(InformationAttach attach) throws Exception;

	public void saveOrUpdate(InformationAttach attach) throws Exception;
}
