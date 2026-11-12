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

        try (java.sql.CallableStatement checkSchedule =
                con.prepareCall("{CALL check_student_schedule(?, ?)}")) {
            checkSchedule.setInt(1, studid);
            checkSchedule.setInt(2, subjid);
            if (checkSchedule.execute()) {
                try (java.sql.ResultSet conflict = checkSchedule.getResultSet()) {
                    if (conflict.next()) {
                        return "Conflict with " + conflict.getString("conflict_schedule");
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            String message = e.getMessage();
            if (message != null) {
                int conflictStart = message.indexOf("Conflict with ");
                if (conflictStart >= 0) {
                    return message.substring(conflictStart).trim();
                }
            }
            System.out.println("Failed to check student schedule: " + e);
            return "Enrollment Failed";
        }

        try {
            String enrollQuery = "insert into enroll(studid, subjid,evaluation) values(?,?,?)";
            java.sql.PreparedStatement enrollStatement = con.prepareStatement(enrollQuery);
            enrollStatement.setInt(1, studid);
            enrollStatement.setInt(2, subjid);
            enrollStatement.setString(3, " ");
            enrollStatement.executeUpdate();
            enrollStatement.close();
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
