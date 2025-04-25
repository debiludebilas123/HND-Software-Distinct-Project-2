package GUI;

import Database.DatabaseConnection;
import Models.BaseManagementPanel;
import Models.Car;
import Models.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class CarManagement extends BaseManagementPanel {
    private JSpinner yearSpinner;
    private JButton addCarButton;
    private JButton editCarButton;
    private JPanel carManagementPanel;
    private JTable carManagementTable;
    private JTextField makeInput;
    private JTextField modelInput;
    private JButton deleteCarButton;
    private JButton saveCarButton;
    private JButton backToMenuButton;
    private JComboBox customerComboBox;
    private JTextField regNoInput;
    private final String[] carDatabaseColumnNames = {"regNo", "make", "model", "year", "customerID"};

    public CarManagement(JFrame frame) {
        super(frame, null, null);
        this.panel = carManagementPanel;
        this.table = carManagementTable;

        loadData("car");
        setupTableSelectionListener();
        reloadingDataAndBoxes(carManagementPanel, "car");
        setYearSpinnerModel();
        populateComboBox();

        addCarRecord();
        editCarRecord();
        saveCarRecord();
        deleteCarButton.addActionListener(e -> deleteRecord("car", "regNo"));

        backToMenuButton.addActionListener(e -> switchToMainPanel());
    }

    private void addCarRecord() {
        addCarButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            Car car = getCar();

            // Add to program table
            DefaultTableModel model = (DefaultTableModel) carManagementTable.getModel();
            model.addRow(new Object[]{car.getRegNo(), car.getMake(),
            car.getModel(), car.getYear(), car.getCustomerID()});
            regNoInput.setText("");
            makeInput.setText("");
            modelInput.setText("");
            yearSpinner.setValue(2000);

            try {
                DatabaseConnection.insertEntity(car);
            } catch (SQLException ex) {
                showError("Failed to insert the car record into the database.");
            }

            showSuccess("Car added successfully.");
        });
    }

    private Car getCar() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();

        // Create object with inputs
        return new Car(
                regNoInput.getText(),
                makeInput.getText(),
                modelInput.getText(),
                (Integer) yearSpinner.getValue(),
                selectedCustomer.getCustomerID()
        );
    }

    private void editCarRecord() {
        editCarButton.addActionListener(e -> {
            if (selectedRowData != null) {
                regNoInput.setText(selectedRowData[0].toString());
                makeInput.setText(selectedRowData[1].toString());
                modelInput.setText(selectedRowData[2].toString());
                int year = Integer.parseInt(selectedRowData[3].toString());
                yearSpinner.setValue(year);
            } else {
                showError("Please select a row from the table to edit.");
            }
        });
    }

    private void saveCarRecord() {
        saveCarButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            try {
                saveChanges("car", "regNo", carDatabaseColumnNames, getCarSavingInputs());
                showSuccess("Successfully saved the car record.");
                loadData("car");
            } catch (SQLException ex) {
                showError("Couldn't save the car record.");
            }
        });
    }

    private Object[] getCarValidatingInputs() {
        String make = makeInput.getText();
        String model = modelInput.getText();
        String year = yearSpinner.getValue().toString();

        return new Object[]{make, model, year};
    }

    private String[] getCarSavingInputs() {
        String regNo = (String) selectedRowData[0];
        String make = makeInput.getText();
        String model = modelInput.getText();
        String year = yearSpinner.getValue().toString();

        Customer customer = (Customer) customerComboBox.getSelectedItem();
        String customerID = customer.getCustomerID();

        return new String[]{regNo, make, model, year, customerID};
    }

    @Override
    protected boolean validateInputs() {
        Object[] inputs = getCarValidatingInputs();

        // Check if any inputs are empty
        for (Object input : inputs) {
            if (input.toString().isEmpty()) {
                showError("Please fill all of the fields.");
                return false;
            }
        }

        return true;
    }

    private void setYearSpinnerModel() {
        SpinnerNumberModel yearModel = new SpinnerNumberModel(
                2000,
                1900,
                Year.now().getValue() + 1,
                1
        );

        yearSpinner.setModel(yearModel);

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "#");
        yearSpinner.setEditor(editor);
    }

    private void populateComboBox() {
        try {
            // Remove any leftover selections
            customerComboBox.removeAllItems();

            // Database columns
            String[] customerColumnLabels = {"customerID", "forename", "surname", "address", "postCode", "phoneNo"};

            // Create objects
            List<Customer> customers = DatabaseConnection.fetchComboBoxValues("customer", customerColumnLabels, Customer.class);

            // Set selections in combo box
            for (Customer customer : customers) {
                customerComboBox.addItem(customer);
            }
        } catch (SQLException ex) {
            showError("Couldn't fetch customer information into combo box.");
        }
    }

    protected void reloadingDataAndBoxes(JPanel panel, String tableName) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData(tableName);
                populateComboBox();
            }
        });
    }

    public JPanel getCarManagementPanel() {
        return carManagementPanel;
    }
}
