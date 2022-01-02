package cn.locusc.mybatis.lagos.cplf.sqlSession;

import cn.locusc.mybatis.lagos.cplf.pojo.Configuration;
import cn.locusc.mybatis.lagos.cplf.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

}
