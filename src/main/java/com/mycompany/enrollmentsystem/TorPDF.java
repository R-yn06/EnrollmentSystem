
package com.mycompany.enrollmentsystem;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TorPDF {

    public static void generateAndOpenTOR(String studId) {

        Document doc = new Document();
        PdfWriter docWriter = null;

        Font bfBold14 = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Font bfBold11 = new Font(FontFamily.TIMES_ROMAN, 11, Font.BOLD);
        Font bfBold10 = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        Font bf10 = new Font(FontFamily.TIMES_ROMAN, 10);
        Font bf9 = new Font(FontFamily.TIMES_ROMAN, 9);

        String fileName = "TOR_Student_" + studId + ".pdf";
        File pdfFile = new File(System.getProperty("user.home"), fileName);

        try {

            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
            doc.open();

            // 1. HEADER (SCHOOL LOGO AND SCHOOL NAME)

            float[] headerWidths = {1.5f, 5f};
            PdfPTable headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(80f);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            Image schoolLogo = Image.getInstance(
                TorPDF.class.getResource("/Seal_of_the_International_State_College_of_the_Philippines.png")
            );

            schoolLogo.scaleToFit(75f, 75f);

            PdfPCell cellLogo = new PdfPCell(schoolLogo);
            cellLogo.setBorder(Rectangle.NO_BORDER);
            cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cellText = new PdfPCell(
                new Phrase(
                    "International State College of The Philippines",
                    bfBold11
                )
            );

            cellText.setBorder(Rectangle.NO_BORDER);
            cellText.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellText.setHorizontalAlignment(Element.ALIGN_LEFT);

            headerTable.addCell(cellLogo);
            headerTable.addCell(cellText);

            doc.add(headerTable);
            doc.add(new Paragraph("\n"));

            // 2. TITLE

            Paragraph title = new Paragraph(
                "Official Transcript of Records",
                bfBold14
            );

            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            // 3. FETCH STUDENT INFORMATION

            String studName = "";
            String studCourse = "";
            String studGender = "";

            String studentSql =
                "SELECT studname, studcrs, studgender "
                + "FROM students WHERE studid = ?";

            PreparedStatement pstmtStud =
                EnrollmentSystem.con.prepareStatement(studentSql);

            pstmtStud.setInt(1, Integer.parseInt(studId));

            ResultSet rsStud = pstmtStud.executeQuery();

            if (rsStud.next()) {

                studName = rsStud.getString("studname") != null
                    ? rsStud.getString("studname") : "";

                studCourse = rsStud.getString("studcrs") != null
                    ? rsStud.getString("studcrs") : "";

                studGender = rsStud.getString("studgender") != null
                    ? rsStud.getString("studgender") : "";
            }

            rsStud.close();
            pstmtStud.close();

            // 4. STUDENT INFORMATION TABLE

            float[] infoWidths = {5f, 5f};

            PdfPTable infoTable = new PdfPTable(infoWidths);
            infoTable.setWidthPercentage(90f);

            addNoBorderCell(
                infoTable,
                "Student ID: " + studId,
                bfBold11,
                Element.ALIGN_LEFT
            );

            addNoBorderCell(
                infoTable,
                "Student Name: " + studName,
                bfBold11,
                Element.ALIGN_LEFT
            );

            addNoBorderCell(
                infoTable,
                "Student Course: " + studCourse,
                bfBold11,
                Element.ALIGN_LEFT
            );

            addNoBorderCell(
                infoTable,
                "Gender: " + studGender,
                bfBold11,
                Element.ALIGN_LEFT
            );

            doc.add(infoTable);
            doc.add(new Paragraph("\n"));

            // 5. MASTER TABLE FOR SUBJECTS AND GRADES

            float[] columnWidths = {1.8f, 2f, 4f, 2f, 1.5f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);

            addHeaderCell(table, "SubjID", Element.ALIGN_CENTER, bfBold10);
            addHeaderCell(table, "Subj Code", Element.ALIGN_CENTER, bfBold10);
            addHeaderCell(table, "Descriptive Title", Element.ALIGN_CENTER, bfBold10);
            addHeaderCell(table, "Final", Element.ALIGN_CENTER, bfBold10);
            addHeaderCell(table, "Credit", Element.ALIGN_CENTER, bfBold10);

            // 6. DISCOVER SEMESTER DATABASES

            List<String> semesterDbs = new ArrayList<>();

            Connection tempCon = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL",
                EnrollmentSystem.currentUser.isEmpty()
                    ? "root"
                    : EnrollmentSystem.currentUser,
                EnrollmentSystem.currentPassword
            );

            Statement tempSt = tempCon.createStatement();

            ResultSet rsDbs = tempSt.executeQuery("SHOW DATABASES");

            while (rsDbs.next()) {

                String dbName = rsDbs.getString(1);
                String lowerDb = dbName.toLowerCase();

                if (lowerDb.startsWith("1st_sy")
                    || lowerDb.startsWith("2nd_sy")
                    || lowerDb.startsWith("summer_sy")) {

                    semesterDbs.add(dbName);
                }
            }

            rsDbs.close();
            tempSt.close();

            int totalSubjectsListed = 0;

            // 7. RETRIEVE ENROLLED SUBJECTS FROM EACH SEMESTER

            for (String dbName : semesterDbs) {

                try {

                    String query =
                        "SELECT s.subjid, s.subjcode, s.subjdesc, "
                        + "s.subjunits, g.final "
                        + "FROM `" + dbName + "`.enroll e "
                        + "JOIN `" + dbName + "`.subjects s "
                        + "ON e.subjid = s.subjid "
                        + "LEFT JOIN `" + dbName + "`.grades g "
                        + "ON e.eid = g.enroll_eid "
                        + "WHERE e.studid = ?";

                    PreparedStatement pstmt =
                        tempCon.prepareStatement(query);

                    pstmt.setInt(1, Integer.parseInt(studId));

                    ResultSet rs = pstmt.executeQuery();

                    boolean hasSemRows = false;

                    while (rs.next()) {

                        if (!hasSemRows) {

                            PdfPCell semCell = new PdfPCell(
                                new Phrase(dbName, bfBold10)
                            );

                            semCell.setColspan(5);
                            semCell.setBorder(Rectangle.NO_BORDER);
                            semCell.setHorizontalAlignment(Element.ALIGN_LEFT);

                            table.addCell(semCell);

                            hasSemRows = true;
                        }

                        totalSubjectsListed++;

                        String subjid = rs.getString("subjid");

                        String subjcode = rs.getString("subjcode") != null
                            ? rs.getString("subjcode") : "";

                        String subjdesc = rs.getString("subjdesc") != null
                            ? rs.getString("subjdesc") : "";

                        String units = String.valueOf(
                            rs.getInt("subjunits")
                        );

                        String fin = rs.getString("final");

                        if (fin == null
                            || fin.trim().isEmpty()
                            || fin.equalsIgnoreCase("null")) {

                            fin = "No Grade";
                        }

                        addBorderlessRowCell(
                            table, subjid, Element.ALIGN_CENTER, bf10
                        );

                        addBorderlessRowCell(
                            table, subjcode, Element.ALIGN_CENTER, bf10
                        );

                        addBorderlessRowCell(
                            table, subjdesc, Element.ALIGN_CENTER, bf10
                        );

                        addBorderlessRowCell(
                            table, fin, Element.ALIGN_CENTER, bf10
                        );

                        addBorderlessRowCell(
                            table, units, Element.ALIGN_CENTER, bf10
                        );
                    }

                    rs.close();
                    pstmt.close();

                } catch (Exception e) {

                    System.out.println(
                        "Skipping database " + dbName + ": " + e.getMessage()
                    );
                }
            }

            tempCon.close();

            doc.add(table);
            doc.add(new Paragraph("\n"));

            // 8. TOTAL SUBJECT COUNT

            Paragraph subjectCountPara = new Paragraph(
                "Number of Subjects Listed: " + totalSubjectsListed,
                bfBold11
            );

            subjectCountPara.setIndentationLeft(30f);
            doc.add(subjectCountPara);

            doc.add(new Paragraph("\n"));

            // 9. GRADING SYSTEM

            float[] legendWidths = {5f, 5f};

            PdfPTable legendTable = new PdfPTable(legendWidths);
            legendTable.setWidthPercentage(90f);

            String oldGrading =
                "Grading System up to SY 1981-82\n\n"
                + "95-100 = 1.0 = Excellent\n"
                + "90-94 = 1.5 = Very Good\n"
                + "85-89 = 2.0 = Good\n"
                + "80-84 = 2.5 = Fair\n"
                + "75-79 = 3.0 = Passed\n"
                + "Below 75 = 5.0 = Failed";

            String newGrading =
                "New Grading System (1st Sem. SY1982-83 & up)\n\n"
                + "95-100 = 1.0 = Excellent\n"
                + "90-94 = 1.5 = Very Good\n"
                + "85-89 = 2.0 = Good\n"
                + "80-84 = 2.5 = Fair\n"
                + "75-79 = 3.0 = Passed\n"
                + "Below 75 = 5.0 = Failed";

            addNoBorderCell(
                legendTable, oldGrading, bf9, Element.ALIGN_LEFT
            );

            addNoBorderCell(
                legendTable, newGrading, bf9, Element.ALIGN_LEFT
            );

            doc.add(legendTable);

            Paragraph extraLegend = new Paragraph(
                "FOR FCF Grades: O - Outstanding, HS - Highly Satisfactory, "
                + "MS - Moderately Satisfactory, S - Satisfactory, "
                + "F - Fair, P - Poor\n"
                + "Quality Point Equivalent: 1.0 = 4.0, "
                + "1.5 = 3.5, 2.0 = 3.0, 2.5 = 2.5, 3.0 = 2.0",
                bf9
            );

            extraLegend.setIndentationLeft(30f);
            doc.add(extraLegend);

            doc.add(new Paragraph("\n\n"));

            // 10. SCHOOL SEAL DISCLAIMER

            Paragraph sealNotice = new Paragraph(
                "NOT VALID WITHOUT SCHOOL SEAL",
                bfBold11
            );

            sealNotice.setIndentationLeft(30f);
            doc.add(sealNotice);

            doc.add(new Paragraph("\n\n"));

            // 11. SIGNATURES FOOTER

            float[] sigWidths = {5f, 5f};

            PdfPTable sigTable = new PdfPTable(sigWidths);
            sigTable.setWidthPercentage(90f);

            String currentDate = new SimpleDateFormat(
                "MM/dd/yyyy"
            ).format(new Date());

            addNoBorderCell(
                sigTable,
                "Prepared By: RYAN PIERRE CARAOS\n"
                + "___________________________________\n\n"
                + "Date: " + currentDate,
                bfBold10,
                Element.ALIGN_LEFT
            );

            addNoBorderCell(
                sigTable,
                "Checked By: CHRISTOPHER ALVIOLA\n"
                + "___________________________________\n\n"
                + "Registrar: ATTY. EDGAR ESCALANTE\n"
                + "___________________________________",
                bfBold10,
                Element.ALIGN_LEFT
            );

            doc.add(sigTable);

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (doc != null && doc.isOpen()) {
                doc.close();
            }

            if (docWriter != null) {
                docWriter.close();
            }
        }

        // 12. AUTOMATICALLY OPEN GENERATED PDF

        try {

            if (pdfFile.exists()) {

                if (Desktop.isDesktopSupported()) {

                    Desktop.getDesktop().open(pdfFile);

                } else {

                    Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler "
                        + pdfFile.getAbsolutePath()
                    );
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    // HEADER CELL

    private static void addHeaderCell(
        PdfPTable table,
        String text,
        int align,
        Font font
    ) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setHorizontalAlignment(align);
        cell.setBorder(Rectangle.BOX);
        cell.setBackgroundColor(new BaseColor(255, 255, 255));

        table.addCell(cell);
    }

    // BORDERLESS ROW CELL

    private static void addBorderlessRowCell(
        PdfPTable table,
        String text,
        int align,
        Font font
    ) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setHorizontalAlignment(align);
        cell.setBorder(Rectangle.NO_BORDER);

        table.addCell(cell);
    }

    // NO BORDER CELL

    private static void addNoBorderCell(
        PdfPTable table,
        String text,
        Font font,
        int align
    ) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);

        table.addCell(cell);
    }
}
