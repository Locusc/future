package cn.locusc.mybatis.lagos.action.test;

import cn.locusc.mybatis.lagos.action.dao.IUserDao;
import cn.locusc.mybatis.lagos.action.impl.IUserDaoImpl;
import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    @Test
    public void test() throws IOException {
        // 1.配置文件的加载, 把配置文件加载成字节输入流
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        // 2.解析了配置文件, 并创建了sqlSessionFactory工厂
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        // 3.生产sqlSession
        // openSession默认开启一个事务 但是该事务不会自动提交
        // 在进行增删改操作时要手动提交事务
        // openSession(true) 自动提交
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 4.sqlSession调用方法
        List<Object> objects = sqlSession.selectList("user.findAll");
        objects.forEach(f -> System.out.println(f.toString()));
        sqlSession.close();
    }

    @Test
    public void test2() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(2L);
        user.setAccount_name("jay");

        sqlSession.insert("user.saveUser", user);

        // 提交事务
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void test3() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(2L);
        user.setAccount_name("jay chan");

        sqlSession.update("user.updateUser", user);

        // 提交事务
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void test4() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        sqlSession.delete("user.deleteUser", 2L);

        // 提交事务
        sqlSession.commit();
        sqlSession.close();
    }


    @Test
    public void testTraditionDao() throws IOException {
        IUserDao IUserDao = new IUserDaoImpl();
        List<User> all = IUserDao.findAll();
        all.forEach(f -> System.out.println(f.toString()));
    }

    @Test
    public void testProxy1() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        List<User> all = mapper.findAll();
        all.forEach(f -> System.out.println(f.toString()));
    }

    @Test
    public void testProxy2() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao mapper = sqlSession.getMapper(IUserDao.class);

        User user = new User();
        user.setUsername("jay");
        user.setId(1L);
        List<User> byCondition = mapper.findByCondition(user);
        byCondition.forEach(f -> System.out.println(f.toString()));
    }

    @Test
    public void testProxy3() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserDao mapper = sqlSession.getMapper(IUserDao.class);


        long[] arr = {1L, 1448480091257487361L};
        List<User> byCondition = mapper.findByIds(arr);
        byCondition.forEach(f -> System.out.println(f.toString()));
    }

}
