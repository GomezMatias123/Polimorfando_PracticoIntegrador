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

public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) throws SQLException{
    Libro nuevoLibro = new Libro(p_titulo, p_edicion, p_editorial, p_anio);
    LibroDAO.insertar(nuevoLibro);
    this.getLibros().add(nuevoLibro);
}

public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) throws SQLException{
    Socio nuevoSocio = new Estudiante(p_dniSocio, p_nombre, p_carrera);
    SocioDAO.insertar(nuevoSocio);
    this.getSocios().add(nuevoSocio);
}

public void nuevoSocioDocente(int p_dniSocio,String p_nombre, String p_area) throws SQLException{
    Socio nuevoSocio = new Docente(p_dniSocio, p_nombre, p_area);
    SocioDAO.insertar(nuevoSocio);
    this.getSocios().add(nuevoSocio);
}

public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) throws SQLException{
    Prestamo nuevoPrestamo = null;
    Socio socioSolicitante = p_socio;
    Libro libroDPrestamo = p_libro;

    if (socioSolicitante == null || libroDPrestamo == null) {
        System.err.println("Ocurrio un error: asignado al socio o al libro solicitado");
        return false;
    }
        if(socioSolicitante.puedePedir() == true && libroDPrestamo.prestado()  == false){
            nuevoPrestamo = new Prestamo(p_fechaRetiro, socioSolicitante, libroDPrestamo);
            PrestamoDAO.insertar(nuevoPrestamo);  
            libroDPrestamo.agregarPrestamo(nuevoPrestamo);
            socioSolicitante.agregarPrestamo(nuevoPrestamo);
                return true;
        } else {
        return false;
        }
}

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

    public int cantidadDeSociosPorTipo(String p_objeto){
        int cant = 0;
        for(Socio s : this.getSocios()){
            if (s.soyDeLaClase().trim().equalsIgnoreCase(p_objeto.trim())) {
                cant += 1;
            }
        }
        return cant;
    }

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
}
