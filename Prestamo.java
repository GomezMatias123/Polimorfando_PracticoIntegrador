import java.util.Calendar;

public class Prestamo {

    //Atributos

    private Calendar fechaRetiro;
    private Calendar fechaDevolucion;
    private Socio socio;
    private Libro libro;

    //Metodos

    /**
     * 
     * Constructor de la clase Socio
     * 
    */
    public Prestamo(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {

        this.setFechaRetiro(p_fechaRetiro);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
        this.setFechaDevolucion(null);
    }

    /**
     * Recibe un valor Calendar que representa la fecha de devolucion
     * Envia el parametro al setter correspondiente
     * 
     * @param p_fecha
     * 
     */

    public void registrarFechaDevolucion(Calendar p_fecha) {

        this.setFechaDevolucion(p_fecha);

    }

    /**
     * Recibe una fecha la cual sera comparada con la fecha de vencimiento del prestamo.
     * 
     * @param p_fecha
     * @return retorna un valor bool que depende de si la fecha recibida es despues de la fecha de vencimiento
     * 
     */

    public boolean vencido(Calendar p_fecha) {

        Calendar fechaVencimiento = (Calendar) this.getFechaRetiro().clone();

        fechaVencimiento.add(Calendar.DAY_OF_MONTH, this.getSocio().getDiasPrestamo());


        
        return p_fecha.after(fechaVencimiento);

    }

    /**    
     * 
     * @return retorna un string con los datos solicitados
     * 
     */

    public String toString() {

        return "Retiro: " + this.getFechaRetiro() + "Devolucion: - " + (this.getFechaDevolucion() == null ? this.getFechaDevolucion() : " - ") + 
        this.getLibro().toString() + "Socio: " + this.getSocio().getNombre();

    }


    /**
     * Setters y getters correspondientes
     * 
     * @param p_fecha
     */

    private void setFechaRetiro(Calendar p_fecha) {

        this.fechaRetiro = p_fecha;

    }

    private void setFechaDevolucion(Calendar p_fecha) {

        this.fechaDevolucion = p_fecha;

    }

    private void setSocio(Socio p_socio) {

        this.socio = p_socio;

    }

    private void setLibro(Libro p_libro) {

        this.libro = p_libro;

    }

    public Calendar getFechaRetiro() {

        return this.fechaRetiro;

    }

    public Calendar getFechaDevolucion() {

        return this.fechaDevolucion;

    }

    public Socio  getSocio() {

        return this.socio;

    }

    public Libro getLibro() {

        return this.libro;

    }

} 