/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.enrollmentsystem;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Grades {

  
    public void setupGradeComboBoxes(JComboBox<String> prelim, JComboBox<String> midterm, JComboBox<String> prefinal, JComboBox<String> finalCmb) {
        String[] gradeOptions = {"", "A", "B+", "B", "C+", "C", "D", "F", "FD"};
        prelim.setModel(new DefaultComboBoxModel<>(gradeOptions));
        midterm.setModel(new DefaultComboBoxModel<>(gradeOptions));
        prefinal.setModel(new DefaultComboBoxModel<>(gradeOptions));
        finalCmb.setModel(new DefaultComboBoxModel<>(gradeOptions));
    }

    /**
     * Loads assigned subjects for a specific teacher into AssignedTable.
     */
    public void showAssign(JTable assignedTable, int teachid) {
        DefaultTableModel tblmodel = (DefaultTableModel) assignedTable.getModel();
        tblmodel.setRowCount(0);

        EnrollmentSystem b = new EnrollmentSystem();
        b.DBConnect();

        try {
            String query = "SELECT s.subjid, s.subjcode, s.subjdesc, s.subjunits, s.subjsched, COUNT(e.studid) AS Students "
                         + "FROM subjects s "
                         + "JOIN assign a ON s.subjid = a.subjid "
                         + "LEFT JOIN enroll e ON s.subjid = e.subjid "
                         + "WHERE a.tid = ? "
                         + "GROUP BY s.subjid, s.subjcode, s.subjdesc, s.subjunits, s.subjsched";

            PreparedStatement pstmt = EnrollmentSystem.con.prepareStatement(query);
            pstmt.setInt(1, teachid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String i = rs.getString("subjid");
                String c = rs.getString("subjcode");
                String d = rs.getString("subjdesc");
                int t = rs.getInt("subjunits");
                String g = rs.getString("subjsched");
                int f = rs.getInt("Students");

                Object[] items = {i, c, d, t, g, f};
                tblmodel.addRow(items);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

 
    public void loadEnrolledStudentsAndGrades(JTable gradesTable, String subjid) {
        DefaultTableModel tblmodel = (DefaultTableModel) gradesTable.getModel();
        tblmodel.setRowCount(0);

        EnrollmentSystem sys = new EnrollmentSystem();
        sys.DBConnect();

        try {
            String query = "SELECT st.studid, st.studname, g.prelim, g.midterm, g.prefinal, g.final "
                         + "FROM enroll e "
                         + "INNER JOIN students st ON e.studid = st.studid "
                         + "LEFT JOIN grades g ON e.eid = g.enroll_eid "
                         + "WHERE e.subjid = ?";

            PreparedStatement pstmt = EnrollmentSystem.con.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(subjid));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("studid");
                String name = rs.getString("studname");
                String prelim = rs.getString("prelim") != null ? rs.getString("prelim") : "";
                String midterm = rs.getString("midterm") != null ? rs.getString("midterm") : "";
                String prefinal = rs.getString("prefinal") != null ? rs.getString("prefinal") : "";
                String fin = rs.getString("final") != null ? rs.getString("final") : "";

                tblmodel.addRow(new Object[]{id, name, prelim, midterm, prefinal, fin});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    public void saveGrades(Component parent, String sid, String currentSubjId, String prelim, String midterm, String prefinal, String fin, JTable gradesTable) {
        if (sid.isEmpty() || sid.equals("StudID") || currentSubjId.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please select a student from the table first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EnrollmentSystem sys = new EnrollmentSystem();
        sys.DBConnect();

        try {
            String getEidSql = "SELECT eid FROM enroll WHERE studid = ? AND subjid = ?";
            PreparedStatement pstmtEid = EnrollmentSystem.con.prepareStatement(getEidSql);
            pstmtEid.setInt(1, Integer.parseInt(sid));
            pstmtEid.setInt(2, Integer.parseInt(currentSubjId));
            ResultSet rsEid = pstmtEid.executeQuery();

            if (rsEid.next()) {
                int eid = rsEid.getInt("eid");

                String upsertSql = "INSERT INTO grades (enroll_eid, prelim, midterm, prefinal, final) "
                                 + "VALUES (?, ?, ?, ?, ?) "
                                 + "ON DUPLICATE KEY UPDATE prelim=?, midterm=?, prefinal=?, final=?";

                PreparedStatement pstmt = EnrollmentSystem.con.prepareStatement(upsertSql);
                pstmt.setInt(1, eid);
                pstmt.setString(2, prelim);
                pstmt.setString(3, midterm);
                pstmt.setString(4, prefinal);
                pstmt.setString(5, fin);

                pstmt.setString(6, prelim);
                pstmt.setString(7, midterm);
                pstmt.setString(8, prefinal);
                pstmt.setString(9, fin);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(parent, "Grades updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrolledStudentsAndGrades(gradesTable, currentSubjId);
            } else {
                JOptionPane.showMessageDialog(parent, "Student enrollment record not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    public void setComboValue(JComboBox<String> cmb, Object val) {
        if (val != null && !val.toString().isEmpty()) {
            cmb.setSelectedItem(val.toString());
        } else {
            cmb.setSelectedIndex(0);
        }
    }

   
    public void logout(JFrame currentFrame) {
        int confirm = JOptionPane.showConfirmDialog(
            currentFrame, 
            "Are you sure you want to log out?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            EnrollmentSystem.currentUser = "";
            EnrollmentSystem.currentPassword = "";
            EnrollmentSystem.currentRole = "";
            EnrollmentSystem.currentSemesterDB = "";
            EnrollmentSystem.db = "";

            try {
                if (EnrollmentSystem.con != null && !EnrollmentSystem.con.isClosed()) {
                    EnrollmentSystem.con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            currentFrame.dispose();
        }
    }
}