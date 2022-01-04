package cn.locusc.mybatis.lagos.action.impl;

import cn.locusc.mybatis.lagos.action.dao.IUserDao;
import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IUserDaoImpl implements IUserDao {

    @Override
    public List<User> findAll() throws IOException {
        InputStream resourceAsStream =
                Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new
                SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<User> userList = sqlSession.selectList("user.findAll");
        sqlSession.close();
        return userList;
    }

    @Override
    public List<User> findByCondition(User user) {
        return null;
    }

    @Override
    public List<User> findByIds(long[] ids) {
        return null;
    }

}
