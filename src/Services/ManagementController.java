package Services;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ManagementController {
    public static void loadDataIntoTable(JTable table, Supplier<ResultSet> dataFetcher, String tableName) {
        try {
            // Get database table's data
            ResultSet rs = dataFetcher.get();

            // Create a table with fetched data
            DefaultTableModel tableModel = TableUtil.createGeneralTableModel(rs);

            table.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading " + tableName + " data. Please try again.");
            throw new RuntimeException(ex);
        }
    }

    public static void loadDataIntoTable(JTable table, Supplier<ResultSet> dataFetcher) {
        try {
            // Get database table's data
            ResultSet rs = dataFetcher.get();

            // Create a table with fetched data
            DefaultTableModel tableModel = TableUtil.createGeneralTableModel(rs);

            table.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading data. Please try again.");
            throw new RuntimeException(ex);
        }
    }

    public static void deleteSelectedRow(JTable table, String tableName, String columnName, Object selectedRowData, JFrame frame) {
        int selectedRow = table.getSelectedRow();

        // Check if a row is selected
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this " + tableName + " ?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Delete the entry from the JTable
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(selectedRow);
                try {
                    // Delete the data from the database
                    DatabaseConnection.deleteRow(tableName, columnName, selectedRowData.toString());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error deleting " + tableName + "! Please try again.");
                    throw new RuntimeException(ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a " + tableName + "!");
        }
    }

}
