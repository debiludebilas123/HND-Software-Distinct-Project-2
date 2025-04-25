import GUI.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::initializeGUI);
    }

    public static void initializeGUI() {
        JFrame frame = new JFrame("Car Service System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 250);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);

        // Creating class instances for display
        CustomerManagement customerManagement = new CustomerManagement(frame);
        CarManagement carManagement = new CarManagement(frame);
        GarageManagement garageManagement = new GarageManagement(frame);
        JobManagement jobManagement = new JobManagement(frame);
        MainMenu mainMenu = new MainMenu(frame);
        AverageCustomerSpendingManagement averageCustomerSpendingManagement = new AverageCustomerSpendingManagement(frame);

        // Adding panels to the main panel which is connected to the card layout, so I can easily switch between each panel
        panel.add(mainMenu.getMainMenuPanel(), "MainMenu");
        panel.add(customerManagement.getCustomerManagementPanel(), "CustomerManagement");
        panel.add(carManagement.getCarManagementPanel(), "CarManagement");
        panel.add(garageManagement.getGarageManagementPanel(), "GarageManagement");
        panel.add(jobManagement.getJobManagementPanel(), "JobManagement");
        panel.add(averageCustomerSpendingManagement.getAvgCustomerSpendingPanel(), "AverageCustomerSpending");

        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}
