package com.mycompany.enrollmentsystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rcaraos
 */
public class Subjects {
     
 public void newsubject(int subjid, String subjcode, String subjdesc, int subjunits, String subjsched) {

    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();

    try {
        String query = "INSERT INTO subjects VALUES (" +
                subjid + ", '" +
                subjcode + "', '" +
                subjdesc + "', '" +
                subjunits + "', '" +
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
    String query = "UPDATE subjects SET subjcode = ?, subjdesc = ?, subjunits = ?, "
                 + "subjsched = ? WHERE subjid = ?";
    try {
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