package cn.locusc.mybatis.lagos.comsume.dao;

import cn.locusc.mybatis.lagos.comsume.pojo.User;
import cn.locusc.mybatis.lagos.cplf.io.Resources;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSession;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSessionFactory;
import cn.locusc.mybatis.lagos.cplf.sqlSession.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class UserDaoImpl implements IUserDao {

    @Override
    public List<User> findAll() throws Exception {
        InputStream resourceAsInputStream = Resources.getResourceAsInputStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsInputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        return sqlSession.selectList("user.selectList");
    }

    @Override
    public User findByCondition(User user) throws Exception {
        InputStream resourceAsInputStream = Resources.getResourceAsInputStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsInputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        return sqlSession.selectOne("user.selectOne", user);
    }

}
