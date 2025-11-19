package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * App entry point for the Zoo Management System.
 *
 * Responsibilities:
 *  - Bootstraps the Swing UI on the Event Dispatch Thread
 *  - Leaves all UI and database work to JWindow and the database layer
 */
public final class ZooController {

    private ZooController() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        // slways start wwing apps on the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            // use nimbus, otherwise default
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // default swing UI
            }

            new UI.JWindow();
        });
    }
}
