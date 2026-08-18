/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.enrollmentsystem;




/**
 *
 * @author RyanP
 */

public class Students {
 
 public void newstudent(int studid, String studname, String studadd,
                       String studcrs, String studgender, String studyrlvl) {

    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();

    try {
        String query = "INSERT INTO Students VALUES (" +
                studid + ", '" +
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
    public void update_student(int studid, String studname, String studadd,
                   String studcrs, String studgender, String studyrlvl){
    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    String query = "UPDATE students SET studname = ?, studadd = ?, studcrs = ?, "
                 + "studgender = ?, studyrlvl = ? WHERE studid = ?";
    try {
        java.sql.PreparedStatement ps = b.con.prepareStatement(query);
        ps.setString(1, studname);
        ps.setString(2, studadd);
        ps.setString(3, studcrs);
        ps.setString(4, studgender);
        ps.setString(5, studyrlvl);
        ps.setInt(6, studid);
        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Student updated successfully!");
        }
    } catch(Exception ex) {
        System.out.println("Not successful!");
        ex.printStackTrace();
    }
}
}