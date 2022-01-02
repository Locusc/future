package cn.locusc.mybatis.lagos.cplf.utils;

import java.util.ArrayList;
import java.util.List;

public class ParameterMappingTokenHandler implements TokenHandler {

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    // content是参数名称 #{id}和#{accountName}
    @Override
    public String handleToken(String content) {
        parameterMappings.add(buildParameterMapping(content));
        return "?";
    }

    private ParameterMapping buildParameterMapping(String content) {
        return new ParameterMapping(content);
    }
}
