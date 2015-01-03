import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class myDatabase
{
	public static Connection conn;
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException
	{
		setupConnection();
		
		while (true)
			performAction();
	}
	
	public static void setupConnection() throws SQLException, ClassNotFoundException
	{
		String driverName = "oracle.jdbc.driver.OracleDriver";
		Class.forName(driverName);
		
		String serverName = "147.46.15.238";
		String portNumber = "1521";
		String sid = "orcl";
		String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
		String userName = "DB-2011-11693";
		String password = "DB-2011-11693";
		
		conn = DriverManager.getConnection(url, userName, password);
	}
	
	public static void displayActionList()
	{
		System.out.println("===========");
		System.out.println("1. list all lectures");
		System.out.println("2. list all students");
		System.out.println("3. insert a lecture");
		System.out.println("4. remove a lecture");
		System.out.println("5. insert a student");
		System.out.println("6. remove a student");
		System.out.println("7. register for lecture");
		System.out.println("8. list all lectures of a student");
		System.out.println("9. list all registered students of a lecture");
		System.out.println("10. exit");
		System.out.println("===========");
	}
	
	public static void performAction() throws SQLException
	{
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		int actionNum;
		displayActionList();
		
		while (true)
		{
			System.out.print("Select your action: ");
			try
			{
				actionNum = Integer.parseInt(b.readLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Please select 1~10");
				continue;
			}
			catch (IOException e)
			{
				System.out.println("Please select 1~10");
				continue;
			}
			
			if (actionNum < 1 || actionNum > 10)
			{
				System.out.println("Please select 1~10");
				continue;
			}
			
			break;
		}
		
		switch (actionNum)
		{
		case 1:
			listAllLectures();
			break;
		case 2:
			listAllStudents();
			break;
		case 3:
			insertLecture();
			break;
		case 4:
			removeLecture();
			break;
		case 5:
			insertStudent();
			break;
		case 6:
			removeStudent();
			break;
		case 7:
			registerLecture();
			break;
		case 8:
			listAllLecturesOfStudent();
			break;
		case 9:
			listAllRegisteredStudentsOfLecture();
			break;
		case 10:
			System.out.println("\nThanks!");
			conn.close();
			System.exit(0);
		}
		
		System.out.println("");
	}
	
	public static void listAllLectures() throws SQLException
	{
		System.out.println("\n-------------------------------------------------------------------------------------");
		System.out.printf("%-8s%-25s%-10s%-15s%-20s\n", "Id", "Name", "Credit", "Capacity", "Current Applied");
		System.out.println("-------------------------------------------------------------------------------------");
		
		String sql = "SELECT * FROM LECTURE";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next())
		{
			int id = rs.getInt("id");
			String name = rs.getString("name");
			int credit = rs.getInt("credit");
			int capacity = rs.getInt("capacity");
			int currApp = 0;
			
			String sql2 = "SELECT COUNT(*) AS CURRENT_APPLIED FROM REGISTRATION WHERE LECTURE_ID = " + Integer.toString(id);
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			ResultSet rs2 = stmt2.executeQuery();
			while (rs2.next())
				currApp = rs2.getInt("current_applied");
			
			rs2.close();
			
			System.out.printf("%-8d%-25s%-10d%-15d%-20d\n", id, name, credit, capacity, currApp);
		}
		
		rs.close();
		
		System.out.println("-------------------------------------------------------------------------------------");
	}
	
	public static void listAllStudents() throws SQLException
	{
		System.out.println("\n-------------------------------------------------------------------------------------");
		System.out.printf("%-25s%-25s%-25s\n", "Id", "Name", "Used credits");
		System.out.println("-------------------------------------------------------------------------------------");
		
		String sql = "SELECT * FROM STUDENT";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next())
		{
			String id = rs.getString("id");
			String name = rs.getString("name");
			int usedCredits = 0;
			
			String sql2 = "SELECT SUM(CREDIT) AS USED_CREDITS FROM LECTURE, REGISTRATION WHERE STUDENT_ID = '" + id + "' AND LECTURE_ID = ID";
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			ResultSet rs2 = stmt2.executeQuery();
			while (rs2.next())
				usedCredits = rs2.getInt("used_credits");
			
			rs2.close();
			
			System.out.printf("%-25s%-25s%-25d\n", id, name, usedCredits);
		}
		
		rs.close();
		
		System.out.println("-------------------------------------------------------------------------------------");
	}
	
	public static void insertLecture() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		String name;
		int id = 0, credit, capacity;
		
		while (true)
		{
			try
			{
				System.out.print("Input lecture name: ");
				name = b.readLine();
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			break;
		}
		
		while (true)
		{
			try
			{
				System.out.print("Input lecture credit: ");
				credit = Integer.parseInt(b.readLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			if (credit <= 0)
			{
				System.out.println("Credit should be over 0.");
				continue;
			}
			
			break;
		}
		
		while (true)
		{
			try
			{
				System.out.print("Input lecture capacity: ");
				capacity = Integer.parseInt(b.readLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			if (capacity <= 0)
			{
				System.out.println("Capacity should be over 0.");
				continue;
			}
			
			break;
		}
		
		String sql = "SELECT MAX(ID) AS MAX_ID FROM LECTURE";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while (rs.next())
			id = rs.getInt("max_id") + 1;
		
		rs.close();
		
		String sql2 = "INSERT INTO LECTURE VALUES (?, ?, ?, ?)";
		PreparedStatement stmt2 = conn.prepareStatement(sql2);
		
		stmt2.setInt(1, id);
		stmt2.setString(2, name);
		stmt2.setInt(3, credit);
		stmt2.setInt(4, capacity);
		
		stmt2.executeUpdate();
		
		System.out.println("\nA Lecture is successfully inserted.");
	}
	
	public static void removeLecture() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		int id = 0;
		
		while (true)
		{
			try
			{
				System.out.print("Input lecture id: ");
				id = Integer.parseInt(b.readLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			String sql = "DELETE FROM LECTURE WHERE ID = " + Integer.toString(id);
			PreparedStatement stmt = conn.prepareStatement(sql);
			int cnt = stmt.executeUpdate();
			
			if (cnt == 0)
			{
				System.out.println("Lecture " + Integer.toString(id) + " doesn't exist");
				continue;
			}
			
			break;
		}
		
		System.out.println("\nA Lecture is successfully deleted.");
	}
	
	public static void insertStudent() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		String id, name;
		
		while (true)
		{
			try
			{
				System.out.print("Input student name: ");
				name = b.readLine();
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			break;
		}
		
		while (true)
		{
			try
			{
				System.out.print("Input student id: ");
				id = b.readLine();
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			if (!id.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9]$"))
			{
				System.out.println("Id should have form ¡®nnnn-nnnnn¡¯.");
				continue;
			}
			
			break;
		}
		
		String sql = "INSERT INTO STUDENT VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setString(1, id);
		stmt.setString(2, name);
		
		stmt.executeUpdate();
		
		System.out.println("\nA Student is successfully inserted.");
	}
	
	public static void removeStudent() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		String id;
		
		while (true)
		{
			try
			{
				System.out.print("Input student id: ");
				id = b.readLine();
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			if (!id.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9]$"))
			{
				System.out.println("Id should have form ¡®nnnn-nnnnn¡¯.");
				continue;
			}
			
			String sql = "DELETE FROM STUDENT WHERE ID = '" + id + "'";
			PreparedStatement stmt = conn.prepareStatement(sql);
			int cnt = stmt.executeUpdate();
			
			if (cnt == 0)
			{
				System.out.println("Student " + id + " doesn't exist");
				continue;
			}
			
			break;
		}
		
		System.out.println("\nA Student is successfully deleted.");
	}
	
	public static void registerLecture() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		String stuID;
		int lecID;
		
		while (true)
		{
			while (true)
			{
				try
				{
					System.out.print("Input student id: ");
					stuID = b.readLine();
				}
				catch (IOException e)
				{
					System.out.println("Wrong input type");
					continue;
				}
			
				if (!stuID.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9]$"))
				{
					System.out.println("Id should have form ¡®nnnn-nnnnn¡¯.");
					continue;
				}
				
				break;
			}
			
			while (true)
			{
				try
				{
					System.out.print("Input lecture id: ");
					lecID = Integer.parseInt(b.readLine());
				}
				catch (NumberFormatException e)
				{
					System.out.println("Wrong input type");
					continue;
				}
				catch (IOException e)
				{
					System.out.println("Wrong input type");
					continue;
				}
				
				break;
			}
		
			int stuCnt = 0;
			String sql = "SELECT COUNT(*) AS CNT_STU FROM STUDENT WHERE ID = '" + stuID + "'";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next())
				stuCnt = rs.getInt("cnt_stu");
			
			rs.close();
		
			int lecCnt = 0;
			String sql2 = "SELECT COUNT(*) AS CNT_LEC FROM LECTURE WHERE ID = " + Integer.toString(lecID);
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			ResultSet rs2 = stmt2.executeQuery();
		
			while (rs2.next())
				lecCnt = rs2.getInt("cnt_lec");
			
			rs2.close();
		
			if (stuCnt == 0)
				System.out.println("Student " + stuID + " doesn't exist");
		
			if (lecCnt == 0)
				System.out.println("Lecture " + Integer.toString(lecID) + " doesn't exist");
			
			if (stuCnt == 0 || lecCnt == 0)
				continue;
			
			int capacity = 0;
			String sql3 = "SELECT CAPACITY FROM LECTURE WHERE ID = " + Integer.toString(lecID);
			PreparedStatement stmt3 = conn.prepareStatement(sql3);
			ResultSet rs3 = stmt3.executeQuery();
			
			while (rs3.next())
				capacity = rs3.getInt("capacity");
			
			rs3.close();
			
			int currApp = 0;
			String sql4 = "SELECT COUNT(*) AS CURRENT_APPLIED FROM REGISTRATION WHERE LECTURE_ID = " + Integer.toString(lecID);
			PreparedStatement stmt4 = conn.prepareStatement(sql4);
			ResultSet rs4 = stmt4.executeQuery();
			while (rs4.next())
				currApp = rs4.getInt("current_applied");
			
			rs4.close();
			
			if (currApp == capacity)
				System.out.println("Capacity of a lecture is full.");
			
			int credit = 0;
			String sql5 = "SELECT CREDIT FROM LECTURE WHERE ID = " + Integer.toString(lecID);
			PreparedStatement stmt5 = conn.prepareStatement(sql5);
			ResultSet rs5 = stmt5.executeQuery();
			while (rs5.next())
				credit = rs5.getInt("credit");
			
			rs5.close();
			
			int usedCredits = 0;
			String sql6 = "SELECT SUM(CREDIT) AS USED_CREDITS FROM LECTURE, REGISTRATION WHERE STUDENT_ID = '" + stuID + "' AND LECTURE_ID = ID";
			PreparedStatement stmt6 = conn.prepareStatement(sql6);
			ResultSet rs6 = stmt6.executeQuery();
			while (rs6.next())
				usedCredits = rs6.getInt("used_credits");
			
			rs6.close();
			
			if (credit + usedCredits > 18)
				System.out.println("No remaining credits.");
			
			if (currApp == capacity || credit + usedCredits > 18)
				continue;
			
			break;
		}
		
		String sql = "INSERT INTO REGISTRATION VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setInt(1, lecID);
		stmt.setString(2, stuID);
		
		stmt.executeQuery();
		
		System.out.println("\nApplied.");
	}
	
	public static void listAllLecturesOfStudent() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		String id;
		
		while (true)
		{
			try
			{
				System.out.print("Input student id: ");
				id = b.readLine();
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			if (!id.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9]$"))
			{
				System.out.println("Id should have form ¡®nnnn-nnnnn¡¯.");
				continue;
			}
			
			break;
		}
		
		System.out.println("\n-------------------------------------------------------------------------------------");
		System.out.printf("%-8s%-25s%-10s%-15s%-20s\n", "Id", "Name", "Credit", "Capacity", "Current Applied");
		System.out.println("-------------------------------------------------------------------------------------");
		
		String sql = "SELECT ID, NAME, CREDIT, CAPACITY, (SELECT COUNT(*) AS CURRENT_APPLIED FROM REGISTRATION WHERE LECTURE_ID = ID) AS CURRENT_APPLIED FROM LECTURE, REGISTRATION WHERE ID = LECTURE_ID AND STUDENT_ID = '" + id + "'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next())
		{
			int lecID = rs.getInt("id");
			String name = rs.getString("name");
			int credit = rs.getInt("credit");
			int capacity = rs.getInt("capacity");
			int currApp = rs.getInt("current_applied");
			
			System.out.printf("%-8d%-25s%-10d%-15d%-20d\n", lecID, name, credit, capacity, currApp);
		}
		
		rs.close();
		
		System.out.println("-------------------------------------------------------------------------------------");
	}
	
	public static void listAllRegisteredStudentsOfLecture() throws SQLException
	{
		System.out.println("");
		InputStreamReader r = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(r);
		int id;
		
		while (true)
		{
			try
			{
				System.out.print("Input lecture id: ");
				id = Integer.parseInt(b.readLine());
			}
			catch (NumberFormatException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			catch (IOException e)
			{
				System.out.println("Wrong input type");
				continue;
			}
			
			break;
		}
		
		System.out.println("\n-------------------------------------------------------------------------------------");
		System.out.printf("%-25s%-50s\n", "Id", "Name");
		System.out.println("-------------------------------------------------------------------------------------");
		
		String sql = "SELECT ID, NAME FROM STUDENT, REGISTRATION WHERE STUDENT_ID = ID AND LECTURE_ID = '" + Integer.toString(id) + "'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next())
		{
			String stuID = rs.getString("id");
			String name = rs.getString("name");
			
			System.out.printf("%-25s%-50s\n", stuID, name);
		}
		
		rs.close();
		
		System.out.println("-------------------------------------------------------------------------------------");
	}
}