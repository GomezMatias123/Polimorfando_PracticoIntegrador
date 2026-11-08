import java.util.Calendar;
import java.util.Scanner;
import java.sql.SQLException;
/**
 * Clase ejecutable gestión biblioteca mediante menú por consola.
 */
public class GestionBiblioteca {

    public static void main(String[] args) {
        
        Biblioteca biblio = new Biblioteca("Biblioteca Central UNNE");
        try {
        biblio.cargarDesdeBD();
        } catch (SQLException e) {
            System.out.println("No se pudo abrir la base de datos: " + e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        do {
            System.out.println("\n==========================================");
            System.out.println("   SISTEMA DE GESTIÓN DE BIBLIOTECA");
            System.out.println("==========================================");
            System.out.println("1. Registrar nuevo Socio Estudiante");
            System.out.println("2. Registrar nuevo Socio Docente");
            System.out.println("3. Agregar nuevo Libro");
            System.out.println("4. Prestar Libro");
            System.out.println("5. Devolver Libro");
            System.out.println("6. Listar todos los Libros");
            System.out.println("7. Listar todos los Socios");
            System.out.println("8. Listar Títulos únicos");
            System.out.println("9. Consultar quién tiene un Libro");
            System.out.println("10. Listar Docentes Responsables");
            System.out.println("11. Consultar cantidad de Socios por tipo");
            System.out.println("0. Salir");
            System.out.println("------------------------------------------");
            System.out.print("Ingrese una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
        try{
            switch (opcion) {
                case 1:
                    System.out.print("Ingrese DNI del estudiante: ");
                    int dniEst = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ingrese Nombre y Apellido: ");
                    String nomEst = scanner.nextLine();
                    System.out.print("Ingrese Carrera: ");
                    String carrera = scanner.nextLine();
                    biblio.nuevoSocioEstudiante(dniEst, nomEst, carrera);
                    System.out.println(">> Socio Estudiante registrado con éxito.");
                    break;

                case 2:
                    System.out.print("Ingrese DNI del docente: ");
                    int dniDoc = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ingrese Nombre y Apellido: ");
                    String nomDoc = scanner.nextLine();
                    System.out.print("Ingrese Área/Departamento: ");
                    String area = scanner.nextLine();
                    biblio.nuevoSocioDocente(dniDoc, nomDoc, area);
                    System.out.println(">> Socio Docente registrado con éxito.");
                    break;

                case 3:
                    System.out.print("Ingrese Título del libro: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Ingrese Edición (número): ");
                    int edicion = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ingrese Editorial: ");
                    String editorial = scanner.nextLine();
                    System.out.print("Ingrese Año de publicación: ");
                    int anio = Integer.parseInt(scanner.nextLine());
                    biblio.nuevoLibro(titulo, edicion, editorial, anio);
                    System.out.println(">> Libro agregado con éxito.");
                    break;

                case 4:
                    System.out.print("Ingrese DNI del socio: ");
                    int dniPrestamo = Integer.parseInt(scanner.nextLine());
                    Socio socioEncontrado = biblio.buscarSocio(dniPrestamo);

                    if (socioEncontrado == null) {
                        System.out.println(">> Error: No existe un socio registrado con el DNI ingresado.");
                        break;
                    }

                    System.out.print("Ingrese el Título exacto del libro a prestar: ");
                    String tituloPrestamo = scanner.nextLine();
                    Libro libroEncontrado = buscarLibroPorTitulo(biblio, tituloPrestamo);

                    if (libroEncontrado == null) {
                        System.out.println(">> Error: No existe un libro con el título ingresado.");
                        break;
                    }

                    boolean prestadoOk = biblio.prestarLibro(Calendar.getInstance(), socioEncontrado, libroEncontrado);
                    if (prestadoOk) {
                        System.out.println(">> Préstamo realizado exitosamente.");
                    } else {
                        System.out.println(">> Error: No se pudo realizar el préstamo. El libro ya está prestado o el socio supera el límite / posee deudas.");
                    }
                    break;

                case 5:
                    System.out.print("Ingrese el Título exacto del libro a devolver: ");
                    String tituloDev = scanner.nextLine();
                    Libro libroDev = buscarLibroPorTitulo(biblio, tituloDev);

                    if (libroDev == null) {
                        System.out.println(">> Error: El libro especificado no existe en la biblioteca.");
                    } else {
                            biblio.devolverLibro(libroDev);
                    }
                    break;

                case 6:
                    System.out.println(biblio.listaDeLibros());
                    break;

                case 7:
                    System.out.println(biblio.listaDeSocios());
                    break;

                case 8:
                    System.out.println(biblio.listaDeTitulos());
                    break;

                case 9:
                    System.out.print("Ingrese el Título del libro a consultar: ");
                    String tituloCons = scanner.nextLine();
                    Libro libroCons = buscarLibroPorTitulo(biblio, tituloCons);

                    if (libroCons == null) {
                        System.out.println(">> Error: El libro no pertenece al catálogo.");
                    } else {
                            String tenedor = biblio.quienTieneElLibro(libroCons);
                            System.out.println(">> El libro '" + libroCons.getTitulo() + "' está en poder de: " + tenedor);
                    }
                    break;

                case 10:
                    System.out.println(biblio.listaDeDocentesResponsables());
                    break;

                case 11:
                    System.out.print("Ingrese el tipo de socio (Estudiante / Docente): ");
                    String tipoSocio = scanner.nextLine();
                    int cantidad = biblio.cantidadDeSociosPorTipo(tipoSocio);
                    System.out.println(">> Cantidad de socios de tipo " + tipoSocio + ": " + cantidad);
                    break;

                case 0:
                    System.out.println("Cerrando el sistema de biblioteca...");
                    break;

                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        } catch (SQLException e) {
    if (e.getMessage().contains("UNIQUE")) {
        System.out.println(">> Error: ya existe un registro con ese DNI o título.");
    } else {
        System.out.println(">> Error de base de datos: " + e.getMessage());
    }
} catch (LibroNoPrestadoException e) {
    System.out.println(">> Error: " + e.getMessage());
} catch (NumberFormatException e) {
    System.out.println(">> Error: debe ingresar un número válido.");
}

        } while (opcion != 0);

        scanner.close();
    }

    /**
     * Busca un libro por su título dentro de la biblioteca, sin distinguir mayúsculas
     * de minúsculas ni espacios al principio o al final.
     * 
     * @param biblio Biblioteca en la que se realiza la búsqueda.
     * @param tituloDev Título del libro que se busca.
     * @return El libro encontrado, o null si no existe ninguno con ese título.
     */
    public static Libro buscarLibroPorTitulo(Biblioteca biblio, String tituloDev){
        for(Libro l : biblio.getLibros()){
            if (l.getTitulo().trim().equalsIgnoreCase(tituloDev.trim())) {
                return l;
            }
        }
        return null;
    }
} 