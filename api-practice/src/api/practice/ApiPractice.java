package api.practice;

import api.practice.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del proyecto.
 * Inicializa la aplicación Swing sobre el Event Dispatch Thread (EDT).
 */
public class ApiPractice {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Look and feel nativo para una interfaz más familiar.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Si falla el look and feel, la app sigue con el predeterminado.
                System.err.println("No fue posible aplicar el look and feel del sistema: " + ex.getMessage());
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
