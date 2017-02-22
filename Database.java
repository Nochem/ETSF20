import java.sql.*;

/*
 * Class for managing the database.
 */
public class Database {

	// If you have the mysql server on your own computer use "localhost" as
	// server address.
	private static String databaseServerAddress = "vm23.cs.lth.se";
	private static String databaseUser = "fte11ama"; // database login user
	private static String databasePassword = "bkd7uz6v"; // database login
															// password
	private static String database = "fte11ama"; // the database to use, i.e.
													// default schema
	Connection conn = null;

	public Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Necessary on Windows
													// computers
			conn = DriverManager.getConnection("jdbc:mysql://" + databaseServerAddress + "/" + database, databaseUser,
					databasePassword);

			// Display the contents of the database in the console.
			// This should be removed in the final version
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from Respondents");
			while (rs.next()) {
				String name = rs.getString("name");
				System.out.println(name);
			}

			stmt.close();
		} catch (SQLException e) {
			printSqlError(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean addValues(int s1, int s2, int s3, int s4, String name) {
		boolean resultOK = false;
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE Respondents SET q1=(?), q2=(?), q3=(?), q4=(?) WHERE name=(?);";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s1);
			ps.setInt(2, s2);
			ps.setInt(3, s3);
			ps.setInt(4, s4);
			ps.setString(5, name);
			ps.executeUpdate();
			resultOK = true;
			ps.close();
		} catch (SQLException e) {
			printSqlError(e);
			resultOK = false; // one reason may be that the name is already in
								// the database
			System.out.println(name + "Error in addValues method");
			
		}
		return resultOK;
	}

	
	public boolean addName(String name) {
		boolean resultOK = false;
		PreparedStatement ps = null;
		try {
			String sql = "insert into Respondents (name) values(?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.executeUpdate();
			resultOK = true;
			ps.close();
		} catch (SQLException e) {
			resultOK = false; // one reason may be that the name is already in
								// the database
			if (e.getErrorCode() == 1062 && e.getSQLState().equals("23000")) {
				// duplicate key error
				System.out.println(name + " already exists in the database");
			} else {
				printSqlError(e);
			}
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					printSqlError(e);
				}
			}
		}
		return resultOK;
	}

	public void addProject(String project, String name){
		PreparedStatement ps = null;
		try {
			String sql = "insert into ProjectMembers (project, name) values (?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, project);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println("Error in addProject");
		}
	}
	
	public String showDB(){
		StringBuilder SB = new StringBuilder();
		try {
			Statement stmt = conn.createStatement(); // project, Respondents.name, q1, q2, q3, q4 // and Respondents.name='(?)' // where Respondents.name=ProjectMembers.name;
			ResultSet rs = stmt.executeQuery("select * from ProjectMembers natural join Respondents;");
			while (rs.next()){
				SB.append("<p>" +rs.getString(1) + " ");
				SB.append(rs.getString(2) + " ");
				SB.append(rs.getString(3) + " ");
				SB.append(rs.getString(4) + " ");
				SB.append(rs.getString(5) + " ");
				SB.append(rs.getString(6) + " " + "</p>");
			}
			stmt.close();
			return SB.toString();
		} catch (SQLException e) {
			System.out.println("SQLException in showDB");
			return null;
		}
	}
	
	
	private void printSqlError(SQLException e) {
		System.out.println("SQLException: " + e.getMessage());
		System.out.println("SQLState: " + e.getSQLState());
		System.out.println("VendorError: " + e.getErrorCode());
		e.printStackTrace();
	}

}
