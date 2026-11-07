import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PrestamoDAO {

    public static void insertar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO prestamo (fecha_retiro, dni_socio, titulo_libro) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, p.getFechaRetiro().getTimeInMillis());
            ps.setInt(2, p.getSocio().getDNI());
            ps.setString(3, p.getLibro().getTitulo().trim());
            ps.executeUpdate();
        }
    }

    public static void registrarDevolucion(String titulo, Calendar fecha) throws SQLException {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? "
                    + "WHERE titulo_libro = ? AND fecha_devolucion IS NULL";
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, fecha.getTimeInMillis());
            ps.setString(2, titulo.trim());
            ps.executeUpdate();
        }
    }

    // Reconstruye los préstamos y los engancha a los socios y libros ya cargados
    public static void cargarTodos(ArrayList<Socio> socios, ArrayList<Libro> libros) throws SQLException {
        HashMap<Integer, Socio> porDni = new HashMap<>();
        for (Socio s : socios) porDni.put(s.getDNI(), s);

        HashMap<String, Libro> porTitulo = new HashMap<>();
        for (Libro l : libros) porTitulo.put(l.getTitulo().trim().toLowerCase(), l);

        String sql = "SELECT fecha_retiro, fecha_devolucion, dni_socio, titulo_libro "
                    + "FROM prestamo ORDER BY id";
        try (Connection con = ConexionDB.conectar();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Socio socio = porDni.get(rs.getInt("dni_socio"));
                Libro libro = porTitulo.get(rs.getString("titulo_libro").trim().toLowerCase());

                Calendar retiro = Calendar.getInstance();
                retiro.setTimeInMillis(rs.getLong("fecha_retiro"));

                Prestamo p = new Prestamo(retiro, socio, libro);
                libro.agregarPrestamo(p);
                socio.agregarPrestamo(p);

                long dev = rs.getLong("fecha_devolucion");
                if (!rs.wasNull()) {
                    Calendar devolucion = Calendar.getInstance();
                    devolucion.setTimeInMillis(dev);
                    p.registrarFechaDevolucion(devolucion);
                }
            }
        }
    }
}