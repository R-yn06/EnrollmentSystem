package com.mycompany.enrollmentsystem;




public class Assign extends EnrollmentSystem {
    static int subjid;

    public void setsubjid(int a) {
        subjid = a;
    }

    public int getsubjid() {
        return subjid;
    }

    public String assignTchr(int tchrid) {
        DBConnect();

        // Directly insert TID and SubjID since SubjID is the primary key
        String query = "INSERT INTO assign(TID, SubjID) VALUES(" + tchrid + ", " + subjid + ")";

        try {
            st.executeUpdate(query);
            return "Teacher " + tchrid + " assigned to subject " + subjid;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return "This subject is already assigned to a teacher.";
        } catch (Exception e) {
            System.out.println("Failed to assign teacher: " + e);
            return "Assignment Failed";
        }
    }

    public String deleteSubject(int tchrid) {
        DBConnect();

        String query = "DELETE FROM assign WHERE TID = " + tchrid + " AND SubjID = " + subjid;

        System.out.println("tchrid = " + tchrid);
        System.out.println("subjid = " + subjid);
        System.out.println("query = " + query);

        try {
            int rows = st.executeUpdate(query);
            System.out.println("rows deleted = " + rows);

            if (rows > 0) {
                return "Subject " + subjid + " unassigned from teacher " + tchrid;
            } else {
                return "Teacher is not assigned to this subject.";
            }
        } catch (Exception e) {
            System.out.println("Failed to drop subject: " + e);
            return "Drop Failed";
        }
    }
}