package GUI;

import Database.DatabaseConnection;
import Models.BaseManagementPanel;
import Models.Car;
import Models.Garage;
import Models.Job;
import Services.PanelSwitcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JobManagement extends BaseManagementPanel {
    private JPanel jobManagementPanel;
    private JTextField dateInInput;
    private JComboBox carRegisterComboBox;
    private JComboBox garageComboBox;
    private JButton addJobButton;
    private JButton editJobButton;
    private JButton deleteJobButton;
    private JButton saveJobButton;
    private JTable jobManagementTable;
    private JButton backToMenuButton;
    private JTextField dateOutInput;
    private JTextField costInput;
    private JComboBox paymentStatusComboBox;
    private JButton showLongestStayingCarButton;
    private JButton showAverageCustomerSpendingButton;
    private final String[] jobDatabaseColumnNames = {"jobID", "regNo", "garageID", "dateIn", "dateOut", "cost", "paymentStatus"};

    public JobManagement(JFrame frame) {
        super(frame, null, null);
        this.panel = jobManagementPanel;
        this.table = jobManagementTable;

        loadData("job");
        populateComboBoxes();
        setupTableSelectionListener();
        reloadingDataAndBoxes(jobManagementPanel, "job");

        addJobRecord();
        saveJobRecord();
        editJobRecord();
        deleteJobButton.addActionListener(e -> deleteRecord("job", "jobID"));
        showAverageCustomerSpendingButton.addActionListener(e -> PanelSwitcher.switchPanel(jobManagementPanel, "AverageCustomerSpending", parentFrame, 800, 800));
        showLongestStayingCarButton.addActionListener(e -> showLongestStayingCar());

        backToMenuButton.addActionListener(e -> switchToMainPanel());
    }

    private void showLongestStayingCar() {
        try {
            DatabaseConnection.CarStayRecord record = DatabaseConnection.fetchLongestStayingCar();

            if (record != null) {
                String message = String.format(
                        "The longest staying car was a %s %s (%s) - Registration: %s%n" +
                        "It stayed for %d days.",
                        record.make(), record.model(), record.year(),
                        record.regNo(), record.daysStayed()
                );

                JOptionPane.showMessageDialog(
                        parentFrame,
                        message,
                        "Longest Staying Car",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        parentFrame,
                        "No completed jobs found in the system.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    parentFrame,
                    "Error fetching longest stay data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateComboBoxes() {
        try {
            // Remove any leftover selections from last time
            garageComboBox.removeAllItems();
            carRegisterComboBox.removeAllItems();
            paymentStatusComboBox.removeAllItems();

            // Database column names
            String[] garageColumnLabels = {"garageID", "garageName", "address", "town", "postcode", "phoneNo"};
            String[] carColumnLabels = {"regNo", "make", "model", "year", "customerID"};

            // Get insurance data from database and create objects with it
            List<Garage> garages = DatabaseConnection.fetchComboBoxValues("garage", garageColumnLabels, Garage.class);
            List<Car> cars = DatabaseConnection.fetchComboBoxValues("car", carColumnLabels, Car.class);

            // Set the selections for the combo boxes
            for (Garage garage : garages) {
                garageComboBox.addItem(garage);
            }
            for (Car car : cars) {
                carRegisterComboBox.addItem(car);
            }

            paymentStatusComboBox.addItem("Pending");
            paymentStatusComboBox.addItem("Paid");
            paymentStatusComboBox.addItem("Cancelled");

        } catch (SQLException e) {
            showError("Error loading data into job management combo boxes.");
        }
    }

    protected void reloadingDataAndBoxes(JPanel panel, String tableName) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData(tableName);
                populateComboBoxes();
            }
        });
    }

    private void editJobRecord() {
        editJobButton.addActionListener(e -> {
            if (selectedRowData != null) {
                String regNo = selectedRowData[1].toString();
                Car car = (Car) findItemInComboBoxById(carRegisterComboBox, regNo);
                if (car != null) {
                    setComboBoxSelection(carRegisterComboBox, car);
                }

                String garageID = selectedRowData[2].toString();
                Garage garage = (Garage) findItemInComboBoxById(garageComboBox, garageID);
                if (garage != null) {
                    setComboBoxSelection(garageComboBox, garage);
                }

                dateInInput.setText(selectedRowData[3].toString());
                dateOutInput.setText(selectedRowData[4].toString());
                costInput.setText(selectedRowData[5].toString());
                paymentStatusComboBox.setSelectedItem(selectedRowData[6].toString());
            } else {
                showError("Please select a row in the table to edit.");
            }
        });
    }

    private void saveJobRecord() {
        saveJobButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            try {
                saveChanges("job", "jobID", jobDatabaseColumnNames, getGarageSavingInputs());
                showSuccess("Successfully saved the job record.");
                loadData("job");
            } catch (SQLException ex) {
                showError("Couldn't save the job record.");
            }
        });
    }

    private void addJobRecord() {
        addJobButton.addActionListener(e -> {
            if (!validateInputs()) {
                return;
            }

            // Generate a job ID
            String jobID;
            try {
                jobID = DatabaseConnection.getJobIDFromDatabase();
            } catch (SQLException ex) {
                showError("Failed to generate a job ID.");
                throw new RuntimeException(ex);
            }

            Job job = getJob(jobID);

            // Add to program table
            DefaultTableModel model = (DefaultTableModel) jobManagementTable.getModel();
            model.addRow(new Object[]{job.getJobID(), job.getRegNo(), job.getGarageID(), job.getDateIn(), job.getDateOut(), job.getCost(), job.getPaymentStatus()});
            dateInInput.setText("");
            dateOutInput.setText("");
            costInput.setText("");
            paymentStatusComboBox.setSelectedItem("Pending");

            try {
                DatabaseConnection.insertEntity(job);
            } catch (SQLException ex) {
                showError("Failed to insert job record to the database.");
            }

            showSuccess("Job added successfully.");
        });
    }

    private Job getJob(String jobID) {
        Car selectedCar = (Car) carRegisterComboBox.getSelectedItem();
        Garage selectedGarage = (Garage) garageComboBox.getSelectedItem();

        // Create job object with inputs
        return new Job(
                jobID,
                selectedCar.getRegNo(),
                selectedGarage.getGarageID(),
                LocalDate.parse(dateInInput.getText()),
                LocalDate.parse(dateOutInput.getText()),
                Double.parseDouble(costInput.getText()),
                (String) paymentStatusComboBox.getSelectedItem()
        );
    }

    private String[] getGarageSavingInputs() {
        String jobID = (String) selectedRowData[0];

        Car car = (Car) carRegisterComboBox.getSelectedItem();
        String regNo = car.getRegNo();

        Garage garage = (Garage) garageComboBox.getSelectedItem();
        String garageID = garage.getGarageID();

        String dateIn = dateInInput.getText();
        String dateOut = dateOutInput.getText();
        String cost = costInput.getText();
        String paymentStatus = paymentStatusComboBox.getSelectedItem().toString();

        return new String[]{jobID, regNo, garageID, dateIn, dateOut, cost, paymentStatus};
    }

    private Object[] getJobValidatingInputs() {
        String dateIn = dateInInput.getText();
        String dateOut = dateOutInput.getText();
        String cost = costInput.getText();

        return new Object[]{dateIn, dateOut, cost};
    }

    public JPanel getJobManagementPanel() {
        return jobManagementPanel;
    }

    @Override
    protected boolean validateInputs() {
        Object[] values = getJobValidatingInputs();

        // Check if any inputs are empty
        for (Object value : values) {
            if (value.toString().isEmpty()) {
                showError("Please fill all the fields.");
                return false;
            }
        }

        // Check if date is a valid date format
        if (!isObjectLocalDate(values[0]) || !isObjectLocalDate(values[1])) {
            showError("Please enter valid dates e.g. 2020-10-10");
            return false;
        }

        // Check if cost is a valid double
        if (!isObjectDouble(values[2])) {
            showError("Please enter a positive number for cost e.g. 177.62");
            return false;
        }

        return true;
    }
}
