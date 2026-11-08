<p align="center">
  <img src="img/Webp.net-resizeimage-1-300x300.png" alt="FaCENA - UNNE" width="220">
</p>

# Gestión de Biblioteca

**Programa:** Biblioteca Central UNNE  
**Trabajo Practico 7:** Integrador de Programación Orientada a Objetos `Gestion Biblioteca`
**Equipo:** Polimorfando.

## Herramientas

- **Java**, Lenguaje de programacion.
- **Swing**, incluido en el JDK, para la interfaz gráfica.
- **SQLite** con el driver JDBC `sqlite-jdbc 3.54.1.0` para la persistencia de los datos.
- **Git y GitHub** para el control de versiones.
- **Trello** para la organizacion en el flujo de trabajo.

## Enunciado del problema

Una biblioteca desea administrar los préstamos de sus libros.
Cada libro tiene una ficha de préstamos en donde se asientan todos los préstamos realizados del libro. Cada
socio tiene una lista de préstamos donde se asientan los préstamos en su poder. Los socios pueden ser
estudiantes o docentes. Un socio estudiante puede pedir prestado un libro sólo si no tiene más de 3 libros en su
poder y si no tiene ningún libro vencido (20 días después de la fecha de retiro). Un socio docente no tiene límite
en cuanto a la cantidad de libros prestados, pero solo puede pedir prestado un libro si no tiene alguna
devolución vencida (superados los días de préstamos que tiene cada docente, desde la fecha de retiro).
Inicialmente todos los docentes tienen 5 días de préstamo, pero si es responsable se le van agregando días de
préstamo, individualmente.
Al pedir un préstamo, éste se crea con fecha de retiro solamente; la biblioteca asienta el préstamo en la ficha del
libro y en la ficha del socio. Cuando se devuelve el libro, en el préstamo se indica la fecha de devolución.
La administración de la biblioteca necesita informes que indiquen:
• Qué cantidad de socios de un determinado tipo tienen (según un tipo de socio dado).
• Qué libros están prestados, cuyo plazo de devolución se encuentre vencido.
• Qué socio tiene en su poder determinado Libro.
• Cuáles son los Títulos que tiene la Biblioteca (Sin repetir, en caso de haber más de 1 ejemplar)
• Qué docentes devolvieron los libros siempre antes de su vencimiento (o el mismo día).

## Alcance de la solución

El programa implementa el diseño de clases del enunciado (`Biblioteca`, `Socio`, `Estudiante`,
`Docente`, `Libro` y `Prestamo`) y lo amplía con:

- **Persistencia en SQLite:** los socios, libros y préstamos se guardan en una base de datos y se
  recuperan al abrir el programa.
- **Interfaz gráfica (Swing)** Con un menú principal y tres ventanas:
  - **Libros:** Agregar libros, búsqueda por título, y préstamo, devolución y consulta de quién tiene el libro.
  - **Socios:** Registro de estudiantes y docentes, búsqueda por D.N.I. o nombre, filtro por tipo,
    conteo de socios por tipo y ampliación de días de préstamo para docentes responsables.
  - **Préstamos:** Préstamo eligiendo socio y libro, devolución desde la tabla.
- **Menú por consola** (`GestionBiblioteca`) con las operaciones y los listados del enunciado.

## Cómo ejecutarlo

### Requisitos

- **JDK**  Instalado y actualizado.
- El driver `lib/sqlite-jdbc-3.54.1.0.jar` el mismo ya se encuentra en el repositorio dentro de lib/.

La base de datos `biblioteca.db` se crea sola la primera vez que se ejecuta ya que la misma no se sube al repositorio. Para empezar de cero, cerrar el programa y borrar ese archivo.

### Con Visual Studio Code (o Cursor)

1. Clonar el repositorio y abrir la carpeta en VS Code.
2. Instalar la extensión **Extension Pack for Java**, **SQLite**, **SQLite Viewer**, **Code Runner**.
3. Abrir una terminal en la carpeta del proyecto y compilar:
4. Abrir Prefences: OPEN USER SETTINGS(JSON) y agregar al final:

"code-runner.executorMap": {`
       `"java": "cd $dir && javac -encoding UTF-8 -cp \".;lib/*\" *.java && java --enable-native-access=ALL-UNNAMED -cp \".;lib/*\" $fileNameWithoutExt"
    }

5. Activar la opcion `code-runner.runInTermina` de la extension instalada antes mencionada.

Para la **Interfaz Grafica** ejecuntar `InterfarBiblioteca` y para la interfaz por consola ejecutar `GestionBibliotcea`.

### Con BlueJ

Se necesita **BlueJ 5 o superior** (probado con la versión 6.0).

1. **Project → Open Non BlueJ...** y elegir la carpeta del proyecto.
2. Agregar el driver de SQLite: **Tools → Preferences → Libraries → Add File...** y elegir
   `lib/sqlite-jdbc-3.54.1.0.jar`. Luego **reiniciar BlueJ**.
3. Apretar **Compile**.
4. Clic derecho sobre la clase y elegir `void main(String[] args)`:
   - **`InterfazBiblioteca`** para la interfaz gráfica.
   - **`GestionBiblioteca`** para el menú por consola (se usa en la ventana Terminal de BlueJ).

## Estructura del repositorio

-polimorfando_practicointegrador/
    ├── img/
    │       └── facena-unne-1024x320.png     
    ├── lib/
    │   └── sqlite-jdbc-3.54.1.0.jar 
    ├── README.md
    ├── Biblioteca.java
    ├── ConexionDB.java
    ├── Docente.java
    ├── Estudiante.java
    ├── Ficha de evalaución del trabajo en equipo.docx
    ├── GestionBiblioteca.java
    ├── InterfazBiblioteca.java
    ├── Libro.java
    ├── LibroDAO.java
    ├── LibroNoPrestadoException.java
    ├── Prestamo.java
    ├── PrestamoDAO.java
    ├── Socio.java
    ├── SocioDAO.java
    └── VentanaBiblioteca.java

## Enlaces

- **Repositorio:** https://github.com/GomezMatias123/Polimorfando_PracticoIntegrador
- **Trello:** https://trello.com/invite/b/6aea59b0a16cfb55edae40f4/ATTI255ec56b6af54e10f8166eb3e7076b3aA1C27A65/polimorfandopracticointegrador

## Integrantes

| Integrante | D.N.I. | GitHub |
|---|---|---|
| Aegerter Sebastián Gabriel | 46786457 | https://github.com/SebaAeg |
| Baez Zimerman Victor Hugo | 45373286 | https://github.com/SebaAeg |
| Gabaldo Zdanovicz Matias Nahuel | 46515453 | https://github.com/zeedthegreat |
| Acosta Gonzalo David | 46144799 | https://github.com/gonzalodavidacosta |
| Fernandez Fabio Javier | 46382663 | https://github.com/fernandezfabiojavier-dot |
| Gomez Matias Nicolas | 41949991 | https://github.com/GomezMatias123 |