/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.enrollmentsystem;

import java.sql.ResultSet;
import java.sql.SQLException;




/**
 *
 * @author RyanP
 */

public class Students {
 
public void newstudent(String studname, String studadd,
                       String studcrs, String studgender,
                       String studyrlvl) {

    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();

    try {

       
        String checkQuery = "SELECT COUNT(*) FROM Students";
        ResultSet rs = b.st.executeQuery(checkQuery);

        int count = 0;

        if (rs.next()) {
            count = rs.getInt(1);
        }

        
        if (count == 0) {
            b.st.executeUpdate("ALTER TABLE Students AUTO_INCREMENT = 1000");
        }

        // Insert student
        String query = "INSERT INTO Students " +
                "(studname, studadd, studcrs, studgender, studyrlvl) VALUES ('" +
                studname + "', '" +
                studadd + "', '" +
                studcrs + "', '" +
                studgender + "', '" +
                studyrlvl + "')";

        int rows = b.st.executeUpdate(query);

        if (rows > 0) {
            System.out.println("Student inserted successfully!");
        }

    } catch (Exception ex) {
        System.out.println("Not successful!");
        ex.printStackTrace();
    }
}


    
    public void delete_student(int studid){
        EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    String query = "delete from students where studid ="+ studid;
    try {
            int rows = b.st.executeUpdate(query);
        }
    catch(Exception ex) {
        System.out.println("Not successful!");
        ex.printStackTrace();
    }
   
    }
    public void update_student(int studid, String studname, String studadd, String studcrs, String studgender, String studyrlvl){
    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    
    try{
            String query = "update students set studname = '" + studname 
                + "', studadd = '" + studadd 
                + "', studcrs = '" + studcrs
                + "', studyrlvl = '" + studyrlvl 
                + "' where studid = " + studid;
            int rows = b.st.executeUpdate(query);
            if (rows > 0) {
                System.out.println("Updated successfully!");
            } else {
                System.out.println("No matching student found!");
            }
        }catch (SQLException ex){
            System.out.println("not Success with sql!");
        }
    }
    }