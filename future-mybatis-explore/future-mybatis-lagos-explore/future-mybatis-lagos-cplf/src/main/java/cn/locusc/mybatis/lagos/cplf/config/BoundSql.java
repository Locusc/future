package cn.locusc.mybatis.lagos.cplf.config;

import cn.locusc.mybatis.lagos.cplf.utils.ParameterMapping;

import java.util.List;

public class BoundSql {

    private String sqlText;

    private List<ParameterMapping> parameterMappings;

    public BoundSql(String sqlText, List<ParameterMapping> parameterMappings) {
        this.sqlText = sqlText;
        this.parameterMappings = parameterMappings;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }
}
