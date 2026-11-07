import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:biblioteca.db";

    public static Connection conectar() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }
    
    public static void crearTablas() throws SQLException {
    try (Connection con = conectar(); Statement st = con.createStatement()) {
        st.execute("""
            CREATE TABLE IF NOT EXISTS socio (
                dni_socio      INTEGER PRIMARY KEY,
                nombre        TEXT NOT NULL,
                tipo          TEXT NOT NULL CHECK (tipo IN ('ESTUDIANTE','DOCENTE')),
                carrera       TEXT,
                area          TEXT,
                dias_prestamo INTEGER NOT NULL
            )""");

        st.execute("""
            CREATE TABLE IF NOT EXISTS libro (
                titulo    TEXT PRIMARY KEY COLLATE NOCASE,
                edicion   INTEGER,
                editorial TEXT,
                anio      INTEGER
            )""");

        st.execute("""
            CREATE TABLE IF NOT EXISTS prestamo (
                id               INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_retiro     INTEGER NOT NULL,
                fecha_devolucion INTEGER,
                dni_socio        INTEGER NOT NULL REFERENCES socio(dni_socio),
                titulo_libro     TEXT NOT NULL REFERENCES libro(titulo)
            )""");

        st.execute("""
            CREATE UNIQUE INDEX IF NOT EXISTS ux_prestamo_abierto
    ON prestamo(titulo_libro) WHERE fecha_devolucion IS NULL""");
    }
}
}