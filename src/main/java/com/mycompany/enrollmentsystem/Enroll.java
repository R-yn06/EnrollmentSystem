/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.enrollmentsystem;

/**
 *
 * @author RyanP
 */
public class Enroll extends EnrollmentSystem {
    
    static int subjid;
    public void setsubjid(int a){
        subjid = a;
    }
    public int getsubjid(){
        return subjid;
    }
    
      
    public String enrollStud(int studid){
        DBConnect();
        int neweid = 0;
        String query1 = "select max(eid) + 1 as maxId from enroll";
        try {
            rs = st.executeQuery(query1);
            if (rs.next() && rs.getInt("maxId") > 0) {
                neweid = rs.getInt("maxId");
            } else {
                neweid = 1;
            }
        } catch (Exception e) {
            System.out.println("Failed to get enrollment ID " + e);
        }

        
        
        String enrollQuery = "insert into enroll(studid, subjid,evaluation)" + "values('"+studid+"','"+subjid+"',' ')";
        try {
            st.executeUpdate(enrollQuery);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return "Student is already enrolled in this subject.";
        } catch (Exception e) {
            System.out.println("Failed to insert" + e);
            return "Enrollment Failed";
        }
        return "Student "+ studid + " enrolled to " + subjid;               
    }
    
    
    
    public String dropSubject(int studid){
        DBConnect();
        
        String query = "delete from enroll " + "where studid = " + studid + " and subjid = " + subjid;
        
        System.out.println("studid = " + studid);
System.out.println("subjid = " + subjid);
System.out.println("query = " + query);
        try {
            int rows = st.executeUpdate(query);
            System.out.println("rows deleted = " + rows);

            if(rows>0){
                return "Subject "+ subjid + " dropped from student " +studid;
            }else{
                return "Student is not enrolled in this subject.";
            }
            
        } catch (Exception e) {
            System.out.println("Failed to drop subject "+ e);
            return "Drop Failed";
        }
    }
    
    
}

