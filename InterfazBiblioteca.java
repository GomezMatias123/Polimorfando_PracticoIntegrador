import javax.swing.*;
import java.sql.SQLException;

public class InterfazBiblioteca {
    public static void main(String[] args) {
        Biblioteca biblio = new Biblioteca("Biblioteca Central UNNE");
        try {
            biblio.cargarDesdeBD();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "No se pudo abrir la base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new VentanaBiblioteca(biblio).setVisible(true));
    }
}