package Lsh0708_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Lsh0708_Project_DAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String userid = "scott";
	String passwd = "tiger";

	public Lsh0708_Project_DAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 부서, 검색 조건에 따라 검색하기
	public ArrayList<Lsh0708_project_DTO> select(String dept, ArrayList<String> selectedItem) {
		ArrayList<Lsh0708_project_DTO> list = new ArrayList<Lsh0708_project_DTO>();
		ArrayList<String> nullRemoveList = new ArrayList<String>();
		for (String item : selectedItem) {
			if (item != null) {
				nullRemoveList.add(item);
			}
		}
		// DB 검색을 위한 조건 나열
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < nullRemoveList.size(); i++) {
			stringBuilder.append(nullRemoveList.get(i));
			// 마지막 요소가 아니면 ", " 추가
			if (i < nullRemoveList.size() - 1) {
				stringBuilder.append(", ");
			}
		}

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			// 오름차순으로 검색하기
			if (dept.equals("전체")) {
				query = "SELECT " + stringBuilder + " FROM employee order by id ASC";
			} else {
				query = "SELECT " + stringBuilder + " FROM employee WHERE department=\'" + dept + "\' order by id ASC";
			}
			pstmt = con.prepareStatement(query);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				Lsh0708_project_DTO dto = new Lsh0708_project_DTO();
				for (String column : nullRemoveList) {
					switch (column) {
					case "id":
						dto.setId(rs.getInt("id"));
						break;
					case "name":
						dto.setName(rs.getString("name"));
						break;
					case "department":
						dto.setDepartment(rs.getString("department"));
						break;
					case "birthdate":
						dto.setBirthdate(rs.getString("birthdate"));
						break;
					case "address":
						dto.setAddress(rs.getString("address"));
						break;
					case "telNum":
						dto.setTelNum(rs.getString("telNum"));
						break;
					case "sex":
						dto.setSex(rs.getString("sex"));
						break;
					default:
						break;
					}
				}
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	};

	// 사용자 정보 수정을 위해 해당 사용자 검색 후 반환 - 사용사 id 이용(사용자 id값 primary key)
	public Lsh0708_project_DTO selectOne(int userID) {

		Lsh0708_project_DTO dto = new Lsh0708_project_DTO();
		System.out.println(userID);
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			query = "SELECT * FROM employee where id = ? ";
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, userID);
			rs = pstmt.executeQuery();

			if (rs.next()) {

				dto.setId(rs.getInt("id"));
				dto.setName(rs.getString("name"));
				dto.setDepartment(rs.getString("department"));
				dto.setBirthdate(rs.getString("birthdate"));
				dto.setAddress(rs.getString("address"));
				dto.setTelNum(rs.getString("telNum"));
				dto.setSex(rs.getString("sex"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dto;
	};

	// 사용자 추가
	public int insertDB(String name, String department, String birthdate, String address, String telNum, String sex) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result1 = 0;
		int lastId = 0;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			// 마지막 사용자 id 이용하기 위해 내림차순으로 조회
			String sql2 = "SELECT * FROM employee order by id desc";
			PreparedStatement stmt = con.prepareStatement(sql2);
			ResultSet rs = stmt.executeQuery(sql2);
			if (rs.next()) {
				lastId = rs.getInt(1) + 1;
			}

			String sql = "INSERT INTO employee(id,name,department,birthdate,address,telNum,sex)"
					+ "VALUES(?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, lastId);
			pstmt.setString(2, name);
			pstmt.setString(3, department);
			pstmt.setString(4, birthdate);
			pstmt.setString(5, address);
			pstmt.setString(6, telNum);
			pstmt.setString(7, sex);
			result1 = pstmt.executeUpdate();
			System.out.println(result1 + "개의 레코드가 저장");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return result1;
	} // insert

	// 사용자 정보수정
	public int modifyDB(int id, String name, String department, String address, String telNum, String sex) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			con = DriverManager.getConnection(url, userid, passwd);
			
			//id를 이용해 해당 id의 정보 수정 - id, 생년월일은 변경될 일 없음
			String sql = "UPDATE employee set name=?,department=?,address=?,telNum=?,sex=? WHERE id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, department);
			pstmt.setString(3, address);
			pstmt.setString(4, telNum);
			pstmt.setString(5, sex);
			pstmt.setInt(6, id);
			result = pstmt.executeUpdate();
			System.out.println(result + "개의 레코드가 변경");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return result;
	} // modify

	// 사용자 삭제
	public int deleteUser(ArrayList<String> delUser) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int totalDeletedCount = 0;
//		if (delUser == null || delUser.isEmpty()) {
//			return totalDeletedCount;
//		}
		try {

			con = DriverManager.getConnection(url, userid, passwd);
			
			String sql = "DELETE FROM employee WHERE id = ?";
			pstmt = con.prepareStatement(sql);
			// 선택된 모든 사용자 삭제 
			for (String userId : delUser) {
				pstmt.setString(1, userId);
				int deletedCount = pstmt.executeUpdate();
				totalDeletedCount += deletedCount; // 삭제된 개수를 누적
				System.out.println(userId + " 삭제 완료");
			}

			System.out.println(totalDeletedCount + "개의 레코드 삭제");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} // finally
		return totalDeletedCount;
	} // delete
}
