package cn.locusc.mybatis.lagos.action.test;

import cn.locusc.mybatis.lagos.action.mapper.IUserMapper;
import cn.locusc.mybatis.lagos.action.mapper.TkUserMapper;
import cn.locusc.mybatis.lagos.action.pojo.TkUser;
import cn.locusc.mybatis.lagos.action.pojo.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PluginsTest {

    private IUserMapper iUserMapper;
    private SqlSession sqlSession;
    private SqlSessionFactory sqlSessionFactory;
    private TkUserMapper tkUserMapper;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession();

        iUserMapper = sqlSession.getMapper(IUserMapper.class);
        tkUserMapper = sqlSession.getMapper(TkUserMapper.class);
    }

    @Test
    public void pageHelperTest() {
        PageHelper.startPage(1, 1);
        List<User> users = iUserMapper.selectUser();
        users.forEach(f -> System.out.println(f.toString()));

        PageInfo<User> userPageInfo = new PageInfo<>(users);
        System.out.println(userPageInfo.toString());
        System.out.println("总条数: " + userPageInfo.getTotal());
        System.out.println("总页数: " + userPageInfo.getPages());
        System.out.println("当前页: " + userPageInfo.getPageNum());
        System.out.println("每页显示条数: " + userPageInfo.getPageSize());
    }

    @Test
    public void tkMybatisTest() {
        List<TkUser> select = tkUserMapper.select(null);
        select.forEach(f -> System.out.println(f.toString()));

        Example example = new Example(TkUser.class);
        example.createCriteria().andEqualTo("id", 1L);
        List<TkUser> tkUsers = tkUserMapper.selectByExample(example);
        tkUsers.forEach(f -> System.out.println(f.toString()));
    }

}
