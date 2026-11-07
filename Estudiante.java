/**
 * Socio de tipo Estudiante.
 * Tiene 20 dias de prestamo y no puede tener mas de 3 libros en su poder.
 */
public class Estudiante extends Socio {
    private String carrera;

    public Estudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        super(p_dniSocio, p_nombre, 20);
        this.setCarrera(p_carrera);
    }

    private void setCarrera(String p_carrera) {
        this.carrera = p_carrera;
    }

    public String getCarrera() {
        return this.carrera;
    }

    /**
     * El estudiante puede pedir si no tiene prestamos vencidos
     * y si no tiene mas de 3 libros en su poder.
     */
    public boolean puedePedir() {
        return super.puedePedir() && this.cantLibrosPrestados() <= 3;
    }

    public String soyDeLaClase() {
        return "Estudiante";
    }
}
