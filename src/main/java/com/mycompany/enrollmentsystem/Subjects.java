package com.mycompany.enrollmentsystem;

import java.sql.ResultSet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rcaraos
 */
public class Subjects {
     
    
    public void newsubject(String subjcode, String subjdesc, int subjunits, String subjsched) {

    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();

    try {

        
        String checkQuery = "SELECT COUNT(*) FROM Subjects";
        ResultSet rs = b.st.executeQuery(checkQuery);

        int count = 0;

        if (rs.next()) {
            count = rs.getInt(1);
        }

        
        if (count == 0) {
            b.st.executeUpdate(
                "ALTER TABLE Subjects AUTO_INCREMENT = 2000"
            );
        }

        
        String query = "INSERT INTO Subjects " +
                "(subjcode, subjdesc, subjunits, subjsched) VALUES ('" +
                subjcode + "', '" +
                subjdesc + "', " +
                subjunits + ", '" +
                subjsched + "')";

        int rows = b.st.executeUpdate(query);

        if (rows > 0) {
            System.out.println("Subject Added successfully!");
        }

    } catch (Exception ex) {
        System.out.println("Not successful! (SUBJECT)");
        ex.printStackTrace();
    }
}


    
    public void delete_subject(int subjid){
        EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    String query = "delete from subjects where subjid ="+ subjid;
    try {
            int rows = b.st.executeUpdate(query);
        }
    catch(Exception ex) {
        System.out.println("Not successful! (Subject)");
        ex.printStackTrace();
    }
   
    }
    public void update_subject(int subjid, String subjcode, String subjdesc, int subjunits, String subjsched){
    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
   
    try {
         String query = "UPDATE subjects SET subjcode = ?, subjdesc = ?, subjunits = ?, "
                 + "subjsched = ? WHERE subjid = ?";
        
        java.sql.PreparedStatement ps = b.con.prepareStatement(query);
        ps.setString(1, subjcode);
        ps.setString(2, subjdesc);
        ps.setInt(3, subjunits);
        ps.setString(4, subjsched);
        ps.setInt(5, subjid);
        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Subject updated successfully!");
        }
    } catch(Exception ex) {
        System.out.println("Not successful!");
        ex.printStackTrace();
    }
}
}