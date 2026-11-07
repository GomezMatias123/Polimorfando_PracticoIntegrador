import java.sql.*;
import java.util.ArrayList;

public class LibroDAO {

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
