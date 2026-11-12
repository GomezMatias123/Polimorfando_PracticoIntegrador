import java.util.ArrayList;
import java.util.HashSet;
import java.util.Calendar;
import java.sql.SQLException;

public class Biblioteca {

    // Atributos
    private String nombre;
    private ArrayList<Libro> libros;
    private ArrayList<Socio> socios;

    // Constructores

    /**
     * Constructor con parametros, instancia un objeto de la clase
     * 
     * @param p_nombre
     * @param p_libros
     * @param p_socios
     */
    public Biblioteca(String p_nombre, ArrayList<Libro> p_libros, ArrayList<Socio> p_socios) {
        this.setNombre(p_nombre);
        this.setLibro(p_libros);
        this.setSocio(p_socios);
    }

    /**
     * Constructor con parametros, instancia un objeto de la clase
     * 
     * @param p_nombre
     */
    public Biblioteca(String p_nombre) {
        this.setNombre(p_nombre);
        this.setLibro(new ArrayList<Libro>());
        this.setSocio(new ArrayList<Socio>());
    }
    // metodos

    /**
     * recibe un String y permite modificar el valor del atributo nombre
     * 
     * @param p_nombre
     */
  private void setNombre(String p_nombre){
      this.nombre = p_nombre;
  }

    /**
     * recibe un ArrayList de Libro y permite modificar el valor del atributo libro
     * 
     * @param p_libro
     */
    private void setLibro(ArrayList<Libro> p_libro) {
        this.libros = p_libro;
    }

    /**
     * recibe un ArrayList de Socio y permite modificar el valor del atributo socio
     * 
     * @param p_socios
     */
    private void setSocio(ArrayList<Socio> p_socios) {
        this.socios = p_socios;
    }

    /**
     * permite obtener el valor de nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * permite obtener el valor de libro
     * 
     * @return libros
     */
    public ArrayList<Libro> getLibros() {
        return libros;
    }

    /**
     * permite obtener el valor de socio
     * 
     * @return socios
     */
    public ArrayList<Socio> getSocios() {
        return socios;
    }
    /**
     * Prepara la biblioteca al iniciar el programa: crea las tablas si no existen y
     * carga en memoria los socios, los libros y los préstamos guardados en la base de datos
     * Debe llamarse una sola vez, ya que agrega los datos a las listas existentes.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */

    public void cargarDesdeBD() throws SQLException {
    ConexionDB.crearTablas();
    this.getSocios().addAll(SocioDAO.cargarTodos());
    this.getLibros().addAll(LibroDAO.cargarTodos());
    PrestamoDAO.cargarTodos(this.getSocios(), this.getLibros());
    }
    /**
     * Devuelve una coleccion con los docentes responsables (aquellos que nunca
     * tuvieron ni tienen un prestamo vencido).
     * 
     * @return ArrayList<Docente> con los docentes responsables.
     */
    public ArrayList<Docente> docentesResponsables() {
        ArrayList<Docente> docentesResp = new ArrayList<Docente>();
        for (Socio unSocio : this.getSocios()) {
            if (unSocio.soyDeLaClase().equalsIgnoreCase("Docente")) {
                Docente unDocente = (Docente) unSocio;
                if (unDocente.esResponsable()) {
                    docentesResp.add(unDocente);
                }
            }
        }
        return docentesResp;
    }

    /**
     * Devuelve el nombre del Socio que tiene el libro pasado por parametro,
     * o indica si el libro se encuentra en la biblioteca.
     * 
     * @param p_libro Libro a consultar.
     * @return Nombre del socio que posee actualmente el libro, o mensaje si esta en la biblioteca.
     */
    public String quienTieneElLibro(Libro p_libro) {
        if (p_libro.prestado()) {
            return p_libro.ultimoPrestamo().getSocio().toString();
        } else {
            return "El libro se encuentra en la biblioteca";
        }
    }

    /**
     * Busca y devuelve el Socio que tiene el DNI pasado como parametro.
     * 
     * @param p_dni DNI del socio a buscar.
     * @return El objeto Socio encontrado, o null si no existe.
     */
    public Socio buscarSocio(int p_dni) {
        for (Socio unSocio : this.getSocios()) {
            if (unSocio.getDNI() == p_dni) {
                return unSocio;
            }
        }
        return null;
    }

    /**
     * Devuelve un String con la lista de socios segun el Formato 1 del enunciado.
     * 
     * @return Cadena formateada con el listado de socios y totales por tipo.
     */
    public String listaDeSocios() {
        String lista = "\tLista de Socios:\n\n";
        int i = 1;
        for (Socio unSocio : this.getSocios()) {
            lista += i + ") " + unSocio.toString() + "\n";
            i += 1;
        }
        lista = lista + "**************************************\n";
        lista = lista + "Cantidad de Socios del tipo Estudiante: " + this.cantidadDeSociosPorTipo("Estudiante") + "\n";
        lista = lista + "Cantidad de Socios del tipo Docente: " + this.cantidadDeSociosPorTipo("Docente") + "\n";
        lista = lista + "**************************************";
        return lista;
    }

    /**
     * Devuelve un String con la lista de los titulos con los que cuenta
     * la Biblioteca
     * 
     * @return Cadena con los titulos sin repetir.
     */
    public String listaDeTitulos() {
        String lista = "";
        HashSet<String> titulos = new HashSet<String>();
        for (Libro unLibro : this.getLibros()) {
            titulos.add(unLibro.toString());
        }
        for (String unTitulo : titulos) {
            lista = lista + unTitulo + "\n";
        }
        return lista;
    }

    /**
     * Devuelve un String con la lista de libros segun el Formato 2 del enunciado.
     * 
     * @return Cadena formateada con el listado de libros y su estado de prestamo.
     */
    public String listaDeLibros() {
        String lista = "\tLista de Libros:\n\n";
        int i = 1;
        for (Libro unLibro : this.getLibros()) {
            String estadoPrestamo;
            if (unLibro.prestado()) {
                estadoPrestamo = "Si";
            } else {
                estadoPrestamo = "No";
            }
            lista += i + ") " + unLibro.toString() + " || Prestado: (" + estadoPrestamo + ")\n";
            i += 1;
        }
        return lista;
    }

    /**
     * Devuelve un String con la lista de docentes responsables segun el Formato 3
     * del enunciado.
     * 
     * @return Cadena formateada con el listado de docentes responsables.
     */
    public String listaDeDocentesResponsables() {
        String lista = "\tLista de Docentes Responsables:\n\n";
        for (Docente unDocente : this.docentesResponsables()) {
            lista = lista + "* " + unDocente.toString() + "\n";
        }
        return lista;
    }

    /**
     * Registra un nuevo libro: lo guarda en la base de datos y luego lo agrega
     * a la lista de libros. Si el guardado falla, el libro no se agrega.
     * 
     * @param p_titulo Título del libro,
     * @param p_edicion Número de edición del libro.
     * @param p_editorial Editorial del libro.
     * @param p_anio Año de publicación del libro.
     * @throws SQLException si ya existe un libro con ese título o falla el acceso a la base de datos.
     */
public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) throws SQLException{
    Libro nuevoLibro = new Libro(p_titulo, p_edicion, p_editorial, p_anio);
    LibroDAO.insertar(nuevoLibro);
    this.getLibros().add(nuevoLibro);
}
    /**
     * Registra un nuevo socio de tipo Estudiante: lo guarda en la base de datos
     * y luego lo agrega a la lista de socios. Si el guardado falla, el socio no se agrega.
     * 
     * @param p_dniSocio D.N.I. del estudiante.
     * @param p_nombre Nombre y apellido del estudiante.
     * @param p_carrera Carrera que cursa el estudiante.
     * @throws SQLException si ya existe un socio con ese D.N.I. o falla el acceso a la base de datos.
     */
public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) throws SQLException{
    Socio nuevoSocio = new Estudiante(p_dniSocio, p_nombre, p_carrera);
    SocioDAO.insertar(nuevoSocio);
    this.getSocios().add(nuevoSocio);
}

    /**
     * Registra un nuevo socio de tipo Docente: lo guarda en la base de datos
     * y luego lo agrega a la lista de socios. Si el guardado falla, el socio no se agrega.
     * 
     * @param p_dniSocio D.N.I. del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_area Área o departamento al que pertenece el docente.
     * @throws SQLException si ya existe un socio con ese D.N.I. o falla el acceso a la base de datos.
     */
public void nuevoSocioDocente(int p_dniSocio,String p_nombre, String p_area) throws SQLException{
    Socio nuevoSocio = new Docente(p_dniSocio, p_nombre, p_area);
    SocioDAO.insertar(nuevoSocio);
    this.getSocios().add(nuevoSocio);
}

    /**
     * Registra el préstamo de un libro a un socio. Se concreta solo si el socio puede pedir
     * (no tiene préstamos vencidos ni supera su límite) y si el libro no está prestado.
     * Si se concreta, el préstamo se guarda en la base de datos y se agrega al historial
     * del libro y del socio.
     * 
     * @param p_fechaRetiro Fecha en que el socio retira el libro.
     * @param p_socio Socio que solicita el libro.
     * @param p_libro Libro que se presta.
     * @return true si el préstamo se realizó; false si el socio o el libro son null o si el socio no puede pedir o el libro ya se encuentra prestado.
     * @throws SQLException si falla el guardado en la base de datos.
     */
public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) throws SQLException{
    Prestamo nuevoPrestamo = null;
    Socio socioSolicitante = p_socio;
    Libro libroDPrestamo = p_libro;

    if (socioSolicitante == null || libroDPrestamo == null) {
        System.err.println("Ocurrio un error: asignado al socio o al libro solicitado");
        return false;
    }
        if(socioSolicitante.puedePedir(p_fechaRetiro) == true && libroDPrestamo.prestado()  == false){
            nuevoPrestamo = new Prestamo(p_fechaRetiro, socioSolicitante, libroDPrestamo);
            PrestamoDAO.insertar(nuevoPrestamo);  
            libroDPrestamo.agregarPrestamo(nuevoPrestamo);
            socioSolicitante.agregarPrestamo(nuevoPrestamo);
                return true;
        } else {
        return false;
        }
}

    /**
     * Registra la devolución de un libro prestado: asienta la fecha de hoy como fecha
     * de devolución de su último préstamo, tanto en la base de datos como en memoria.
     * Si el libro es null, informa el error por consola y no hace nada.
     * 
     * @param pLibro Libro que se devuelve.
     * @throws LibroNoPrestadoException si el libro no se encuentra prestado.
     * @throws SQLException si falla el guardado en la base de datos.
     */
    public void devolverLibro(Libro pLibro) throws SQLException{
    Libro libroDDevolucion = pLibro;
    
    if (libroDDevolucion == null) 
        {
        System.err.println("Error en asignacion del libro");
        return;
    }

    if (!libroDDevolucion.prestado()) {
    throw new LibroNoPrestadoException("El libro " + libroDDevolucion.toString() + " no se puede devolver ya que se encuentra en la biblioteca");
    }

    
    Calendar fechaHoy = Calendar.getInstance();
    int cantPrestamos = libroDDevolucion.getPrestamos().size() - 1;
    PrestamoDAO.registrarDevolucion(libroDDevolucion.getTitulo(), fechaHoy); 
    libroDDevolucion.getPrestamos().get(cantPrestamos).registrarFechaDevolucion(fechaHoy);
    }

    /**
     * Cuenta los socios de un tipo determinado (por ejemplo "Estudiante" o "Docente"),
     * 
     * @param p_objeto Tipo de socio que se quiere contar.
     * @return Cantidad de socios de ese tipo.
     */
    public int cantidadDeSociosPorTipo(String p_objeto){
        int cant = 0;
        for(Socio s : this.getSocios()){
            if (s.soyDeLaClase().trim().equalsIgnoreCase(p_objeto.trim())) {
                cant += 1;
            }
        }
        return cant;
    }

    /**
     * Devuelve una colección con los préstamos que están vencidos y todavía no fueron
     * devueltos, comparando contra la fecha actual.
     * 
     * @return ArrayList de Prestamo con los préstamos vencidos sin devolver.
     */

    public ArrayList <Prestamo> prestamosVencidos(){
        ArrayList <Prestamo> auxListaPrestamos = new ArrayList<>();
        Calendar fechaHoy = Calendar.getInstance();
        for(Socio s : this.getSocios()){
            for(Prestamo p : s.getPrestamos()){
                if (p.vencido(fechaHoy) && p.getFechaDevolucion() == null) {
                    auxListaPrestamos.add(p);
                }
            }
        }
        return auxListaPrestamos;
    }

    /**
     * Suma días de préstamo a un docente, solo si es responsable (nunca tuvo ni tiene un
     * préstamo vencido). El nuevo total se guarda primero en la base de datos y luego
     * se actualiza el docente en memoria.
     * 
     * @param p_docente Docente al que se le suman días.
     * @param p_dias Cantidad de días a sumar (debe ser mayor a cero).
     * @return true si se sumaron los días; false si el docente es null, p_dias no es positivo o el docente no es responsable.
     * @throws SQLException si falla el guardado en la base de datos.
     */
    public boolean cambiarDiasDocente(Docente p_docente, int p_dias) throws SQLException {
        if (p_docente == null || p_dias <= 0 || !p_docente.esResponsable()) {
            return false;
        }
        int nuevoTotal = p_docente.getDiasPrestamo() + p_dias;
        SocioDAO.actualizarDiasPrestamo(p_docente.getDNI(), nuevoTotal);   // primero la base
        p_docente.cambiarDiasDePrestamo(p_dias);                            // después la memoria
        return true;
    }
}
