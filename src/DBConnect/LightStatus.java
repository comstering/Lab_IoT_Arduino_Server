package DBConnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LightStatus {
	private static LightStatus instance = new LightStatus();
	
	public static LightStatus getInstance() {
		return instance;
	}
	
	private DBConnector dbc = DBConnector.getInstance();
	private Connection conn;    //  connecttion:db에 접근하게 해주는 객체
	private PreparedStatement pstmt;    //  db에 sql문을 전달하는 객체
	private String sql;    //  sql 쿼리를 저장하기 위한 변수
	private ResultSet rs;    //  db에서 sql쿼리를 실행한 결과를 가져오는 객체
	private String returns;    //  db 연결결과를 return하는 변수
	
	public String lightStatusUpdate(String check, String st) {
		if(check.equals("security")) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(dbc.getURL(), dbc.getID(), dbc.getPassword());
				sql = "update iot set light=?";
				pstmt = conn.prepareStatement(sql);    //  db와 접근하기 위한 쿼리 저장
				pstmt.setString(1, st);    //  sql문에 ?를 st로 변환
				pstmt.executeUpdate();    //  db에 쿼리문 날리기
				returns = "ok";
			} catch(Exception e) {
				e.printStackTrace();
				returns = "error";
			}
		}
		return returns;
	}
	
	public String lightStatusCheck(String androidCheck) {
		if(androidCheck.equals("security")) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(dbc.getURL(), dbc.getID(), dbc.getPassword());
				sql = "select light from iot";
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if(rs.next())
					returns = rs.getString("light");
				else
					returns = "no";
			} catch(Exception e) {
				e.printStackTrace();
				returns = "error";
			} finally {
				if (rs != null)try {rs.close();} catch (SQLException ex) {}
				if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
				if (conn != null)try {conn.close();} catch (SQLException ex) {}
			}
		}
		else
			returns = "noAndroid";
		return returns;
	}
}
