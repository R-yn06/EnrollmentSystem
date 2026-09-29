package com.mycompany.enrollmentsystem;

import com.formdev.flatlaf.FlatDarkLaf;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EnrollmentSystem {
 
    public static Connection con;
    public static Statement st;
    public static ResultSet rs; 
    
    
    public static String currentUser = "";
    public static String currentPassword = "";
    public static String currentRole = "";
    public static String currentSemesterDB = "";
    public static String db = "";

    public void currentDB(String dbName) {
        db = dbName;
        currentSemesterDB = dbName;
    }

  
    public boolean DBConnect() {
        String targetDB = (db != null && !db.trim().isEmpty()) ? db.trim() : "";
        String user = (currentUser != null && !currentUser.isEmpty()) ? currentUser : "root";
        String pass = (currentPassword != null) ? currentPassword : "root";
        return DBConnect(user, pass, targetDB);
    }

   
    public boolean DBConnect(String username, String password) {
        String targetDB = (db != null && !db.trim().isEmpty() && !db.equalsIgnoreCase("null")) ? db.trim() : "";
        return DBConnect(username, password, targetDB);
    }

   
    public boolean DBConnect(String username, String password, String semesterDB) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbParam = (semesterDB != null && !semesterDB.trim().isEmpty() && !semesterDB.equalsIgnoreCase("null")) 
                             ? semesterDB.trim() 
                             : "";
            
            String url = "jdbc:mysql://localhost:3306/" + dbParam + "?zeroDateTimeBehavior=CONVERT_TO_NULL";
            con = DriverManager.getConnection(url, username, password);
            st = con.createStatement();
            
           
            currentUser = username;
            currentPassword = password;
            if (!dbParam.isEmpty()) {
                currentSemesterDB = dbParam;
                db = dbParam;
            }
            
            System.out.println("Connected to database '" + dbParam + "' as user: " + username);
            return true;
        } catch (Exception ex) {
            System.err.println("Database connection failed for user " + username + " on " + semesterDB + ": " + ex.getMessage());
            return false;
        }
    }

    public static List<String> getDatabases() {
        List<String> dbList = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection tempCon = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
            Statement tempSt = tempCon.createStatement();
            ResultSet tempRs = tempSt.executeQuery("SHOW DATABASES");

            while (tempRs.next()) {
                String dbName = tempRs.getString(1);
                dbList.add(dbName);
            }

            tempRs.close();
            tempSt.close();
            tempCon.close();
        } catch (Exception e) {
            System.err.println("Failed to fetch database list: " + e.getMessage());
        }
        return dbList;
    }

 
    public boolean createDatabaseUser(String id, String name, String role, String targetDB) {
        String cleanName = name.replaceAll("\\s+", "");
        String newUsername = id + cleanName;
        String newPassword = cleanName;

        try {
            Connection rootCon = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root");
            Statement rootSt = rootCon.createStatement();
            

            rootSt.executeUpdate("CREATE USER IF NOT EXISTS '" + newUsername + "'@'localhost' IDENTIFIED BY '" + newPassword + "'");
          
            if (role.equalsIgnoreCase("Student")) {
                rootSt.executeUpdate("GRANT SELECT ON `" + targetDB + "`.* TO '" + newUsername + "'@'localhost'");
            } else if (role.equalsIgnoreCase("Teacher")) {
                rootSt.executeUpdate("GRANT SELECT, INSERT, UPDATE ON `" + targetDB + "`.* TO '" + newUsername + "'@'localhost'");
                rootSt.executeUpdate("GRANT DELETE ON `" + targetDB + "`.enroll TO '" + newUsername + "'@'localhost'");
            }
            
            rootSt.executeUpdate("FLUSH PRIVILEGES");
            rootCon.close();
            return true;
        } catch (Exception ex) {
            System.err.println("Failed to create MySQL user: " + ex.getMessage());
            return false;
        }
    }

    
    public String newdb(String term) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String schyear = "SY" + year + "_" + (year + 1);
        String dbName = term + "_" + schyear;

        db = ""; 
        if (!DBConnect("root", "root", "")) return null;
            
        try {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
            st.executeUpdate("USE `" + dbName + "`");
            currentDB(dbName);
            
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    studid INT NOT NULL AUTO_INCREMENT,
                    studname VARCHAR(100) NOT NULL,
                    studadd VARCHAR(255) NULL,
                    studcrs VARCHAR(100) NULL,
                    studgender VARCHAR(20) NULL,
                    studyrlvl VARCHAR(20) NULL,
                    PRIMARY KEY (studid));
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS subjects (
                    subjid INT NOT NULL AUTO_INCREMENT,
                    subjcode VARCHAR(50) NULL DEFAULT NULL,
                    subjdesc VARCHAR(255) NULL DEFAULT NULL,
                    subjunits INT NULL DEFAULT NULL,
                    subjsched VARCHAR(100) NULL,
                    PRIMARY KEY (subjid));
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS teachers (
                    tid INT NOT NULL AUTO_INCREMENT,
                    tname VARCHAR(100) NULL DEFAULT NULL,
                    tdept VARCHAR(100) NULL DEFAULT NULL,
                    tadd VARCHAR(255) NULL,
                    tcontact VARCHAR(50) NULL,
                    tstatus VARCHAR(50) NULL,
                    PRIMARY KEY (tid));
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS assign (
                    SubjID INT NOT NULL UNIQUE,
                    TID INT NOT NULL,
                    FOREIGN KEY (SubjID) REFERENCES subjects(subjid),
                    FOREIGN KEY (TID) REFERENCES teachers(tid));
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS enroll (
                    eid INT NOT NULL AUTO_INCREMENT,
                    studid INT NULL DEFAULT NULL,
                    subjid INT NULL DEFAULT NULL,
                    evaluation VARCHAR(255) DEFAULT NULL,
                    PRIMARY KEY (eid),
                    UNIQUE (studid, subjid),
                    FOREIGN KEY (studid) REFERENCES students(studid),
                    FOREIGN KEY (subjid) REFERENCES subjects(subjid));
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS grades (
                    gradeid INT NOT NULL AUTO_INCREMENT,
                    enroll_eid INT NOT NULL UNIQUE,
                    prelim VARCHAR(10) NULL DEFAULT NULL,
                    midterm VARCHAR(10) NULL DEFAULT NULL,
                    prefinal VARCHAR(10) NULL DEFAULT NULL,
                    final VARCHAR(10) NULL DEFAULT NULL,
                    PRIMARY KEY (gradeid),
                    FOREIGN KEY (enroll_eid) REFERENCES enroll(eid));
            """);
          
            return dbName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {      
        try {   
            System.setProperty("flatlaf.useWindowDecorations", "true");
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf Dark theme: " + ex.getMessage());
        }     
        java.awt.EventQueue.invokeLater(() -> {
            LoginForm a = new LoginForm();
            a.setVisible(true);        
        });  
    }
}