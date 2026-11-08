import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
    /**
 * Maneja la conexión con la base de datos SQLite de la biblioteca (archivo biblioteca.db)
 * y la creación de sus tablas.
 */
public class ConexionDB {
    /** Ruta de la base de datos. Es relativa: el archivo se crea en la carpeta desde la que se ejecuta el programa. */ 
    private static final String URL = "jdbc:sqlite:biblioteca.db";

    /**
     * Abre una conexión con la base de datos y activa el control de claves foráneas,
     * que SQLite trae desactivado por defecto. Si el archivo no existe, se crea vacío.
     * Quien llama a este método es responsable de cerrar la conexión.
     * 
     * @return Conexión abierta con la base de datos.
     * @throws SQLException si no se puede abrir la conexión.
     */
    public static Connection conectar() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }
    
    /**
     * Crea las tablas socio, libro y prestamo si todavía no existen, si las tablas ya existen no se modifican ni se borran sus datos.
     * 
     * @throws SQLException si ocurre un error al crear las tablas.
     */
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