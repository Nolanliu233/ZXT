package cn.tongyuankeji.dao.structure;

import org.springframework.stereotype.Repository;

import cn.tongyuankeji.common.parameters.EnumCompanyState;
import cn.tongyuankeji.dao.BaseDAOImpl;
import cn.tongyuankeji.entity.structure.Company;

/**
 * Company DAO
 * 
 * @author 代平 2017-09-2 创建
 */
@Repository
public class CompanyDAOImpl extends BaseDAOImpl<Company, Integer> implements CompanyDAO {

	protected CompanyDAOImpl() {
		super(Company.class, "sysId");
	}

	@Override
	public Company getCompanyById(int cpyId, EnumCompanyState minStatus, Boolean lock) throws Exception {
		// TODO Auto-generated method stub
		return super.getById(cpyId, minStatus.byteValue(), lock);
	}

	@Override
	public void saveOrUpdate(Company company) throws Exception {
		getSession().saveOrUpdate(company);
	}

}
