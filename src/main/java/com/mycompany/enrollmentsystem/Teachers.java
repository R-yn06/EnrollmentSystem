package com.mycompany.enrollmentsystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rcaraos
 */
public class Teachers {
     
 public void newteacher(int tid, String tname, String tdept, String tcontact) {

    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();

    try {
        String query = "INSERT INTO Teachers VALUES (" +
                tid + ", '" +
                tname + "', '" +
                tdept + "', '" +
                tcontact + "')";

        int rows = b.st.executeUpdate(query);

        if (rows > 0) {
            System.out.println("Teacher Added successfully!");
        }

    } catch (Exception ex) {
        System.out.println("Not successful! (Teacher)");
        ex.printStackTrace();
    }
}
    
    public void delete_teacher(int tid){
        EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    String query = "delete from Teachers where tid ="+ tid;
    try {
            int rows = b.st.executeUpdate(query);
        }
    catch(Exception ex) {
        System.out.println("Not successful! (Teacher)");
        ex.printStackTrace();
    }
   
    }
    public void update_Teacher(int tid, String tname, String tdept,  String tcontact){
    EnrollmentSystem b = new EnrollmentSystem();
    b.DBConnect();
    String query = "UPDATE teachers SET tname = ?, tdept = ?, "
                 + "tcontact = ? WHERE tid = ?";
    try {
        java.sql.PreparedStatement ps = b.con.prepareStatement(query);
        ps.setString(1, tname);
        ps.setString(2, tdept);
        ps.setString(3, tcontact);
        ps.setInt(4, tid);
        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Teacher updated successfully!");
        }
    } catch(Exception ex) {
        System.out.println("Not successful!");
        ex.printStackTrace();
    }
}
}