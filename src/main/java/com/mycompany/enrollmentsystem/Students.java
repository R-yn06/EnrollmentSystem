package com.mycompany.enrollmentsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author RyanP
 */
public class Students {

    public void newstudent(String studname, String studadd,
                           String studcrs, String studgender,
                           String studyrlvl) {

        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) {
            System.out.println("Database connection failed!");
            return;
        }

        try {
            // Check if students table is empty to reset AUTO_INCREMENT to 1000
            String checkQuery = "SELECT COUNT(*) FROM students";
            ResultSet rs = b.st.executeQuery(checkQuery);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                b.st.executeUpdate("ALTER TABLE students AUTO_INCREMENT = 1000");
            }

            // Insert student using PreparedStatement to safely retrieve auto-generated studid
            String insertQuery = "INSERT INTO students (studname, studadd, studcrs, studgender, studyrlvl) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = b.con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, studname);
            pstmt.setString(2, studadd);
            pstmt.setString(3, studcrs);
            pstmt.setString(4, studgender);
            pstmt.setString(5, studyrlvl);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Student inserted successfully!");

                // Retrieve the generated student ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newid = generatedKeys.getInt(1);
                    
                    // Sanitize student name (remove spaces) for MySQL username
                    String cleanName = studname.replaceAll("\\s+", "");
                    String username = newid + cleanName;
                    String dbName = EnrollmentSystem.db;

                    // SQL 1: Create MySQL user
                    String query1 = "CREATE USER IF NOT EXISTS '" + username + "'@'localhost' IDENTIFIED BY '" + cleanName + "'";
                    
                    // SQL 2: Grant SELECT permission on active database
                    String query2 = "GRANT SELECT ON `" + dbName + "`.* TO '" + username + "'@'localhost'";
                    
                    // Execute user creation and grant commands
                    b.st.executeUpdate(query1);
                    b.st.executeUpdate(query2);
                    b.st.executeUpdate("FLUSH PRIVILEGES");

                    System.out.println("User '" + username + "' created and granted SELECT access on database `" + dbName + "`.");
                }
            }

        } catch (Exception ex) {
            System.out.println("Failed to add student or create user!");
            ex.printStackTrace();
        }
    }

    public void delete_student(int studid) {
        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) return;

        String query = "DELETE FROM students WHERE studid = " + studid;
        try {
            int rows = b.st.executeUpdate(query);
            if (rows > 0) {
                System.out.println("Deleted successfully!");
            } else {
                System.out.println("No student found with ID: " + studid);
            }
        } catch (Exception ex) {
            System.out.println("Delete operation failed!");
            ex.printStackTrace();
        }
    }

    public void update_student(int studid, String studname, String studadd, String studcrs, String studgender, String studyrlvl) {
        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) return;

        try {
            String query = "UPDATE students SET " +
                           "studname = '" + studname + "', " +
                           "studadd = '" + studadd + "', " +
                           "studcrs = '" + studcrs + "', " +
                           "studgender = '" + studgender + "', " +
                           "studyrlvl = '" + studyrlvl + "' " +
                           "WHERE studid = " + studid;

            int rows = b.st.executeUpdate(query);
            if (rows > 0) {
                System.out.println("Updated successfully!");
            } else {
                System.out.println("No matching student found!");
            }
        } catch (SQLException ex) {
            System.out.println("SQL Exception during update!");
            ex.printStackTrace();
        }
    }
}