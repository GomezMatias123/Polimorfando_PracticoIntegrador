import java.util.Calendar;

/**
 * Socio de tipo Docente.
 * Arranca con 5 dias de prestamo y no tiene limite de libros.
 * Si es responsable se le pueden ir sumando dias de prestamo.
 */
public class Docente extends Socio {
    private String area;

    public Docente(int p_dniSocio, String p_nombre, String p_area) {
        super(p_dniSocio, p_nombre, 5);
        this.setArea(p_area);
    }

    private void setArea(String p_area) {
        this.area = p_area;
    }

    public String getArea() {
        return this.area;
    }

    /**
     * Es responsable si nunca tuvo ni tiene un prestamo vencido.
     * Si el libro ya se devolvio, se compara contra la fecha de devolucion;
     * si todavia lo tiene, contra la fecha de hoy.
     */
    public boolean esResponsable() {
        Calendar hoy = Calendar.getInstance();
        for (Prestamo prestamo : this.getPrestamos()) {
            if (prestamo.getFechaDevolucion() != null) {
                if (prestamo.vencido(prestamo.getFechaDevolucion())) {
                    return false;
                }
            } else if (prestamo.vencido(hoy)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Suma dias de prestamo al docente, como premio a la responsabilidad.
     */
    public void cambiarDiasDePrestamo(int p_dias) {
        this.setDiasPrestamo(this.getDiasPrestamo() + p_dias);
    }

    /**
     * El docente no tiene limite de libros, solo se controla
     * que no tenga devoluciones vencidas.
     */
    public boolean puedePedir() {
        return super.puedePedir();
    }

    public String soyDeLaClase() {
        return "Docente";
    }
}
