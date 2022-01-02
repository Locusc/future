package cn.locusc.mybatis.lagos.comsume.jdbc;

import java.sql.*;
import java.util.HashMap;

/**
 * @author Jay
 * jdbc原始写法
 * 2022/1/2
 */
public class JDBCTraditional {

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 通过驱动管理类获取数据库连接
            connection = DriverManager.getConnection(
                    "jdbc:mysql://39.107.96.199:10086/mercy?characterEncoding=utf-8",
                    "xxxx",
                    "xxxx"
            );
            // 定义sql语句? 表示占位符
            String sql = "select * from sys_user where account_name = ?";
            // 获取预处理的statement
            preparedStatement = connection.prepareStatement(sql);
            // 设置参数，第⼀个参数为sql语句中参数的序号(从1开始)，第⼆个参数为设置的参数值
            preparedStatement.setString(1, "locusc");
            // 向数据库发出sql执行查询, 查询出结果集
            resultSet = preparedStatement.executeQuery();

            HashMap<String, Object> map = new HashMap<>();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                int is_del = resultSet.getInt("is_del");

                map.put("id", id);
                map.put("is_del", is_del);
            }

            System.out.println(map.toString());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
