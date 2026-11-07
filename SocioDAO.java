import java.sql.*;
import java.util.ArrayList;

public class SocioDAO {

    public static void insertar(Socio s) throws SQLException {
        String sql = "INSERT INTO socio (dni_socio, nombre, tipo, carrera, area, dias_prestamo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getDNI());
            ps.setString(2, s.getNombre());

            if (s instanceof Docente d) {
                ps.setString(3, "DOCENTE");
                ps.setNull(4, Types.VARCHAR);
                ps.setString(5, d.getArea());
            } else if (s instanceof Estudiante e) {
                ps.setString(3, "ESTUDIANTE");
                ps.setString(4, e.getCarrera());
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setInt(6, s.getDiasPrestamo());
            ps.executeUpdate();
        }
    }

    public static void actualizarDiasPrestamo(int dni, int dias) throws SQLException {
        String sql = "UPDATE socio SET dias_prestamo = ? WHERE dni_socio = ?";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dias);
            ps.setInt(2, dni);
            ps.executeUpdate();
        }
    }

    public static ArrayList<Socio> cargarTodos() throws SQLException {
        ArrayList<Socio> socios = new ArrayList<>();
        String sql = "SELECT dni_socio, nombre, tipo, carrera, area, dias_prestamo FROM socio";
        try (Connection con = ConexionDB.conectar();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Socio s;
                    if (rs.getString("tipo").equals("DOCENTE")) {
                        s = new Docente(rs.getInt("dni_socio"), rs.getString("nombre"), rs.getString("area"));
                    } else {
                        s = new Estudiante(rs.getInt("dni_socio"), rs.getString("nombre"), rs.getString("carrera"));
                    }
                    s.setDiasPrestamo(rs.getInt("dias_prestamo")); // restaura el valor guardado
                    socios.add(s);
                }
        }
        return socios;
    }
}