package cn.locusc.mybatis.lagos.comsume;

import cn.locusc.mybatis.lagos.comsume.dao.IUserDao;
import cn.locusc.mybatis.lagos.comsume.pojo.User;
import cn.locusc.mybatis.lagos.cplf.io.Resources;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSession;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSessionFactory;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class Consumer {

    @Test
    public void test() throws Exception {
        InputStream resourceAsInputStream = Resources.getResourceAsInputStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsInputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(1L);
        user.setAccount_name("locusc");
        //User user1 = sqlSession.selectOne("user.selectOne", user);

        //System.out.println(user1);

        //List<User> users = sqlSession.selectList("user.selectList");

        // 代理对象调用接口中任意方法, 都会执行invoke方法
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        List<User> users = userDao.findAll();
        users.forEach(f -> System.out.println(f.toString()));
    }

}
