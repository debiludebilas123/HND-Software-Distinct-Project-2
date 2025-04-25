package Services;

import javax.swing.*;
import java.awt.*;

public class PanelSwitcher {
    // Function for switching between panels
    public static void switchPanel(JPanel currentPanel, String targetPanelName, JFrame frame, int width, int height) {
        Container parent = currentPanel.getParent();
        // Check if JPanel uses card layout
        if (parent instanceof JPanel) {
            CardLayout layout = (CardLayout) parent.getLayout();
            layout.show(parent, targetPanelName);

            resizeFrame(frame, width, height);
        }
    }

    public static void resizeFrame(JFrame frame, int width, int height) {
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }
}
