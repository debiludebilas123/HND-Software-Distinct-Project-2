package Models;

import Database.DatabaseConnection;
import Services.ManagementController;
import Services.PanelSwitcher;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseManagementPanel {
    protected JTable table;
    protected JFrame parentFrame;
    protected JPanel panel;
    protected Object[] selectedRowData;

    public BaseManagementPanel(JFrame parentFrame, JPanel panel, JTable table) {
        this.parentFrame = parentFrame;
        this.panel = panel;
        this.table = table;
    }

    protected void loadData(String tableName) { // Load specific entity data (e.g., Patient, Doctor)
        ManagementController.loadDataIntoTable(
                table,
                () -> {
                    try {
                        // Fetches database table data
                        return DatabaseConnection.fetchTable(tableName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                tableName
        );
    }

    protected void loadAverageCustomerSpendingData() { // Load specific entity data (e.g., Patient, Doctor)
        ManagementController.loadDataIntoTable(
                table,
                () -> {
                    try {
                        // Fetches database table data
                        return DatabaseConnection.fetchAverageCustomerSpending();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    protected void saveChanges(String tableName, String idColumn, String[] columns, Object[] values) throws SQLException {   // Save to DB
        DatabaseConnection.updateRecord(tableName, idColumn, selectedRowData[0].toString(), columns, values);
    }

    protected void setupTableSelectionListener() {
        // Method for loading data from the row which the user selects in the program for editing and saving data
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedRowData = new Object[table.getColumnCount()];
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        selectedRowData[i] = table.getValueAt(selectedRow, i);
                    }
                }
            }
        });
    }

    protected <T> T findItemInComboBoxById(JComboBox<T> comboBox, String ID) {
        // Method for finding the item by ID, so I can display it on the selection combo box when editing
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item instanceof Insertable) {
                Insertable insertableItem = (Insertable) item;
                if (insertableItem.getId().equals(ID)) {
                    return item;
                }
            }
        }
        showError("Item not found.");
        return null;
    }

    protected <T> void setComboBoxSelection(JComboBox<T> comboBox, Object value) {
        // Setting the found item in the combo box selector
        T item = findItemInComboBox(comboBox, value);
        if (item != null) {
            comboBox.setSelectedItem(item);
        } else {
            showError("Item not found in combo box: " + value);
        }
    }

    protected <T> T findItemInComboBox(JComboBox<T> comboBox, Object value) {
        // Method for finding the item by name, so I can display it on the selection combo box when editing
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item.toString().equals(value.toString())) {
                return item;
            }
        }
        showError("Item not found.");
        return null;
    }

    protected void reloadingData(JTable table, String tableName) {
        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData(tableName);
            }
        });
    }

    protected void deleteRecord(String tableName, String idColumn) {   // Delete from DB
        if (selectedRowData != null) {
            ManagementController.deleteSelectedRow(table, tableName, idColumn, selectedRowData[0], parentFrame);
        } else {
            showError("No record selected!");
        }
    }

    // Mandatory method for all management panels to validate inputs
    protected abstract boolean validateInputs();

    protected boolean isObjectDouble(Object o) {
        // Tries parsing the object o as a double and if it pops an error out that means it isn't and vice versa
        try {
            double num = Double.parseDouble(o.toString());
            if (num > 0) {
                return true;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean isObjectLocalDate(Object o) {
        // Tries parsing the object o as a local date and if it pops an error out that means it isn't and vice versa
        try {
            LocalDate.parse(o.toString());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    protected boolean isValidPhoneNum(String phoneNumber) {
        // Uses a regex to compare the given phone number to the correct format (+441234567890)
        String regex = "^07\\d{3}\\s\\d{6}$";  // Regex for UK phone numbers
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    protected void switchToMainPanel() {
        PanelSwitcher.switchPanel(panel, "MainMenu", parentFrame, 500, 370);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}