package DBConnect;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Security.AESDec;
import Security.Crypto;

public class DoorStatus {
	private DBConnector dbc;    //  db접근에 필요한 db connector
	private Connection conn;    //  connecttion:db에 접근하게 해주는 객체
	private PreparedStatement pstmt;    //  db에 sql문을 전달하는 객체
	private String sql;    //  sql 쿼리를 저장하기 위한 변수
	private ResultSet rs;    //  db에서 sql쿼리를 실행한 결과를 가져오는 객체
	private String returns;    //  db 연결결과를 return하는 변수
	
	private Crypto crypto;

	public DoorStatus() {
		dbc = new DBConnector();    //  DBConnector 객체 생성
		conn = dbc.getConn();    //  connection 생성 및 db 연결
		crypto = new Crypto();
	}
	
	public String doorStatusUpdate(String check, String value) {    //  아두이노에서 문 상태 업데이트
		if(dbc.arduinoCheck(check)) {    //  아두이노 접근이 맞는지 확인
			try {
				sql = "update iot set door=?";    //  door 상태 업데이트 시키는 쿼리문
				pstmt = conn.prepareStatement(sql);    //  db에 접근하기 위한 쿼리 저장
				pstmt.setString(1, dbc.decryptoString(value));    //  sql문에 ?를 복호화된 st로 변환
				pstmt.executeUpdate();    //  db에 쿼리문 날리기
				returns = "ok";    //  db에 올바르게 업뎃하면 ok return
			} catch (SQLException e) {    //  예외 처리
				System.err.println("DoorStatus Update SQLException error");
				returns = "SQLError";
			} finally {    //  db접속이 끝나면 초기화 및 닫아주기
				try {
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (SQLException e) {
					System.err.println("DoorStatus Update PreparedStatement close SQLException error");
				}
			}
		}
		else {    //  아두이노 접근이 아닐 경우 noArduino return
			returns="no Arduino";
		}
		return returns;
	}

	public String doorStatusCheck(String check, String key) {    //  안드로이드에서 문상태 확인
		try {
			if("security".equals(AESDec.aesDecryption(check, crypto.decrypto(key)))) {    //  안드로이드 접근이 맞는지 확인
				try {
					sql = "select door from iot";    //  door 상태 체크하는 쿼리문
					pstmt = conn.prepareStatement(sql);    //  db에 접근하기 위한 쿼리 저장
					rs = pstmt.executeQuery();    //  db에 쿼리문 날리기
					//  db에서 쿼리문 날리고 얻은 결과값 가져오기
					if(rs.next()) {    //  결과값 있으면 결과값 return
						String value;
						value = rs.getString("door");
						if(value.equals("1")) {
							returns = "open-";
						} else if(value.equals("0")) {
							returns = "close-";
						}
					}
					else {    //  결과값 없으면 no return
						returns = "no";
					}
				} catch (SQLException e) {    //  예외 처리
					System.err.println("DoorStatus Check SQLException error");
					returns = "SQLError";
				} finally {    //  db접속이 끝나면 초기화 및 닫아주기
					try {
						if (rs != null) {
							rs.close();
						}
						if (pstmt != null) {
							pstmt.close();
						}
					} catch (SQLException e) {
						System.err.println("DoorStatus Check close SQLException error");
					}
				}
			}
			else {    //  안드로이드 접근이 아닐 경우 noAndroid return
				returns = "noAndroid";
			}
		} catch (InvalidKeyException e) {
			System.err.println("DoorStatus Check InvalidKeyException error");
		} catch (UnsupportedEncodingException e) {
			System.err.println("DoorStatus Check UnsupportedEncodingException error");
		} catch (NoSuchPaddingException e) {
			System.err.println("DoorStatus Check NoSuchPaddingException error");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("DoorStatus Check NoSuchAlgorithmException error");
		} catch (InvalidAlgorithmParameterException e) {
			System.err.println("DoorStatus Check InvalidAlgorithmParameterException error");
		} catch (BadPaddingException e) {
			System.err.println("DoorStatus Check BadPaddingException error");
		} catch (IllegalBlockSizeException e) {
			System.err.println("DoorStatus Check IllegalBlockSizeException error");
		}
		return returns;
	}
}
