import java.sql.*;
import java.util.ArrayList;

    /**
     * Acceso a datos de la tabla libro: guarda libros nuevos y los lee de la base de datos.
     */
public class LibroDAO {

    /**
     * Guarda un libro nuevo en la base de datos. El título se guarda sin espacios al principio
     * ni al final y funciona como identificador, por lo que no puede repetirse
     * (sin distinguir mayúsculas de minúsculas).
     * 
     * @param l Libro que se guarda.
     * @throws SQLException si ya existe un libro con ese título o falla el acceso a la base de datos.
     */
    public static void insertar(Libro l) throws SQLException {
        String sql = "INSERT INTO libro (titulo, edicion, editorial, anio) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo().trim());
            ps.setInt(2, l.getEdicion());
            ps.setString(3, l.getEditorial());
            ps.setInt(4, l.getAnio());
            ps.executeUpdate();
        }
    }

    /**
     * Lee todos los libros guardados en la base de datos. Los libros se crean sin préstamos:
     * el historial se completa después en PrestamoDAO.cargarTodos().
     * 
     * @return {@code ArrayList<Libro>} con los libros guardados.
     * @throws SQLException si falla el acceso a la base de datos.
     */
    public static ArrayList<Libro> cargarTodos() throws SQLException {
        ArrayList<Libro> libros = new ArrayList<>();
        String sql = "SELECT titulo, edicion, editorial, anio FROM libro";
        try (Connection con = ConexionDB.conectar();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                libros.add(new Libro(rs.getString("titulo"), rs.getInt("edicion"),
                                    rs.getString("editorial"), rs.getInt("anio")));
            }
        }
        return libros;
    }
}
