package com.mycompany.enrollmentsystem;




public class Assign extends EnrollmentSystem {
    static int subjid;

    public void setsubjid(int a) {
        subjid = a;
    }

    public int getsubjid() {
        return subjid;
    }

    public String assignTchr(int teachid) {
        DBConnect();

        try {
            try (java.sql.CallableStatement checkSchedule =
                    con.prepareCall("{CALL check_teacher_schedule(?, ?)}")) {
                checkSchedule.setInt(1, teachid);
                checkSchedule.setInt(2, subjid);
                if (checkSchedule.execute()) {
                    try (java.sql.ResultSet conflict = checkSchedule.getResultSet()) {
                        if (conflict.next()) {
                            return "Conflict with " + conflict.getString("conflict_schedule");
                        }
                    }
                }
            }

            String query = "INSERT INTO assign(TID, SubJID) VALUES(?, ?)";
            java.sql.PreparedStatement assignment = con.prepareStatement(query);
            assignment.setInt(1, teachid);
            assignment.setInt(2, subjid);
            assignment.executeUpdate();
            assignment.close();
            return "Teacher " + teachid + " assigned to subject " + subjid;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return "This subject is already assigned to a teacher.";
        } catch (java.sql.SQLException e) {
            String message = e.getMessage();
            if (message != null) {
                int conflictStart = message.indexOf("Conflict with ");
                if (conflictStart >= 0) {
                    return message.substring(conflictStart).trim();
                }
            }
            System.out.println("Failed to assign teacher: " + e);
            return "Assignment Failed";
        } catch (Exception e) {
            System.out.println("Failed to assign teacher: " + e);
            return "Assignment Failed";
        }
    }

    public String deleteSubject(int teachid) {
        DBConnect();

        String query = "DELETE FROM assign WHERE TID = " + teachid + " AND SubjID = " + subjid;

        System.out.println("teachid = " + teachid);
        System.out.println("subjid = " + subjid);
        System.out.println("query = " + query);

        try {
            int rows = st.executeUpdate(query);
            System.out.println("rows deleted = " + rows);

            if (rows > 0) {
                return "Subject " + subjid + " unassigned from teacher " + teachid;
            } else {
                return "Teacher is not assigned to this subject.";
            }
        } catch (Exception e) {
            System.out.println("Failed to drop subject: " + e);
            return "Drop Failed";
        }
    }
}