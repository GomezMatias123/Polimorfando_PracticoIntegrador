import java.util.*;

public abstract class Socio {
    
    //atributos 

    private int dniSocio;
    private String nombre;
    private int diasPrestamo;
    private ArrayList<Prestamo> prestamos;

    //metodos

    /**
     * Constructores de la clase Socio, 
     * 
     * @param p_dni
     * @param p_nombre
     * @param p_diasPrestamo
     */

    public Socio(int p_dni, String p_nombre, int p_diasPrestamo) {

        this.setDNI(p_dni);
        this.setNombre(p_nombre);
        this.setDiasPrestamo(p_diasPrestamo);
        this.setPrestamos(new ArrayList<>());

    }

    /**
     * 
     * cuenta la cantidad de libros prestados
     * 
     * @return retorna el tamaño del arraylist que contiene los prestamos, asi retornando la cantidad de libros prestados
     */

    public int cantLibrosPrestados() {

        return this.getPrestamos().size();

    }

    /**   
     * 
     * @return retorna un string con los datos solicitados
     * 
     */

    public String toString() {

        return "D.N.I: " + this.getDNI() + " || " + this.getNombre() +
                " (" + this.soyDeLaClase() + ") || Libros Prestados: " + this.cantLibrosPrestados();

    }

    /**
     * 
     * revisa si el socio cumple las condiciones para pedir prestado un libro, en este caso, no tener prestamos vencidos
     * 
     * 
     * @return retorna un valor bool que depende de si tiene al menos un prestamo vencido
     */

    public boolean puedePedir() {

        Calendar fechaHoy = new GregorianCalendar();

        for(Prestamo p : this.getPrestamos()) {

            if(p.vencido(fechaHoy) == true) {

                return false;

            } 

        }
        
        return true;

    }

    /**
     * 
     * clase abstract que retornara un string segun la clase del socio
     * 
     * @return retorna un string que representa la clase a la que pertenece el socio
     * 
     */

    public abstract String soyDeLaClase();

    private void setDNI(int p_dni) {

        this.dniSocio = p_dni;

    }

    /**
     * 
     * setters y getters correspondientes de la clase Socio.
     * 
     */

    private void setNombre(String p_nombre) {

        this.nombre = p_nombre;

    }

    private void setDiasPrestamo(int p_diasPrestamo) {

        this.diasPrestamo = p_diasPrestamo;

    }

    private void setPrestamos(ArrayList<Prestamo> p_prestamos) {

        this.prestamos = p_prestamos;

    }

    public int getDNI() {

        return this.dniSocio;

    }

    public String getNombre() {

        return this.nombre;

    }

    public int getDiasPrestamo() {

        return this.diasPrestamo;

    }

    public ArrayList<Prestamo> getPrestamos() {

        return this.prestamos;

    }


    /**
     * Adiciona un nuevo préstamo a la lista del Socio.
     * 
     * @param p_prestamo Objeto de tipo Prestamo a agregar.
     */
    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

}
