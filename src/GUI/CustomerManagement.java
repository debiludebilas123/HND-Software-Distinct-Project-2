package GUI;

import Database.DatabaseConnection;
import Models.BaseManagementPanel;
import Models.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class CustomerManagement extends BaseManagementPanel {
    private JButton addCustomerButton;
    private JPanel customerManagementPanel;
    private JTable customerManagementTable;
    private JTextField forenameInput;
    private JTextField surnameInput;
    private JButton backToMenuButton;
    private JTextField phoneInput;
    private JTextField streetInput;
    private JTextField postcodeInput;
    private JButton editCustomerButton;
    private JButton saveCustomerButton;
    private JButton deleteCustomerButton;
    private final String[] customerDatabaseColumnNames = {"customerID", "forename", "surname", "address", "postcode", "phoneNo"};

    public CustomerManagement(JFrame frame) {
        super(frame, null, null);
        this.panel = customerManagementPanel;
        this.table = customerManagementTable;

        loadData("customer");
        setupTableSelectionListener();
        reloadingData(customerManagementTable, "customer");

        saveCustomerRecord();
        editCustomerRecord();
        deleteCustomerButton.addActionListener(e -> deleteRecord("customer", "customerID"));
        addCustomerRecord();

        backToMenuButton.addActionListener(e -> switchToMainPanel());
    }

    private void saveCustomerRecord() {
        saveCustomerButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            try {
                saveChanges("customer", "customerID", customerDatabaseColumnNames, getCustomerSavingInputs());
                loadData("customer");
                showSuccess("Successfully saved the customer.");
            } catch (SQLException ex) {
                showError("Something went wrong with saving the customer record.");
            }
        });
    }

    private void addCustomerRecord() {
        addCustomerButton.addActionListener(e -> {
            // Get customerID from the database (just running a max(customerID) query and adding 1
            String customerID = null;
            try {
                customerID = DatabaseConnection.getCustomerIDFromDatabase();
                int temp = Integer.parseInt(customerID);
                temp++;
                customerID = String.valueOf(temp);
            } catch (SQLException ex) {
                showError("Failed to generate a customer ID.");
                throw new RuntimeException(ex);
            }

            if (!validateInputs()) {
                return;
            }

            // Create customer object based on inputs and polish the data a bit
            Customer customer = new Customer(
                    customerID,
                    forenameInput.getText().substring(0, 1).toUpperCase() + forenameInput.getText().substring(1),
                    surnameInput.getText().substring(0, 1).toUpperCase() + surnameInput.getText().substring(1),
                    streetInput.getText(),
                    postcodeInput.getText().toUpperCase(),
                    phoneInput.getText()
            );

            // Add to program table
            DefaultTableModel model = (DefaultTableModel) customerManagementTable.getModel();
            model.addRow(new Object[]{customer.getCustomerID(), customer.getForename(), customer.getSurname(), customer.getAddress(), customer.getPostcode(), customer.getPhoneNo()});
            forenameInput.setText("");
            surnameInput.setText("");
            phoneInput.setText("");
            streetInput.setText("");
            postcodeInput.setText("");

            // Add to database table
            try {
                DatabaseConnection.insertEntity(customer);
            } catch (SQLException ex) {
                showError("Error adding the record to the database.");
            }
            showSuccess("Customer added successfully!");
        });
    }

    @Override
    protected boolean validateInputs() {
        Object[] values = getCustomerAddingValues();

        // Check if any inputs are empty
        for (Object value : values) {
            if (value.toString().isEmpty()) {
                showError("Please fill all the fields.");
                return false;
            }
        }

        // Check if it's a valid phone number with a valid format
        if (!isValidPhoneNum(values[4].toString())) {
            showError("Please enter a valid phone number.");
            return false;
        }

        return true;
    }

    private Object[] getCustomerAddingValues() {
        // Return the inputs as an object
        String forename = forenameInput.getText();
        String surname = surnameInput.getText();
        String phone = phoneInput.getText();
        String street = streetInput.getText();
        String postcode = postcodeInput.getText();

        return new Object[]{forename, surname, street, postcode, phone};
    }

    private String[] getCustomerSavingInputs() {
        String customerID = (String) selectedRowData[0];
        String forename = forenameInput.getText();
        String surname = surnameInput.getText();
        String phone = phoneInput.getText();
        String street = streetInput.getText();
        String postcode = postcodeInput.getText();

        return new String[]{customerID, forename, surname, street, postcode, phone};
    }

    private void editCustomerRecord() {
        editCustomerButton.addActionListener(e -> {
            if (selectedRowData != null) {
                // Load all inputs based on selected row's data on button press
                forenameInput.setText(selectedRowData[1].toString());
                surnameInput.setText(selectedRowData[2].toString());
                streetInput.setText(selectedRowData[3].toString());
                postcodeInput.setText(selectedRowData[4].toString());
                phoneInput.setText(selectedRowData[5].toString());
            } else {
                showError("Please select a row from the table to edit.");
            }
        });
    }

    public JPanel getCustomerManagementPanel() {
        return customerManagementPanel;
    }
}
