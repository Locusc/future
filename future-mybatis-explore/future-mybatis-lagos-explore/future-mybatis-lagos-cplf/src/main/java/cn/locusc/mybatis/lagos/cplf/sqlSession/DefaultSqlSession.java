package cn.locusc.mybatis.lagos.cplf.sqlSession;

import cn.locusc.mybatis.lagos.cplf.pojo.Configuration;
import cn.locusc.mybatis.lagos.cplf.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        //将要去完成对simpleExecutor里的query方法调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();

        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.query(configuration, mappedStatement, params);
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<T> objects = this.selectList(statementId, params);
        if (objects.size() == 1) {
            return objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用jdk动态代理来为Dao接口生成代理对象并返回
        Object o = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            /**
             * @param proxy 当前代理对象的引用
             * @param method 当前被调用方法的引用
             * @param args 传递的参数
             * @return java.lang.Object
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码
                // 根据不同的情况, 来调用selectList或者selectOne
                // 准备参数 1.statementId(sql语句的唯一标识 namespace.id)

                // 在invoke方法中无法获取映射文件中namespace.id
                // 但是可以借助method对象来获取到当前执行的方法名以及当前方法所在类的全限定名
                // namespace.id 接口全限定名.方法名

                // 方法名 findAll
                String methodName = method.getName();

                // 类的全限定名
                String className = method.getDeclaringClass().getName();

                String statementId = String.format("%s.%s", className, methodName);

                // 准备参数2: params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                // 判断是否进行类 泛型类型参数化
                if(genericReturnType instanceof ParameterizedType) {
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                return selectOne(statementId, args);
            }
        });
        return (T) o;
    }

}
