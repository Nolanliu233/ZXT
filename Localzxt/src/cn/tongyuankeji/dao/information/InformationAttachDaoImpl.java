package cn.tongyuankeji.dao.information;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumGenericState;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.information.InformationAttach;

@Repository
public class InformationAttachDaoImpl extends BaseDAOImpl<InformationAttach, Integer> implements InformationAttachDao {

	protected InformationAttachDaoImpl() {
		super(InformationAttach.class, "sysId");
	}

	@Override
	public List<InformationAttach> getAttachmentsByInfoId(int infoId) throws Exception {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("infoId", infoId));
		return super.getEntityList(criterions, null, false);
	}

	@Override
	public InformationAttach getById(int sysId, EnumGenericState stateMin, Boolean lock) throws Exception{
		return super.getById(sysId, stateMin == null ? null : stateMin.byteValue(), lock);
	}
	
	@Override
	public void deleteInfoAttach(InformationAttach attach) throws Exception {
		getSession().delete(attach);
	}

	@Override
	public void saveOrUpdate(InformationAttach attach) throws Exception {
		getSession().saveOrUpdate(attach);
	}

}
