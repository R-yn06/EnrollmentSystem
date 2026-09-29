package com.mycompany.enrollmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author rcaraos
 */
public class Teachers {

    public void newteacher(String tname, String tdept, String tadd, String tcontact, String tstatus) {

        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) {
            System.out.println("Database connection failed!");
            return;
        }

        try {
            // Check if Teachers table is empty
            String checkQuery = "SELECT COUNT(*) FROM teachers";
            ResultSet rs = b.st.executeQuery(checkQuery);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            // If table is empty, start ID at 3000
            if (count == 0) {
                b.st.executeUpdate("ALTER TABLE teachers AUTO_INCREMENT = 3000");
            }

            // Insert teacher using PreparedStatement to safely retrieve auto-generated tid
            String insertQuery = "INSERT INTO teachers (tname, tdept, tadd, tcontact, tstatus) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = b.con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, tname);
            pstmt.setString(2, tdept);
            pstmt.setString(3, tadd);
            pstmt.setString(4, tcontact);
            pstmt.setString(5, tstatus);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // Retrieve the generated teacher ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newid = generatedKeys.getInt(1);

                    // Sanitize teacher name (remove spaces and make lowercase) for MySQL username
                    String cleanName = tname.replaceAll("\\s+", "").toLowerCase();
                    String username = newid + cleanName; 
                    String password = cleanName;         
                    String dbName = EnrollmentSystem.db;

                    // Connect briefly as root to create the user and grant specific privileges
                    try (Connection rootCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
                         Statement rootSt = rootCon.createStatement()) {
                        
                        // SQL 1: Create MySQL user
                        String query1 = "CREATE USER IF NOT EXISTS '" + username + "'@'localhost' IDENTIFIED BY '" + password + "'";
                        
                        // SQL 2: Grant SELECT, INSERT, UPDATE permissions (No DELETE) on active database
                        String query2 = "GRANT SELECT, INSERT, UPDATE ON `" + dbName + "`.* TO '" + username + "'@'localhost'";
                        
                        // Execute user creation and grant commands
                        rootSt.executeUpdate(query1);
                        rootSt.executeUpdate(query2);
                        rootSt.executeUpdate("FLUSH PRIVILEGES");

                        System.out.println("Teacher inserted successfully!");
                        System.out.println("User '" + username + "' created with SELECT, INSERT, and UPDATE access on database `" + dbName + "`.");
                    } catch (SQLException rootEx) {
                        System.err.println("Teacher added to table, but failed to create MySQL user account: " + rootEx.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Not successful! (Teacher)");
            ex.printStackTrace();
        }
    }

    public void delete_teacher(int tid) {
        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) return;

        try {
            
            String fetchNameQuery = "SELECT tname FROM teachers WHERE tid = " + tid;
            ResultSet rs = b.st.executeQuery(fetchNameQuery);
            
            String tname = "";
            if (rs.next()) {
                tname = rs.getString("tname");
            }
            
           
            String query = "DELETE FROM teachers WHERE tid = " + tid;
            int rows = b.st.executeUpdate(query);
            
            if (rows > 0) {
                System.out.println("Teacher deleted successfully from table!");
                
               
                if (!tname.isEmpty()) {
                    String cleanName = tname.replaceAll("\\s+", "").toLowerCase();
                    String username = tid + cleanName;
                    String dbName = EnrollmentSystem.db;
                    
                    try (Connection rootCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
                         Statement rootSt = rootCon.createStatement()) {
                        
                        // Revoke all privileges 
                        String revokeQuery = "REVOKE ALL PRIVILEGES ON `" + dbName + "`.* FROM '" + username + "'@'localhost'";
                        
                        rootSt.executeUpdate(revokeQuery);
                        rootSt.executeUpdate("FLUSH PRIVILEGES");
                        
                        System.out.println("Access revoked for user '" + username + "' on database `" + dbName + "`.");
                    } catch (SQLException rootEx) {
                        System.err.println("Teacher deleted from table, but failed to revoke privileges: " + rootEx.getMessage());
                    }
                }
            } else {
                System.out.println("No teacher found with ID: " + tid);
            }
        } catch (Exception ex) {
            System.out.println("Delete operation failed! (Teacher)");
            ex.printStackTrace();
        }
    }

    public void update_Teacher(int tid, String tname, String tdept, String tadd, String tcontact, String tstatus) {
        EnrollmentSystem b = new EnrollmentSystem();
        if (!b.DBConnect()) return;

      
        String query = "UPDATE teachers SET tname = ?, tdept = ?, tadd = ?, tcontact = ?, tstatus = ? WHERE tid = ?";
        try {
            PreparedStatement ps = b.con.prepareStatement(query);
            ps.setString(1, tname);
            ps.setString(2, tdept);
            ps.setString(3, tadd);
            ps.setString(4, tcontact);
            ps.setString(5, tstatus);
            ps.setInt(6, tid);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Teacher updated successfully!");
            } else {
                System.out.println("No matching teacher found!");
            }
        } catch (Exception ex) {
            System.out.println("Not successful!");
            ex.printStackTrace();
        }
    }
}