package com.southwind.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.southwind.entity.User;
//import com.xxxx.seckill.pojo.User;
//import com.xxxx.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成用户工具类
 * <p>
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UserUtil {
	private static void createUser(int count) throws Exception {
		List<User> users = new ArrayList<>(count);
		//生成用户
		for (int i = 0; i < count; i++) {
			User user = new User();
			user.setId(130 + i);
			user.setLoginName("test1"+i);
			user.setUserName("user" + i);
			user.setPassword(MD5Util.getSaltMD5("123456"));
			user.setGender(1);
			user.setEmail("11"+i+"@qq.com");
			user.setMobile("134565"+i);
			user.setCreateTime(LocalDateTime.now());
			user.setUpdateTime(LocalDateTime.now());
			users.add(user);
		}
		System.out.println("create user");
		// // //插入数据库
		 Connection conn = getConn();
		 String sql = "insert into user(id, login_name, user_name, password, gender,email,mobile,create_time,update_time)values(?,?,?,?,?,?,?,?,?)";
		 PreparedStatement pstmt = conn.prepareStatement(sql);
		 for (int i = 0; i < users.size(); i++) {
		 	User user = users.get(i);
		 	pstmt.setInt(1, user.getId());
		 	pstmt.setString(2, user.getLoginName());
		 	pstmt.setString(3, user.getUserName());
		 	pstmt.setString(4, user.getPassword());
		 	pstmt.setInt(5, user.getGender());
		 	pstmt.setString(6,user.getEmail());
		 	pstmt.setString(7,user.getMobile());
		 	pstmt.setDate(8,new Date(5000));
		 	pstmt.setDate(9,new Date(5000));
		 	pstmt.addBatch();
		 }
		 pstmt.executeBatch();
		 pstmt.close();
		 conn.close();
		 System.out.println("insert to db");
		//登录，生成userTicket
		String urlString = "http://localhost:8080/login/";
		File file = new File("C:\\Users\\20211218\\Desktop\\config2.txt");
		if (file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection) url.openConnection();
			co.setRequestMethod("GET");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "mobile=" + user.getLoginName() + "&password=" + MD5Util.getSaltMD5("123456");
			out.write(params.getBytes());
			out.flush();
//			InputStream inputStream = co.getInputStream();
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			byte buff[] = new byte[1024];
//			int len = 0;
//			while ((len = inputStream.read(buff)) >= 0) {
//				bout.write(buff, 0, len);
//			}
//			inputStream.close();
//			bout.close();
//			String response = new String(bout.toByteArray());
//			ObjectMapper mapper = new ObjectMapper();
//			RespBean respBean = mapper.readValue(response, RespBean.class);
//			String userTicket = ((String) respBean.getObj());
//			System.out.println("create userTicket : " + user.getId());

//			String row = user.getId() + "," + userTicket;
//			raf.seek(raf.length());
//			raf.write(row.getBytes());
//			raf.write("\r\n".getBytes());
//			System.out.println("write to file : " + user.getId());
		}
		raf.close();

		System.out.println("over");
	}

	private static Connection getConn() throws Exception {
		String url = "jdbc:mysql://localhost:3306/mmall?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
		String username = "root";
		String password = "admin";
		String driver = "com.mysql.cj.jdbc.Driver";
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
	}

	public static void main(String[] args) throws Exception {
		createUser(5000);
	}
}
