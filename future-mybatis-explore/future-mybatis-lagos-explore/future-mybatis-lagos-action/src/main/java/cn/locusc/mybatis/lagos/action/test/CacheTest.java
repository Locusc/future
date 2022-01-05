package cn.locusc.mybatis.lagos.action.test;

import cn.locusc.mybatis.lagos.action.mapper.IUserMapper;
import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CacheTest {

    private IUserMapper userMapper;
    private SqlSession sqlSession;
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession();

        userMapper = sqlSession.getMapper(IUserMapper.class);
    }

    /**
     * 一级缓存底层以hashMap存储
     *  key: statementId + params(入参) + boundSql(处理后的sql对象) + rowBounds(分页对象)
     */
    @Test
    public void firstLevel() {
        // 第一次查询id为1的用户
        // 首先去一级缓存中查询, 有就直接返回, 没有就查询数据库
        // 同时将查询出来的结果存在一级缓存中
        User user1 = userMapper.findUserById(1L);

        // 更新用户
        // 做增删改, 并进行了事务提交会刷新一级缓存
        User user = new User();
        user.setId(1L);
        user.setAccount_name("jay chan");
        userMapper.updateUser(user);
        sqlSession.commit();
        // 手动刷新一级缓存
        // sqlSession.clearCache();

        // 第二次查询id为1的用户
        // 首先是去一级缓存中去查询, 有直接发返回, 没有就查询数据库
        // 同时将查询出来的结果存在一级缓存中
        User user2 = userMapper.findUserById(1L);

        // true
        System.out.println(user1==user2);
    }

    @Test
    public void secondLevel() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();

        IUserMapper mapper1 = sqlSession1.getMapper(IUserMapper.class);
        IUserMapper mapper2 = sqlSession2.getMapper(IUserMapper.class);
        IUserMapper mapper3 = sqlSession3.getMapper(IUserMapper.class);

        User user1 = mapper1.findUserById(1L);
        sqlSession1.close(); // 清空一级缓存

//        User user = new User();
//        user.setId(1L);
//        user.setAccount_name("locusc");
//        mapper3.updateUser(user);
//        sqlSession3.commit();

        User user2 = mapper2.findUserById(1L);

        // false
        // 二级缓存缓存的是数据不是对象
        // 会将数据重新封装成一个新的对象
        System.out.println(user1==user2);
    }

}
