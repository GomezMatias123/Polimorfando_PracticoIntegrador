import javax.swing.*;
import java.sql.SQLException;

/**
 * Punto de entrada de la interfaz gráfica de la biblioteca. Carga los datos guardados en la
 * base de datos y abre la ventana principal Es la alternativa gráfica a la clase GestionBiblioteca, que ofrece el mismo sistema por consola.
 */
public class InterfazBiblioteca {
    /**
     * 
     * Inicia la interfaz gráfica: carga los datos de la biblioteca desde la base de datos y,
     * si no hubo errores, muestra la ventana principal (se crea en el hilo de eventos de Swing en la clase VentanaBiblioteca).
     * Si no se puede abrir la base de datos, informa el error en un cuadro de diálogo
     * y el programa termina.
     * 
     */
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