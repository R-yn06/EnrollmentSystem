package com.mycompany.enrollmentsystem;

import com.formdev.flatlaf.FlatDarkLaf;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class EnrollmentSystem {
    Connection con;
    Statement st;
    static ResultSet rs; 
    
    static String db;

    public void currentDB(String db){
        this.db = db;
    }
    
    public String newdb(String term){
        DBConnect();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String schyear = "SY" + year + "_" + (year + 1);
            
        try{
            String query = "CREATE DATABASE IF NOT EXIST" + term + "_" + schyear;
            st.executeUpdate(query);
            String query2 = "USE " + term + "_" + schyear;
            st.executeUpdate(query2);
            
            String query3 = """
                            CREATE TABLE IF NOT EXISTS students (
                                studid INT NOT NULL AUTO_INCREMENT,
                                studname VARCHAR(100) NOT NULL,
                                studadd VARCHAR(255) NULL,
                                studcrs VARCHAR(100) NULL,
                                studgender VARCHAR(20) NULL,
                                studyrlvl VARCHAR(20) NULL,
                                PRIMARY KEY (studid));
                                """;
            st.executeUpdate(query3);
            String query4 = """
                            CREATE TABLE IF NOT EXISTS subjects (
                                    subjid INT NOT NULL AUTO_INCREMENT,
                                    subjcode VARCHAR(50) NULL DEFAULT NULL,
                                    subjdesc VARCHAR(255) NULL DEFAULT NULL,
                                    subjunits INT NULL DEFAULT NULL,
                                    subjsched VARCHAR(100) NULL,
                                    PRIMARY KEY (subjid));
                            """;
            st.executeUpdate(query4);
            String query5 = """
                           CREATE TABLE IF NOT EXISTS teachers (
                                    tid INT NOT NULL AUTO_INCREMENT,
                                    tname VARCHAR(100) NULL DEFAULT NULL,
                                    tdept VARCHAR(100) NULL DEFAULT NULL,
                                    tadd VARCHAR(255) NULL,
                                    tcontact VARCHAR(50) NULL,
                                    tstatus VARCHAR(50) NULL,
                                    PRIMARY KEY (tid)
                                );
                            """;
            st.executeUpdate(query5);
            String query6 = """
                            CREATE TABLE IF NOT EXISTS assign (
                                SubjID INT NOT NULL UNIQUE,
                                TID INT NOT NULL,
                                FOREIGN KEY (SubjID) REFERENCES subjects(subjid),
                                FOREIGN KEY (TID) REFERENCES teachers(tid)
                            );
                            """;
            st.executeUpdate(query6);
            String query7 = """
                            CREATE TABLE IF NOT EXISTS enroll (
                                eid INT NOT NULL AUTO_INCREMENT,
                                studid INT NULL DEFAULT NULL,
                                subjid INT NULL DEFAULT NULL,
                                evaluation VARCHAR(255) DEFAULT NULL,
                                PRIMARY KEY (eid),
                                UNIQUE (studid, subjid),
                                FOREIGN KEY (studid) REFERENCES students(studid),
                                FOREIGN KEY (subjid) REFERENCES subjects(subjid)
                            );
                            """;
            st.executeUpdate(query7);
            String query8 = """
                            CREATE TABLE IF NOT EXISTS grades (
                                gradeid INT NOT NULL AUTO_INCREMENT,
                                enroll_eid INT NOT NULL UNIQUE,
                                prelim VARCHAR(10) NULL DEFAULT NULL,
                                midterm VARCHAR(10) NULL DEFAULT NULL,
                                prefinal VARCHAR(10) NULL DEFAULT NULL,
                                final VARCHAR(10) NULL DEFAULT NULL,
                                PRIMARY KEY (gradeid),
                                FOREIGN KEY (enroll_eid) REFERENCES enroll(eid)
                            );
                            """;
            st.executeUpdate(query8);
          
        } catch(Exception ex){
            System.out.println(ex);
        }
        return term + "_" + schyear;
    }
    
    public boolean DBConnect(){
       try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Automatically connecting using root/root admin access
            String dbParam = (db == null || db.isEmpty()) ? "" : db;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbParam + "?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "root"); 

            st = con.createStatement();  
            System.out.println("Connected to database!");

        } catch (Exception ex) {
            System.out.print(ex);  
            System.out.println("Connection failed");
            return false;
        }
        return true;
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