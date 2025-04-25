package GUI;

import Models.BaseManagementPanel;
import Services.PanelSwitcher;

import javax.swing.*;

public class AverageCustomerSpendingManagement extends BaseManagementPanel {
    private JTable avgCustomerSpendingTable;
    private JButton backToJobManagementButton;
    private JPanel avgCustomerSpendingPanel;

    public AverageCustomerSpendingManagement(JFrame frame) {
        super(frame, null, null);
        this.table = avgCustomerSpendingTable;
        this.panel = avgCustomerSpendingPanel;

        loadAverageCustomerSpendingData();

        backToJobManagementButton.addActionListener(e -> PanelSwitcher.switchPanel(avgCustomerSpendingPanel, "JobManagement", parentFrame, 800, 800));
    }

    public JPanel getAvgCustomerSpendingPanel() {
        return avgCustomerSpendingPanel;
    }

    @Override
    protected boolean validateInputs() {
        return false;
    }
}
