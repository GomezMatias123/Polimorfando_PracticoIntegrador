import java.sql.*;
import java.util.ArrayList;

    /**
 * Acceso a datos de la tabla socio: guarda socios nuevos, actualiza sus días de préstamo
 * y los lee de la base de datos. Tanto los docentes como los estudiantes se guardan en la misma tabla.
 */
public class SocioDAO {

    /**
     * Guarda un socio nuevo en la base de datos. Según su clase se guarda el tipo
     * ("DOCENTE" o "ESTUDIANTE") y el dato propio: el área del docente o la carrera del estudiante.
     * El dato que no corresponde queda sin valor.
     * 
     * @param s Socio que se guarda (Docente o Estudiante).
     * @throws SQLException si ya existe un socio con ese D.N.I. o falla el acceso a la base de datos.
     */
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
    /**
     * Actualiza los días de préstamo de un socio en la base de datos.
     * 
     * @param dni D.N.I. del socio a actualizar.
     * @param dias Cantidad total de días de préstamo (no la cantidad que se suma).
     * @throws SQLException si falla el acceso a la base de datos.
     */
    public static void actualizarDiasPrestamo(int dni, int dias) throws SQLException {
        String sql = "UPDATE socio SET dias_prestamo = ? WHERE dni_socio = ?";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dias);
            ps.setInt(2, dni);
            ps.executeUpdate();
        }
    }


    /**
     * Lee todos los socios guardados en la base de datos y crea un Docente o un Estudiante
     * según su tipo. Restaura los días de préstamo guardados, que pueden ser distintos
     * del valor inicial de cada tipo. Los socios se crean sin préstamos: el historial se
     * completa después en PrestamoDAO.cargarTodos().
     * 
     * @return {@code ArrayList<Socio>} con los socios guardados.
     * @throws SQLException si falla el acceso a la base de datos.
     */
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
                    s.setDiasPrestamo(rs.getInt("dias_prestamo")); 
                    socios.add(s);
                }
        }
        return socios;
    }
}