import java.lang.reflect.Array;
import java.util.ArrayList;

public class Biblioteca {

//Atributos
private String nombre;
private ArrayList <Libro> libros;
private ArrayList <Socio> socios;


//Constructores

    /**
     * Constructor con parametros, instancia un objeto de la clase
     * @param p_nombre
     * @param p_libros
     * @param p_socios
     */
    public Biblioteca(String p_nombre, ArrayList <Libro> p_libros, ArrayList<Socio> p_socios){
    this.setNombre(p_nombre);
    this.setLibro(p_libros);
    this.setSocio(p_socios);
    }

    /**
     * Constructor con parametros, instancia un objeto de la clase
     * @param p_nombre
     */
    public Biblioteca(String p_nombre){
        this.setNombre(p_nombre);
        this.setLibro(new ArrayList<Libro>());
        this.setSocio(new ArrayList<Socio>());
    }
//metodos

    /**
     * recibe  un String y permite modificar el valor del atributo nombre
     * @param p_nombre
     */
  private void setNombre(String p_nombre){
     this.nombre = p_nombre;
  }

    /**
     * recibe  un ArrayList de Libro y permite modificar el valor del atributo libro
     * @param p_libro
     */
  private void setLibro(ArrayList<Libro> p_libro){
     this.libros = p_libro;
  }

    /**
     * recibe  un ArrayList de Socio y permite modificar el valor del atributo socio
     * @param p_socios
     */
  private void setSocio(ArrayList <Socio> p_socios){
     this.socios = p_socios;
  }

    /**
     * permite obtener el valor de nombre
     * @return nombre
     */
  public String getNombre() {
    return nombre;
  }

    /**
     * permite obtener el valor de libro
     * @return libros
     */
  public ArrayList<Libro> getLibros() {
     return libros;
  }
    /**
     * permite obtener el valor de socio
     * @return socios
     */
  public ArrayList<Socio> getSocios() {
    return socios;
  }
}