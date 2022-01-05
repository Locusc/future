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

public class MybatisMappingTest {

    // 一对一
    @Test
    public void test1() throws IOException {

        List<Order> orderAndUser = orderMapper.findOrderAndUser();
        orderAndUser.forEach(f -> {
            System.out.println(f.toString());
            System.out.println(f.getUser().toString());
        });
    }

    // 一对多
    @Test
    public void test2() throws IOException {
        List<User> all = userMapper.findAll();
        all.forEach(f -> {
            System.out.println(f.toString());
            f.getOrderList().forEach(s -> System.out.println(s.toString()));
            System.out.println("-------------------");
        });
    }


    // 多对多
    @Test
    public void test3() throws IOException {

        List<User> allUserAndRole = userMapper.findAllUserAndRole();
        allUserAndRole.forEach(f -> {
            System.out.println(f.toString());
            f.getRoleList().forEach(s -> System.out.println(s.toString()));
            System.out.println("-------------------");
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

    @Test
    public void addUser() throws IOException {
        User user = new User();
        user.setId(3L);
        user.setAccount_name("jay");

        userMapper.addUser(user);
    }

    @Test
    public void updateUser() throws IOException {
        User user = new User();
        user.setId(3L);
        user.setAccount_name("jay chan");

        userMapper.updateUser(user);
    }

    @Test
    public void deleteUser() throws IOException {
        userMapper.deleteUser(3L);
    }

    @Test
    public void selectUser() throws IOException {
        List<User> users = userMapper.selectUser();
        System.out.println(users.toString());
    }

    // 注解一对一
    @Test
    public void oneToOne() throws IOException {
        List<Order> orders = orderMapper.findOrderAndUserAnnotation();
        orders.forEach(f -> {
            System.out.println(f.toString());
            System.out.println(f.getUser().toString());
        });
    }

    // 注解一对多
    @Test
    public void oneToMany() throws IOException {
        List<User> allAnnotation = userMapper.findAllAnnotation();
        allAnnotation.forEach(f -> {
            System.out.println(f.toString());
            f.getOrderList().forEach(s -> System.out.println(s.toString()));
            System.out.println("----------------");
        });
    }

    // 注解一对多
    @Test
    public void manyToMany() throws IOException {
        List<User> allUserAndRoleAnnotation = userMapper.findAllUserAndRoleAnnotation();
        allUserAndRoleAnnotation.forEach(f -> {
            System.out.println(f.toString());
            f.getRoleList().forEach(s -> System.out.println(s.toString()));
            System.out.println("----------------");
        });
    }

}
