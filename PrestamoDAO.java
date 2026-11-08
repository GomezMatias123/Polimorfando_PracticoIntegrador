import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

    /**
     * Acceso a datos de la tabla prestamo: guarda préstamos nuevos, registra devoluciones
     * y reconstruye los préstamos guardados enlazándolos con sus socios y libros.
     */
public class PrestamoDAO {

    /**
     * Guarda un préstamo nuevo en la base de datos, con su fecha de retiro, el D.N.I. del socio
     * y el título del libro. Los préstamos nuevos se guardan sin fecha de devolución.
     * 
     * @param p Préstamo que se guarda.
     * @throws SQLException si el socio o el libro no existen en la base, si el libro ya tiene un préstamo sin devolver, o si falla el acceso a la base de datos.
     */
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

    /**
     * Registra la fecha de devolución del préstamo abierto (sin devolver) de un libro.
     * Si el libro no tiene un préstamo abierto, no modifica nada.
     * 
     * @param titulo Título del libro que se devuelve.
     * @param fecha Fecha en que se devuelve el libro.
     * @throws SQLException si falla el acceso a la base de datos.
     */
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

    /**
     * Lee los préstamos guardados, en el orden en que se registraron, y los reconstruye
     * agregándolos al historial del socio y del libro correspondientes.
     * No valida reglas de préstamo ni usa la fecha actual: restaura el estado tal como
     * fue guardado. Los socios y los libros deben estar cargados antes de llamar a este método.
     * 
     * @param socios Lista de socios ya cargados, donde se busca el socio de cada préstamo.
     * @param libros Lista de libros ya cargados, donde se busca el libro de cada préstamo.
     * @throws SQLException si falla el acceso a la base de datos.
     */
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