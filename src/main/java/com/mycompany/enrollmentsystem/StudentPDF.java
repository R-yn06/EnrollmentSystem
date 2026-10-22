package com.mycompany.enrollmentsystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rcaraos
 */

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentPDF {

    public static void generateAndOpenPDF(String studId, String schoolYear) {
        Document doc = new Document();
        PdfWriter docWriter = null;

        Font bfBold14 = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Font bfBold12 = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font bf12 = new Font(FontFamily.TIMES_ROMAN, 12);
        Font bf10 = new Font(FontFamily.TIMES_ROMAN, 10);

        String fileName = "Student_Grade_Sheet_" + studId + ".pdf";
        File pdfFile = new File(System.getProperty("user.home"), fileName);

        try {
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
            doc.open();

            // 1. Header Table (Logo / Title Text)
            float[] headerWidths = {1.5f, 5f};
            PdfPTable headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(80f);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cellText = new PdfPCell(new Phrase("International State College \nof The Philippinnes", bfBold12));
            cellText.setBorder(Rectangle.NO_BORDER);
            cellText.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellText.setHorizontalAlignment(Element.ALIGN_LEFT);
            
            // Empty cell placeholder for logo alignment
            PdfPCell cellLogo = new PdfPCell(new Phrase("", bf12));
            cellLogo.setBorder(Rectangle.NO_BORDER);

            headerTable.addCell(cellLogo);
            headerTable.addCell(cellText);
            doc.add(headerTable);

            doc.add(new Paragraph("\n"));

            // 2. Title: Student Grade Sheet
            Paragraph title = new Paragraph("Student Grade Sheet", bfBold14);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            // 3. Fetch Student Information
            String studName = "";
            String studCourse = "";
            String studYear = "";

            String studentSql = "SELECT studname, studcrs, studyrlvl FROM students WHERE studid = ?";
            PreparedStatement pstmtStud = EnrollmentSystem.con.prepareStatement(studentSql);
            pstmtStud.setInt(1, Integer.parseInt(studId));
            ResultSet rsStud = pstmtStud.executeQuery();

            if (rsStud.next()) {
                studName = rsStud.getString("studname") != null ? rsStud.getString("studname") : "";
                studCourse = rsStud.getString("studcrs") != null ? rsStud.getString("studcrs") : "";
                studYear = rsStud.getString("studyrlvl") != null ? rsStud.getString("studyrlvl") : "";
            }

            // 4. Student Metadata Section (2-Column Grid)
            float[] infoWidths = {5f, 5f};
            PdfPTable infoTable = new PdfPTable(infoWidths);
            infoTable.setWidthPercentage(90f);

            addNoBorderCell(infoTable, "Student ID: " + studId, bfBold12, Element.ALIGN_LEFT);
            addNoBorderCell(infoTable, "School Year: " + schoolYear, bfBold12, Element.ALIGN_LEFT);

            addNoBorderCell(infoTable, "Student Name: " + studName, bfBold12, Element.ALIGN_LEFT);
            addNoBorderCell(infoTable, "Student Course: " + studCourse, bfBold12, Element.ALIGN_LEFT);

            addNoBorderCell(infoTable, "Student Year: " + studYear, bfBold12, Element.ALIGN_LEFT);
            addNoBorderCell(infoTable, "", bfBold12, Element.ALIGN_LEFT); // Empty spacing cell

            doc.add(infoTable);
            doc.add(new Paragraph("\n"));

            // 5. Grades Table Header
            float[] columnWidths = {2f, 3f, 2f, 2f, 2f, 2f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);

            insertCell(table, "SubjID", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Subj Code", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prelim", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Midterm", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Prefinal", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Final", Element.ALIGN_CENTER, 1, bfBold12);

            // 6. Query Enrolled Subjects and Grades
            String gradesSql = "SELECT s.subjid, s.subjcode, g.prelim, g.midterm, g.prefinal, g.final "
                             + "FROM enroll e "
                             + "JOIN subjects s ON e.subjid = s.subjid "
                             + "LEFT JOIN grades g ON e.eid = g.enroll_eid "
                             + "WHERE e.studid = ?";

            PreparedStatement pstmtGrades = EnrollmentSystem.con.prepareStatement(gradesSql);
            pstmtGrades.setInt(1, Integer.parseInt(studId));
            ResultSet rsGrades = pstmtGrades.executeQuery();

            int subjectCount = 0;
            while (rsGrades.next()) {
                subjectCount++;
                String subjid = rsGrades.getString("subjid");
                String subjcode = rsGrades.getString("subjcode") != null ? rsGrades.getString("subjcode") : "";
                String prelim = rsGrades.getString("prelim") != null ? rsGrades.getString("prelim") : "null";
                String midterm = rsGrades.getString("midterm") != null ? rsGrades.getString("midterm") : "null";
                String prefinal = rsGrades.getString("prefinal") != null ? rsGrades.getString("prefinal") : "null";
                String fin = rsGrades.getString("final") != null ? rsGrades.getString("final") : "null";

                insertCell(table, subjid, Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, subjcode, Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, prelim, Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, midterm, Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, prefinal, Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, fin, Element.ALIGN_CENTER, 1, bf12);
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            // 7. Footer: Number of Subjects Listed
            Paragraph subjectCountPara = new Paragraph("Number of Subjects Listed: " + subjectCount, bfBold12);
            subjectCountPara.setIndentationLeft(30f);
            doc.add(subjectCountPara);

            doc.add(new Paragraph("\n\n\n"));

            // 8. Footer: Registrar Signature Line
            PdfPTable sigTable = new PdfPTable(1);
            sigTable.setWidthPercentage(40f);
            sigTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell sigCell = new PdfPCell(new Phrase("___________________________\nRegistrar", bfBold12));
            sigCell.setBorder(Rectangle.NO_BORDER);
            sigCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sigTable.addCell(sigCell);

            doc.add(sigTable);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
            if (docWriter != null) {
                docWriter.close();
            }
        }

        // Automatically open the generated PDF
        try {
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pdfFile.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOX);
        cell.setBackgroundColor(new BaseColor(255, 255, 255));
        table.addCell(cell);
    }

    private static void addNoBorderCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}