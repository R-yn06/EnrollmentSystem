/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.enrollmentsystem;

/**
 *
 * @author rcaraos
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EnrollmentSystem {
        Connection con;
        Statement st;
       static ResultSet rs;   
    public static void main(String[] args) {
        //CLASS  VARIABLENAME;        
    StudentsForm a = new StudentsForm();
        a.setVisible(true);  
         a.showrecords();
        
    }
    public boolean DBConnect(){    
      try{
       Class.forName("com.mysql.jdbc.Driver");
       con = DriverManager.getConnection("jdbc:mysql://localhost:3306/enrollmentsystem?zeroDateTimeBehavior=CONVERT_TO_NULL","root","root");
       st = con.createStatement();  
       System.out.println("Connected to database:");
    }catch (Exception ex) {
      System.out.print(ex);   
      return false;
   }    
        return true;
    }

}
    

