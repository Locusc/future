package cn.locusc.spring.ioc.logos.dao.impl;

import cn.locusc.spring.ioc.logos.dao.AccountDao;
import cn.locusc.spring.ioc.logos.pojo.Account;
import cn.locusc.spring.ioc.logos.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author 应癫
 */
public class JdbcAccountDaoImpl implements AccountDao {

    private ConnectionUtils connectionUtils;

    public JdbcAccountDaoImpl(ConnectionUtils connectionUtils, String name) {
        this.connectionUtils = connectionUtils;
        this.name = name;
    }

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    private String name;
    private String[] myArray;
    private Map<String, String> myMap;
    private Set<String> mySet;
    private Properties myProperties;

    public void setMyArray(String[] myArray) {
        this.myArray = myArray;
    }

    public void setMyMap(Map<String, String> myMap) {
        this.myMap = myMap;
    }

    public void setMySet(Set<String> mySet) {
        this.mySet = mySet;
    }

    public void setMyProperties(Properties myProperties) {
        this.myProperties = myProperties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init() {
        System.out.println("初始化方法.....");
    }

    public void destroy() {
        System.out.println("销毁方法......");
    }

    @Override
    public Account queryAccountByCardNo(String cardNo) throws Exception {
        //从连接池获取连接
        // Connection con = DruidUtils.getInstance().getConnection();
        // Connection con = ConnectionUtils.getInstance().getCurrentThreadConnection();
        Connection con = connectionUtils.getCurrentThreadConnection();
        String sql = "select * from account where cardNo=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1,cardNo);
        ResultSet resultSet = preparedStatement.executeQuery();

        Account account = new Account();
        while(resultSet.next()) {
            account.setCardNo(resultSet.getString("cardNo"));
            account.setName(resultSet.getString("name"));
            account.setMoney(resultSet.getInt("money"));
        }

        resultSet.close();
        preparedStatement.close();
        // con.close();

        return account;
    }

    @Override
    public int updateAccountByCardNo(Account account) throws Exception {

        // 从连接池获取连接
        // 改造为从当前线程获取绑定的connection连接
        //Connection con = ConnectionUtils.getInstance().getCurrentThreadConnection();
        // Connection con = DruidUtils.getInstance().getConnection();
        Connection con = connectionUtils.getCurrentThreadConnection();
        String sql = "update account set money=? where cardNo=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1,account.getMoney());
        preparedStatement.setString(2,account.getCardNo());
        int i = preparedStatement.executeUpdate();

        preparedStatement.close();
        // con.close();
        return i;
    }
}
