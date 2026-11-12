package com.mycompany.enrollmentsystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

final class UiTheme {
    private static final Color BACKGROUND = new Color(17, 24, 39);
    private static final Color SURFACE = new Color(31, 41, 55);
    private static final Color FIELD = new Color(15, 23, 42);
    private static final Color BORDER = new Color(71, 85, 105);
    private static final Color TEXT = new Color(226, 232, 240);
    private static final Color MUTED_TEXT = new Color(148, 163, 184);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color TABLE_HEADER = new Color(30, 64, 175);

    private UiTheme() {
    }

    static void apply(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setBackground(BACKGROUND);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);
        styleComponent(frame.getContentPane());
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    private static void styleComponent(Component component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        component.setForeground(TEXT);

        if (component instanceof JPanel) {
            component.setBackground(SURFACE);
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            label.setForeground(TEXT);
            label.setVerticalAlignment(SwingConstants.CENTER);
            if (label.getFont().getSize() >= 24) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 26));
                label.setForeground(Color.WHITE);
            }
        } else if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(6, 9, 6, 9)));
            field.setBackground(FIELD);
            field.setForeground(TEXT);
            field.setCaretColor(TEXT);
            field.setHorizontalAlignment(JTextField.LEADING);
            field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        } else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setBackground(FIELD);
            comboBox.setForeground(TEXT);
            comboBox.setBorder(BorderFactory.createLineBorder(BORDER));
            comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 36));
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(ACCENT);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            button.setMargin(new Insets(4, 12, 4, 12));
            button.setFocusPainted(false);
            button.setOpaque(true);
        } else if (component instanceof JScrollPane) {
            component.setBackground(FIELD);
            ((JScrollPane) component).setBorder(BorderFactory.createLineBorder(BORDER));
        } else if (component instanceof JTable) {
            styleTable((JTable) component);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                styleComponent(child);
            }
        }
    }

    private static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(FIELD);
        table.setGridColor(BORDER);
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(30, 64, 175));
        table.setSelectionForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setBackground(TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setOpaque(true);
    }
}
