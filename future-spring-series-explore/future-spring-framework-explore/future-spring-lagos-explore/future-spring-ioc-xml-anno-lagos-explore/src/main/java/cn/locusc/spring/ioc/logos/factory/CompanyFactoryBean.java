package cn.locusc.spring.ioc.logos.factory;

import cn.locusc.spring.ioc.logos.pojo.Company;
import org.springframework.beans.factory.FactoryBean;

public class CompanyFactoryBean implements FactoryBean<Company> {

    private String companyInfo; // 公司名称,地址,规模

    public void setCompanyInfo(String companyInfo) {
        this.companyInfo = companyInfo;
    }

    @Override
    public Company getObject() throws Exception {
        // 创建复杂对象Company
        Company company = new Company();
        String[] split = companyInfo.split(",");
        company.setName(split[0]);
        company.setAddress(split[1]);
        company.setScale(Integer.parseInt(split[2]));
        return company;
    }

    @Override
    public Class<?> getObjectType() {
        return Company.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
