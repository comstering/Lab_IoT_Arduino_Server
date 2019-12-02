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
	private Connection conn;    //  connecttion:db�� �����ϰ� ���ִ� ��ü
	private PreparedStatement pstmt;    //  db�� sql���� �����ϴ� ��ü
	private String sql;    //  sql ������ �����ϱ� ���� ����
	private ResultSet rs;    //  db���� sql������ ������ ����� �������� ��ü
	private String returns;    //  db �������� return�ϴ� ����
	
	public String lightStatusUpdate(String check, String st) {
		if(check.equals("security")) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(dbc.getURL(), dbc.getID(), dbc.getPassword());
				sql = "update iot set light=?";
				pstmt = conn.prepareStatement(sql);    //  db�� �����ϱ� ���� ���� ����
				pstmt.setString(1, st);    //  sql���� ?�� st�� ��ȯ
				pstmt.executeUpdate();    //  db�� ������ ������
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