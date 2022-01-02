package cn.locusc.mybatis.lagos.cplf.sqlSession;

import cn.locusc.mybatis.lagos.cplf.config.BoundSql;
import cn.locusc.mybatis.lagos.cplf.pojo.Configuration;
import cn.locusc.mybatis.lagos.cplf.pojo.MappedStatement;
import cn.locusc.mybatis.lagos.cplf.utils.GenericTokenParser;
import cn.locusc.mybatis.lagos.cplf.utils.ParameterMapping;
import cn.locusc.mybatis.lagos.cplf.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // 1.注册驱动, 获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2.获取sql语句 select * from mercy_user where id = #{id} and account_name = #{accountName}
        // 转换sql语句: select * from mercy_user where id = ? and account_name = ?
        // 转换过程中, 还要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();

        BoundSql boundSql = getBoundSql(sql);

        // 3.获取预处理对象:  preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4.设置参数
        // 获取到参数的全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass  = getClassType(parameterType);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String content = parameterMapping.getContent();

            // 反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            // 暴力访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            preparedStatement.setObject(i+1, o);
        }

        // 5.执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        // 6.封装返回结果集
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = this.getClassType(resultType);

        ArrayList<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            // 元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 获取字段的值
                Object value = resultSet.getObject(columnName);
                // 使用反射或者内省, 根据数据库表和实体的对应关系, 完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                // 将对象的具体值封装到对象当中
                writeMethod.invoke(o, value);
            }

            objects.add(o);
        }

        return (List<E>) objects;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if(parameterType != null) {
            return Class.forName(parameterType);
        }

        return null;
    }

    // 对#{}的解析工作: 1.将#{}使用?代替 2.解析出#{}里面的值进行存储
    private BoundSql getBoundSql(String sql) {
        // 标记处理类: 配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);

        // 解析出来的sql
        String parseSql = genericTokenParser.parse(sql);

        // #{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        return new BoundSql(parseSql, parameterMappings);
    }

}
