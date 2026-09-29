package com.mycompany.enrollmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
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

          
            String insertQuery = "INSERT INTO students (studname, studadd, studcrs, studgender, studyrlvl) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = b.con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, studname);
            pstmt.setString(2, studadd);
            pstmt.setString(3, studcrs);
            pstmt.setString(4, studgender);
            pstmt.setString(5, studyrlvl);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
              
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newid = generatedKeys.getInt(1);
                    
                   
                    String cleanName = studname.replaceAll("\\s+", "").toLowerCase();
                    String username = newid + cleanName; 
                    String password = cleanName;       
                    String dbName = EnrollmentSystem.db;

                   
                    try (Connection rootCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
                         Statement rootSt = rootCon.createStatement()) {
                        
                      
                        String query1 = "CREATE USER IF NOT EXISTS '" + username + "'@'localhost' IDENTIFIED BY '" + password + "'";
                        
                      
                        String query2 = "GRANT SELECT ON `" + dbName + "`.* TO '" + username + "'@'localhost'";
                        
             
                        rootSt.executeUpdate(query1);
                        rootSt.executeUpdate(query2);
                        rootSt.executeUpdate("FLUSH PRIVILEGES");

                        System.out.println("Student inserted successfully!");
                        System.out.println("User '" + username + "' created and granted SELECT access on database `" + dbName + "`.");
                    } catch (SQLException rootEx) {
                        System.err.println("Student added to table, but failed to create MySQL user account: " + rootEx.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Failed to add student!");
            ex.printStackTrace();
        }
    }

     public void delete_student(int studid) {
        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) return;

        try {
            
            String fetchNameQuery = "SELECT studname FROM students WHERE studid = " + studid;
            ResultSet rs = b.st.executeQuery(fetchNameQuery);
            
            String studname = "";
            if (rs.next()) {
                studname = rs.getString("studname");
            }
            
            
            String query = "DELETE FROM students WHERE studid = " + studid;
            int rows = b.st.executeUpdate(query);
            
            if (rows > 0) {
                System.out.println("Deleted successfully from table!");
                
                
                if (!studname.isEmpty()) {
                    String cleanName = studname.replaceAll("\\s+", "").toLowerCase();
                    String username = studid + cleanName;
                    String dbName = EnrollmentSystem.db;
                    
                    try (Connection rootCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
                         Statement rootSt = rootCon.createStatement()) {
                        
                        // Revoke privileges
                        String revokeQuery = "REVOKE ALL PRIVILEGES ON `" + dbName + "`.* FROM '" + username + "'@'localhost'";
                        
                        rootSt.executeUpdate(revokeQuery);
                        rootSt.executeUpdate("FLUSH PRIVILEGES");
                        
                        System.out.println("Access revoked for user '" + username + "' on database `" + dbName + "`.");
                    } catch (SQLException rootEx) {
                        System.err.println("Student deleted from table, but failed to revoke privileges: " + rootEx.getMessage());
                    }
                }
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