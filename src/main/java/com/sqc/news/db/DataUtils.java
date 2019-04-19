package com.sqc.news.db;

import java.sql.*;
import java.util.List;
public class DataUtils {
    public static final String driver_class = "com.mysql.jdbc.Driver";
    public static final String driver_url = "jdbc:mysql://localhost/studb?useunicode=true&characterEncoding=utf8";
    public static final String user = "root";
    public static final String password = "1234";
    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private ResultSet rst = null;
    
    public static boolean persisit(List<NewsData> lists) {
		String sqlStr = "insert into news(title,editor,source,publishtime,inserttime) values(?,?,?,?,?)";
		try {
			Class.forName(driver_class);
			conn = DriverManager.getConnection(driver_url, user, password);
			ps = conn.prepareStatement(sqlStr);
			 // 遍历记录集，并将各字段数据读取封装到数据集合当中
            for (NewsData entity : lists) {                 	             	 
				ps.setString(1, entity.getTitle());
				ps.setString(2, entity.getEditor());
				ps.setString(3, entity.getSource());			
				ps.setTimestamp(4, new Timestamp(entity.getDate().getTime()));
				ps.setTimestamp(5, new Timestamp(entity.getInserttime().getTime()));			
				ps.addBatch(); //批量插入
			}
            ps.executeBatch();  //批量插入 
        } catch (Exception e) {
       	 System.out.println("连接失败");
            e.printStackTrace();
        } finally {
        	if (ps!=null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	if (conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }
		return true;		
        }
  
}

	
      

