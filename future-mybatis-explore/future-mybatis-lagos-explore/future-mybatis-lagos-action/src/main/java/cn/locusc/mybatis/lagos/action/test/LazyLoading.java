package cn.locusc.mybatis.lagos.action.test;

import cn.locusc.mybatis.lagos.action.mapper.IOrderMapper;
import cn.locusc.mybatis.lagos.action.mapper.IUserMapper;
import cn.locusc.mybatis.lagos.action.pojo.Order;
import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LazyLoading {

    @Test
    public void test1() throws IOException {

        User findById = userMapper.findById(1L);
        System.out.println(findById.toString());
        List<Order> orderList = findById.getOrderList();
        orderList.forEach(f -> {
            System.out.println(f.toString());
        });
    }

    private IUserMapper userMapper;

    private IOrderMapper orderMapper;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        userMapper = sqlSession.getMapper(IUserMapper.class);

        orderMapper = sqlSession.getMapper(IOrderMapper.class);
    }

}
