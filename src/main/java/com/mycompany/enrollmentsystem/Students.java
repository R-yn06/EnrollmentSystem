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
    
//    public void deleteStudent(){
//            EnrollmentSystem b = new EnrollmentSystem();
//    
//            b.DBConnect();
//
//    try {
//        String query = "DELETE FROM Students WHERE studid like '%" + studname + "%'";
//
//        int rows = b.st.executeUpdate(query);
//
//        if (rows > 0) {
//            System.out.println("Student deleted successfully!");
//        } else {
//            System.out.println("Student ID not found!");
//        }
//
//    } catch (Exception ex) {
//        System.out.println("Delete failed!");
//        ex.printStackTrace();
//    }
//}
//    
//    public void updateStudent(){
//        
//    }
}