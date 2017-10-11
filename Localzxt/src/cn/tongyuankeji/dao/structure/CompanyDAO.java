package cn.tongyuankeji.dao.structure;

import cn.tongyuankeji.common.parameters.EnumCompanyState;
import cn.tongyuankeji.dao.BaseDAO;
import cn.tongyuankeji.entity.structure.Company;

public interface CompanyDAO extends BaseDAO<Company> {

	public Company getCompanyById(int cpyId, EnumCompanyState minStatus, Boolean lock) throws Exception;

	public void saveOrUpdate(Company company) throws Exception;
}
