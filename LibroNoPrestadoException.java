/**
 * Excepción que se lanza cuando se intenta devolver un libro que no se encuentra prestado.
 * Es una excepción no verificada.
 */
public class LibroNoPrestadoException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje que describe el motivo del error.
     * 
     * @param mensaje Descripción del error.
     */
    public LibroNoPrestadoException(String mensaje) {
        super(mensaje); 
    }
}
