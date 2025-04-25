package GUI;

import Services.PanelSwitcher;

import javax.swing.*;

public class MainMenu {

    private JButton customerManagementButton;
    private JButton garageManagementButton;
    private JButton carManagementButton;
    private JButton jobManagementButton;
    private JPanel mainMenuPanel;
    private JButton exitSystemButton;
    private JFrame frame;

    public MainMenu(JFrame frame) {
        this.frame = frame;

        setupButtonFunction();
    }

    public void setupButtonFunction() {
        // Triggers for when buttons are pressed
        // Switching panels
        customerManagementButton.addActionListener(e -> PanelSwitcher.switchPanel(mainMenuPanel, "CustomerManagement", frame, 750, 600));
        garageManagementButton.addActionListener(e -> PanelSwitcher.switchPanel(mainMenuPanel, "GarageManagement", frame, 920, 450));
        carManagementButton.addActionListener(e -> PanelSwitcher.switchPanel(mainMenuPanel, "CarManagement", frame, 800, 600));
        jobManagementButton.addActionListener(e -> PanelSwitcher.switchPanel(mainMenuPanel, "JobManagement", frame, 800, 800));

        // Exit the program
        exitSystemButton.addActionListener(e -> System.exit(0));
    }

    public JPanel getMainMenuPanel() {
        return mainMenuPanel;
    }
}
