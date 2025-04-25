package GUI;

import Database.DatabaseConnection;
import Models.BaseManagementPanel;
import Models.Garage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class GarageManagement extends BaseManagementPanel {
    private JPanel garageManagementPanel;
    private JTextField garageNameInput;
    private JButton addGarageButton;
    private JButton editGarageButton;
    private JButton deleteGarageButton;
    private JButton saveGarageButton;
    private JTextField addressInput;
    private JTextField townInput;
    private JTextField postcodeInput;
    private JTextField phoneNoInput;
    private JTable garageManagementTable;
    private JButton backToMenuButton;
    private final String[] garageDatabaseColumnNames = {"garageID", "garageName", "address", "town", "postcode", "phoneNo"};

    public GarageManagement(JFrame frame) {
        super(frame, null, null);
        this.table = garageManagementTable;
        this.panel = garageManagementPanel;

        loadData("garage");
        setupTableSelectionListener();
        reloadingData(garageManagementTable, "garage");

        addGarageRecord();
        saveCustomerRecord();
        editGarageRecord();
        deleteGarageButton.addActionListener(e -> deleteRecord("garage", "garageID"));

        backToMenuButton.addActionListener(e -> switchToMainPanel());
    }

    private void addGarageRecord() {
        addGarageButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            // Generate a garage ID
            String garageID;
            try {
                garageID = DatabaseConnection.getGarageIDFromDatabase();
                String firstLetterTown = townInput.getText().substring(0, 1).toUpperCase();
                garageID = garageID.concat(firstLetterTown);
            } catch (SQLException ex) {
                showError("Failed to generate a garage ID.");
                throw new RuntimeException(ex);
            }

            if (!validateInputs()) {
                return;
            }

            // Create garage object based on inputs
            Garage garage = new Garage(
                    garageID,
                    garageNameInput.getText(),
                    addressInput.getText(),
                    townInput.getText(),
                    postcodeInput.getText(),
                    phoneNoInput.getText()
            );

            // Add to program table
            DefaultTableModel model = (DefaultTableModel) garageManagementTable.getModel();
            model.addRow(new Object[]{garage.getGarageID(), garage.getGarageName(), garage.getAddress(), garage.getTown(), garage.getPostcode(), garage.getPhoneNo()});
            garageNameInput.setText("");
            addressInput.setText("");
            townInput.setText("");
            postcodeInput.setText("");
            phoneNoInput.setText("");

            try {
                DatabaseConnection.insertEntity(garage);
            } catch (SQLException ex) {
                showError("Failed to insert the record to the database.");
            }

            showSuccess("Garage added successfully.");
        });
    }

    private void saveCustomerRecord() {
        saveGarageButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            try {
                saveChanges("garage", "garageID", garageDatabaseColumnNames, getGarageSavingInputs());
                showSuccess("Successfully saved the garage record.");
                loadData("garage");
            } catch (SQLException ex) {
                showError("Couldn't save the the garage record.");
            }
        });
    }

    private String[] getGarageSavingInputs() {
        String garageID = (String) selectedRowData[0];
        String garageName = garageNameInput.getText();
        String address = addressInput.getText();
        String town = townInput.getText();
        String postcode = postcodeInput.getText();
        String phoneNo = phoneNoInput.getText();

        return new String[]{garageID, garageName, address, town, postcode, phoneNo};
    }

    private void editGarageRecord() {
        editGarageButton.addActionListener(e -> {
            if (selectedRowData != null) {
                // Load all inputs based on selected row's data on button press
                garageNameInput.setText(selectedRowData[1].toString());
                addressInput.setText(selectedRowData[2].toString());
                townInput.setText(selectedRowData[3].toString());
                postcodeInput.setText(selectedRowData[4].toString());
                phoneNoInput.setText(selectedRowData[5].toString());
            } else {
                showError("Please select a row from the table to edit.");
            }
        });
    }

    private Object[] getGarageValidatingInputs() {
        String garageName = garageNameInput.getText();
        String address = addressInput.getText();
        String town = townInput.getText();
        String postcode = postcodeInput.getText();
        String phoneNo = phoneNoInput.getText();

        return new Object[]{garageName, address, town, postcode, phoneNo};
    }

    @Override
    protected boolean validateInputs() {
        Object[] values = getGarageValidatingInputs();

        // Check if any inputs are empty
        for (Object value : values) {
            if (value.toString().isEmpty()) {
                showError("Please fill all the fields");
                return false;
            }
        }

        // Check if user entered a valid phone number
        if (!isValidPhoneNum(values[4].toString())) {
            showError("Please enter a valid phone number.");
            return false;
        }

        return true;
    }

    public JPanel getGarageManagementPanel() {
        return garageManagementPanel;
    }
}
