import java.util.ArrayList;

/**
 * TP7, Clase que representa un libro perteneciente al catálogo de la biblioteca.
 * Administra la información bibliográfica del ejemplar y el historial 
 * de sus préstamos asociados
 * 
 * @author Aegerter Sebastián Gabriel
 * @version 1.0
 */
public class Libro {
    //atributos
    private String titulo;
    private int edicion;
    private String editorial;
    private int anio;
    private ArrayList<Prestamo> prestamos;

    /**
     * Constructor para inicializar un Libro con sus datos bibliográficos.
     * Inicializa la colección de préstamos vacía.
     * 
     * @param p_titulo Título del libro.
     * @param p_edicion Número de edición del libro.
     * @param p_editorial Firma o casa editorial del libro.
     * @param p_anio Año de publicación del libro.
     */
    public Libro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        this.setTitulo(p_titulo);
        this.setEdicion(p_edicion);
        this.setEditorial(p_editorial);
        this.setAnio(p_anio);
        this.setPrestamos(new ArrayList<Prestamo>());
    }

    /**
     * Asigna el título del libro.
     * @param p_titulo Título a registrar.
     */
    private void setTitulo(String p_titulo) {
        this.titulo = p_titulo;
    }

    /**
     * Obtiene el título del libro.
     * @return El título registrado.
     */
    public String getTitulo() {
        return this.titulo;
    }
    
    /**
     * Asigna el número de edición del libro.
     * @param p_edicion Número de edición a registrar.
     */
    private void setEdicion(int p_edicion) {
        this.edicion = p_edicion;
    }

    /**
     * Obtiene el número de edición del libro.
     * @return El número de edición.
     */
    public int getEdicion() {
        return this.edicion;
    }

    /**
     * Asigna la editorial del libro.
     * @param p_editorial Editorial a registrar.
     */
    private void setEditorial(String p_editorial) {
        this.editorial = p_editorial;
    }

    /**
     * Obtiene el nombre de la editorial del libro.
     * @return La casa editorial.
     */
    public String getEditorial() {
        return this.editorial;
    }

    /**
     * Asigna el año de publicación del libro.
     * @param p_anio Año a registrar.
     */
    private void setAnio(int p_anio) {
        this.anio = p_anio;
    }

    /**
     * Obtiene el año de publicación del libro.
     * @return El año de publicación.
     */
    public int getAnio() {
        return this.anio;
    }
    
    /**
     * Asigna la lista de préstamos del libro.
     * @param p_prestamos Colección de préstamos a asignar.
     */
    private void setPrestamos(ArrayList<Prestamo> p_prestamos) {
        this.prestamos = p_prestamos;
    }
    
    /**
     * Obtiene la colección completa de préstamos del libro.
     * @return Lista de préstamos registrados.
     */
    public ArrayList<Prestamo> getPrestamos() {
        return this.prestamos;
    }

    /**
     * Adiciona un nuevo préstamo a la lista del libro.
     * 
     * @param p_prestamo Objeto de tipo Prestamo a agregar.
     */
    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    /**
     * Retorna el último préstamo asentado sobre el libro (el más reciente).
     * 
     * @return El objeto Prestamo más reciente, o null si el libro nunca fue prestado.
     */
    public Prestamo ultimoPrestamo() {
        if (this.getPrestamos().isEmpty()) {
            return null;
        }
        return this.getPrestamos().get(this.getPrestamos().size() - 1);
    }

    /**
     * Evalúa si el libro se encuentra actualmente prestado.
     * Recorre el historial buscando algún préstamo sin fecha de devolución asentada.
     * 
     * @return true  si el libro está prestado (posee un préstamo activo sin devolver), 
     *         false si está disponible en la biblioteca.
     */
    public boolean prestado() {
        for (Prestamo p : this.getPrestamos()) {
            if (p.getFechaDevolucion() == null) {
                return true; // Encontró un préstamo sin devolucion, no se puede prestar el libro
            }
        }
        return false; // Todos los prestamos han sido devueltos, el libro se encuentra disponible
    }

    /**
     * Devuelve una representación en texto del libro en el formato oficial.
     * 
     * @return Cadena con la estructura: "Titulo: ".
     */

    public String toString() {
        return "Titulo: " + this.getTitulo();
    }
}