import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;
import javax.swing.table.JTableHeader;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.List;

    /**
     * Ventana principal de la interfaz gráfica de la biblioteca. Muestra un menú inicial con el
     * logo de la universidad y tres pantallas (Libros, Socios y Préstamos) entre las que se navega
     * con botones: solo una está visible a la vez. Todos los datos se leen y se modifican a través d
     * e la clase Biblioteca, que se encarga de guardarlos en la base de datos.
     */
public class VentanaBiblioteca extends JFrame {

    private final Biblioteca biblio;
    private final DefaultTableModel modeloLibros =
        new DefaultTableModel(new String[]{"Título", "Edición", "Editorial", "Año", "Prestado"}, 0) {
            public boolean isCellEditable(int fila, int col) { return false; }
        };
    private final JTable tablaLibros = new JTable(modeloLibros);
    private final JTextField txtBuscarLibro = new JTextField();
    private final DefaultTableModel modeloSocios =
        new DefaultTableModel(new String[]{"DNI", "Nombre", "Tipo", "Carrera / Área", "Días de préstamo"}, 0) {
            public boolean isCellEditable(int fila, int col) { return false; }
    };
    private final JTable tablaSocios = new JTable(modeloSocios);
    private final JLabel lblConteoSocios = new JLabel();
    private final TableRowSorter<DefaultTableModel> filtroSocios = new TableRowSorter<>(modeloSocios);
    private final JTextField txtBuscarSocio = new JTextField();
    private final JComboBox<String> cmbTipoSocio = new JComboBox<>(new String[]{"Todos", "Estudiante", "Docente"});
    private final JComboBox<Socio> cmbSocios = new JComboBox<>();
    private final JComboBox<Libro> cmbLibros = new JComboBox<>();
    private final DefaultTableModel modeloPrestamos =
        new DefaultTableModel(new String[]{"Libro", "Socio", "Retiro", "Devolución", "Vencido"}, 0) {
            public boolean isCellEditable(int fila, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                return (col == 2 || col == 3) ? Calendar.class : Object.class;
            }
        };
        private final JTable tablaPrestamos = new JTable(modeloPrestamos);
        private final ArrayList<Prestamo> filasPrestamos = new ArrayList<>();
        private final CardLayout cartas = new CardLayout();     
        private final JPanel contenedor = new JPanel(cartas);    

    /**
     * Crea la ventana: arma el menú y las tres pantallas, muestra el menú al abrir
     * y carga en las tablas los datos que ya tiene la biblioteca.
     * 
     * @param biblio Biblioteca con la que trabaja la interfaz (con sus datos ya cargados).
     */
    public VentanaBiblioteca(Biblioteca biblio) {
        super(biblio.getNombre());
        this.biblio = biblio;

        contenedor.add(panelMenu(), "menu");                          
        contenedor.add(conEncabezado("Libros", panelLibros()), "libros");    
        contenedor.add(conEncabezado("Socios", panelSocios()), "socios");     
        contenedor.add(conEncabezado("Préstamos", panelPrestamos()), "prestamos");
        add(contenedor);                                              
        cartas.show(contenedor, "menu");                              

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        refrescarLibros();
        refrescarSocios();
        refrescarPrestamos(); 
    }

    /**
     * Arma la pantalla de Libros: una barra de búsqueda por título arriba, la tabla de libros
     * en el centro y los botones Agregar libro, Prestar, Devolver y ¿Quién lo tiene? abajo.
     * 
     * @return Panel con la pantalla de Libros.
     */

    private JPanel panelLibros() {
    JPanel panel = new JPanel(new BorderLayout(8, 12));
    panel.setBackground(AZUL);                                              
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));


    JLabel lblBuscar = new JLabel("Buscar por título:");
    lblBuscar.setFont(FUENTE_NEGRITA);
    lblBuscar.setForeground(Color.WHITE);
    txtBuscarLibro.setFont(FUENTE);
    txtBuscarLibro.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DORADO, 2),
        BorderFactory.createEmptyBorder(4, 8, 4, 8)));

    JPanel busqueda = new JPanel(new BorderLayout(8, 0));
    busqueda.setOpaque(false);                                              
    busqueda.add(lblBuscar, BorderLayout.WEST);
    busqueda.add(txtBuscarLibro, BorderLayout.CENTER);
    panel.add(busqueda, BorderLayout.NORTH);


    TableRowSorter<DefaultTableModel> filtro = new TableRowSorter<>(modeloLibros);
    tablaLibros.setRowSorter(filtro);
    tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    estilizarTabla(tablaLibros);                                            
    txtBuscarLibro.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e)  { aplicarFiltroLibros(filtro); }
        public void removeUpdate(DocumentEvent e)  { aplicarFiltroLibros(filtro); }
        public void changedUpdate(DocumentEvent e) { aplicarFiltroLibros(filtro); }
    });

    JScrollPane scroll = new JScrollPane(tablaLibros);
    scroll.setBorder(BorderFactory.createLineBorder(DORADO, 2));            
    scroll.getViewport().setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);

    
    JButton btnAgregar = new JButton("Agregar libro");
    btnAgregar.addActionListener(e -> agregarLibro());
    JButton btnPrestar = new JButton("Prestar");
    btnPrestar.addActionListener(e -> prestarDesdeLibros());
    JButton btnDevolver = new JButton("Devolver");
    btnDevolver.addActionListener(e -> devolverDesdeLibros());
    JButton btnQuien = new JButton("¿Quién lo tiene?");
    btnQuien.addActionListener(e -> quienTieneDesdeLibros());

    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    botones.setOpaque(false);
    for (JButton b : new JButton[]{btnAgregar, btnPrestar, btnDevolver, btnQuien}) {
        estilizarBoton(b);                                                 
        botones.add(b);
    }
    panel.add(botones, BorderLayout.SOUTH);
    return panel;
}

    /**
     * Filtra la tabla de libros según el texto de la barra de búsqueda: deja visibles los libros
     * cuyo título contiene el texto, sin distinguir mayúsculas de minúsculas.
     * Si la barra está vacía, muestra todos los libros.
     * 
     * @param filtro Ordenador de la tabla de libros sobre el que se aplica el filtro.
     */
private void aplicarFiltroLibros(TableRowSorter<DefaultTableModel> filtro) {
    String texto = txtBuscarLibro.getText().trim();
    if (texto.isEmpty()) {
        filtro.setRowFilter(null);   
    } else {
        
        filtro.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(texto), 0));
    }
}

    /**
     * Vuelve a cargar la tabla de libros con los datos actuales de la biblioteca, indicando si
     * cada uno está prestado, y actualiza los desplegables de la pantalla de Préstamos.
     */
    private void refrescarLibros() {
        modeloLibros.setRowCount(0);
        for (Libro l : biblio.getLibros()) {
            modeloLibros.addRow(new Object[]{
                l.getTitulo(), l.getEdicion(), l.getEditorial(), l.getAnio(),
                l.prestado() ? "Sí" : "No"
            });
        }
        refrescarCombos();
    }

    /**
     * Pide los datos de un libro nuevo en un formulario y lo registra en la biblioteca.
     * Valida que el título no esté vacío y que la edición y el año sean números.
     * Si ya existe un libro con ese título o falla la base de datos, informa el error en pantalla.
     */
    private void agregarLibro() {
        JTextField titulo = new JTextField();
        JTextField edicion = new JTextField();
        JTextField editorial = new JTextField();
        JTextField anio = new JTextField();
        Object[] campos = {"Título:", titulo, "Edición:", edicion,
                            "Editorial:", editorial, "Año:", anio};

        int r = JOptionPane.showConfirmDialog(this, campos, "Nuevo libro",
                                                JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        if (titulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título no puede estar vacío.",
                                            "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            biblio.nuevoLibro(titulo.getText().trim(),
                                Integer.parseInt(edicion.getText().trim()),
                                editorial.getText().trim(),
                                Integer.parseInt(anio.getText().trim()));
                refrescarLibros();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Edición y año deben ser números.",
                                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            String msg = (ex.getMessage() != null && ex.getMessage().contains("UNIQUE"))
                    ? "Ya existe un libro con ese título."
                    : "Error de base de datos: " + ex.getMessage();
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Arma la pantalla de Socios: la barra de búsqueda y el filtro por tipo arriba, la tabla de
     * socios en el centro y abajo los botones para agregar estudiantes y docentes, ampliar los
     * días de préstamo de un docente y el conteo de socios por tipo.
     * 
     * @return Panel con la pantalla de Socios.
     */
    private JPanel panelSocios() {
    JPanel panel = new JPanel(new BorderLayout(8, 12));
    panel.setBackground(AZUL);
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    panel.add(panelBusquedaSocios(), BorderLayout.NORTH);

    tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    estilizarTabla(tablaSocios);
    tablaSocios.setRowSorter(filtroSocios);
    JScrollPane scroll = new JScrollPane(tablaSocios);
    scroll.setBorder(BorderFactory.createLineBorder(DORADO, 2));
    scroll.getViewport().setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);

    
    JButton btnEst = new JButton("Agregar estudiante");
    btnEst.addActionListener(e -> agregarSocio(true));
    JButton btnDoc = new JButton("Agregar docente");
    btnDoc.addActionListener(e -> agregarSocio(false));
    JButton btnDias = new JButton("Ampliar días de préstamo");
    btnDias.addActionListener(e -> ampliarDiasDocente());

    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    botones.setOpaque(false);
    for (JButton b : new JButton[]{btnEst, btnDoc, btnDias}) {
        estilizarBoton(b);
        botones.add(b);
    }

    lblConteoSocios.setFont(FUENTE_NEGRITA);
    lblConteoSocios.setForeground(DORADO);

    JPanel abajo = new JPanel(new BorderLayout());
    abajo.setOpaque(false);
    abajo.add(botones, BorderLayout.WEST);
    abajo.add(lblConteoSocios, BorderLayout.EAST);     
    panel.add(abajo, BorderLayout.SOUTH);
    return panel;
}

    /**
     * Vuelve a cargar la tabla de socios con los datos actuales de la biblioteca (con la carrera
     * o el área según el tipo), actualiza el conteo de estudiantes y docentes y los desplegables
     * de la pantalla de Préstamos.
     */
private void refrescarSocios() {
    modeloSocios.setRowCount(0);
    for (Socio s : biblio.getSocios()) {
        String extra = "";
        if (s instanceof Estudiante e) extra = e.getCarrera();
        else if (s instanceof Docente d) extra = d.getArea();
        modeloSocios.addRow(new Object[]{
            s.getDNI(), s.getNombre(), s.soyDeLaClase(), extra, s.getDiasPrestamo()
        });
    }
    lblConteoSocios.setText("Estudiantes: " + biblio.cantidadDeSociosPorTipo("Estudiante")
        + "   |   Docentes: " + biblio.cantidadDeSociosPorTipo("Docente"));
    refrescarCombos();
}

    /**
     * Pide los datos de un socio nuevo en un formulario y lo registra en la biblioteca.
     * Valida que el nombre y la carrera o el área no estén vacíos y que el D.N.I. sea un número.
     * Si ya existe un socio con ese D.N.I. o falla la base de datos, informa el error en pantalla.
     * 
     * @param esEstudiante true para registrar un estudiante (pide la carrera); false para
     *        registrar un docente (pide el área o departamento).
     */
private void agregarSocio(boolean esEstudiante) {
    JTextField dni = new JTextField();
    JTextField nombre = new JTextField();
    JTextField extra = new JTextField();
    Object[] campos = {"DNI:", dni, "Nombre y apellido:", nombre,
                        esEstudiante ? "Carrera:" : "Área / Departamento:", extra};

    int r = JOptionPane.showConfirmDialog(this, campos,
            esEstudiante ? "Nuevo estudiante" : "Nuevo docente",
            JOptionPane.OK_CANCEL_OPTION);
    if (r != JOptionPane.OK_OPTION) return;

    if (nombre.getText().trim().isEmpty() || extra.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Completá todos los campos.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }
    try {
        int d = Integer.parseInt(dni.getText().trim());
        if (esEstudiante) {
            biblio.nuevoSocioEstudiante(d, nombre.getText().trim(), extra.getText().trim());
        } else {
            biblio.nuevoSocioDocente(d, nombre.getText().trim(), extra.getText().trim());
        }
        refrescarSocios();
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "El DNI debe ser un número.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
        String msg = (ex.getMessage() != null && ex.getMessage().contains("UNIQUE"))
                ? "Ya existe un socio con ese DNI."
                : "Error de base de datos: " + ex.getMessage();
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * Actualiza los desplegables de socios y libros de la pantalla de Préstamos con los datos
     * actuales de la biblioteca, conservando lo que estaba seleccionado si todavía existe.
     */
private void refrescarCombos() {
    Object selSocio = cmbSocios.getSelectedItem();
    Object selLibro = cmbLibros.getSelectedItem();
    cmbSocios.removeAllItems();
    for (Socio s : biblio.getSocios()) cmbSocios.addItem(s);
    cmbLibros.removeAllItems();
    for (Libro l : biblio.getLibros()) cmbLibros.addItem(l);
    if (selSocio != null) cmbSocios.setSelectedItem(selSocio);
    if (selLibro != null) cmbLibros.setSelectedItem(selLibro);
}

    /**
     * Arma la pantalla de Préstamos: los desplegables de socio y libro arriba, la tabla de
     * préstamos en el centro (con los pendientes y vencidos primero) y abajo los botones
     * Prestar, Devolver y ¿Quién tiene el libro?.
     * 
     * @return Panel con la pantalla de Préstamos.
     */
private JPanel panelPrestamos() {
    JPanel panel = new JPanel(new BorderLayout(8, 12));
    panel.setBackground(AZUL);
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));


    JLabel lblSocio = new JLabel("Socio:");
    JLabel lblLibro = new JLabel("Libro:");
    for (JLabel l : new JLabel[]{lblSocio, lblLibro}) {
        l.setFont(FUENTE_NEGRITA);
        l.setForeground(Color.WHITE);
    }
    cmbSocios.setFont(FUENTE);
    cmbSocios.setRenderer(new RenderCombo());
    cmbLibros.setFont(FUENTE);
    cmbLibros.setRenderer(new RenderCombo());

    JPanel arriba = new JPanel(new GridLayout(2, 2, 12, 4));
    arriba.setOpaque(false);
    arriba.add(lblSocio);
    arriba.add(lblLibro);
    arriba.add(cmbSocios);
    arriba.add(cmbLibros);
    panel.add(arriba, BorderLayout.NORTH);


    tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    estilizarTabla(tablaPrestamos);

    tablaPrestamos.setDefaultRenderer(Object.class, new RenderPrestamos());

TableRowSorter<DefaultTableModel> orden = new TableRowSorter<>(modeloPrestamos);
tablaPrestamos.setRowSorter(orden);
orden.setSortKeys(List.of(
    new RowSorter.SortKey(3, SortOrder.ASCENDING),    
    new RowSorter.SortKey(4, SortOrder.DESCENDING))); 

    JScrollPane scroll = new JScrollPane(tablaPrestamos);
    scroll.setBorder(BorderFactory.createLineBorder(DORADO, 2));
    scroll.getViewport().setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);


    JButton btnPrestar = new JButton("Prestar");
    btnPrestar.addActionListener(e -> prestar());
    JButton btnDevolver = new JButton("Devolver");
    btnDevolver.addActionListener(e -> devolver());
    JButton btnQuien = new JButton("¿Quién tiene el libro?");
    btnQuien.addActionListener(e -> quienTiene());

    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    botones.setOpaque(false);
    for (JButton b : new JButton[]{btnPrestar, btnDevolver, btnQuien}) {
        estilizarBoton(b);
        botones.add(b);
    }

    JLabel pista = new JLabel("Para devolver, seleccioná el préstamo en la tabla");
    pista.setFont(FUENTE);
    pista.setForeground(DORADO);

    JPanel abajo = new JPanel(new BorderLayout());
    abajo.setOpaque(false);
    abajo.add(botones, BorderLayout.WEST);
    abajo.add(pista, BorderLayout.EAST);
    panel.add(abajo, BorderLayout.SOUTH);
    return panel;
}

    /**
     * Vuelve a cargar la tabla de préstamos con el historial de todos los libros, indicando si
     * cada préstamo está vencido. Guarda cada préstamo en una lista paralela a las filas para poder
     * identificar el de la fila seleccionada, y actualiza también la tabla de libros.
     */
private void refrescarPrestamos() {
    Calendar hoy = Calendar.getInstance();
    modeloPrestamos.setRowCount(0);
    filasPrestamos.clear();
    for (Libro l : biblio.getLibros()) {
        for (Prestamo p : l.getPrestamos()) {
            Calendar dev = p.getFechaDevolucion();
            boolean vencido = p.vencido(dev != null ? dev : hoy);
            modeloPrestamos.addRow(new Object[]{
                l.getTitulo(),
                p.getSocio().getNombre(),
                p.getFechaRetiro(),          
                dev,                         
                vencido ? "Sí" : "No"
            });
            filasPrestamos.add(p);           
        }
    }
    refrescarLibros();
}

    /**
     * Presta el libro elegido en el desplegable de libros al socio elegido en el desplegable
     * de socios. Si falta elegir alguno de los dos, avisa en pantalla.
     */
private void prestar() {
    Socio s = (Socio) cmbSocios.getSelectedItem();
    Libro l = (Libro) cmbLibros.getSelectedItem();
    if (s == null || l == null) {
        JOptionPane.showMessageDialog(this, "Elegí un socio y un libro.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }
    realizarPrestamo(s, l);
}

    /**
     * Registra la devolución del préstamo seleccionado en la tabla de préstamos, previa confirmación.
     * Avisa si no hay ninguna fila seleccionada o si el préstamo ya fue devuelto.
     */
private void devolver() {
    int fila = tablaPrestamos.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccioná en la tabla el préstamo que se devuelve.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Prestamo p = filasPrestamos.get(tablaPrestamos.convertRowIndexToModel(fila));

    if (p.getFechaDevolucion() != null) {
        JOptionPane.showMessageDialog(this, "Ese préstamo ya fue devuelto.",
                "Préstamo ya devuelto", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    int ok = JOptionPane.showConfirmDialog(this,
            "¿Registrar la devolución de \"" + p.getLibro().getTitulo()
            + "\" por " + p.getSocio().getNombre() + "?",
            "Confirmar devolución", JOptionPane.YES_NO_OPTION);
    if (ok == JOptionPane.YES_OPTION) realizarDevolucion(p.getLibro());
}

    /**
     * Muestra quién tiene el libro elegido en el desplegable de libros.
     */
private void quienTiene() {
    Libro l = (Libro) cmbLibros.getSelectedItem();
    if (l == null) return;
    mostrarQuienTiene(l);
}


    /**
     * Muestra una de las pantallas de la ventana (el menú, Libros, Socios o Préstamos).
     * 
     * @param nombre Nombre de la pantalla: "menu", "libros", "socios" o "prestamos".
     */
private void irA(String nombre) {
    cartas.show(contenedor, nombre);
}

    /**
     * Arma el menú principal: un encabezado con el logo y el nombre de la biblioteca y, debajo,
     * los botones para ir a las pantallas de Libros, Socios y Préstamos.
     * 
     * @return Panel con el menú principal.
     */
private JPanel panelMenu() {
    
    JPanel encabezado = new JPanel();
    encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
    encabezado.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 6, 0, DORADO),
        BorderFactory.createEmptyBorder(20, 0, 20, 0)));
    encabezado.setBackground(Color.WHITE);

    JLabel logo = etiquetaLogo(400);
    logo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel titulo = new JLabel(biblio.getNombre());
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titulo.setForeground(ROJO);                        
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    encabezado.add(logo);
    encabezado.add(Box.createVerticalStrut(15));
    encabezado.add(titulo);

    
    JPanel botones = new JPanel(new GridLayout(3, 1, 0, 12));
    botones.setOpaque(false);                         
    botones.add(botonMenu("Libros", "libros"));
    botones.add(botonMenu("Socios", "socios"));
    botones.add(botonMenu("Préstamos", "prestamos"));

    JPanel centro = new JPanel(new GridBagLayout());
    centro.setBackground(AZUL);                       
    centro.add(botones);

    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(encabezado, BorderLayout.NORTH);
    panel.add(centro, BorderLayout.CENTER);
    return panel;
}

    /**
     * Crea un botón del menú principal que, al hacer clic, muestra la pantalla indicada.
     * 
     * @param texto Texto que se ve en el botón.
     * @param destino Nombre de la pantalla a la que lleva el botón.
     * @return Botón con el estilo del menú.
     */
private JButton botonMenu(String texto, String destino) {
    JButton b = new JButton(texto);
    b.setPreferredSize(new Dimension(280, 48));   
    b.addActionListener(e -> irA(destino));
    b.setBackground(ROJO);
    b.setForeground(DORADO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 16));
    b.setFocusPainted(false);                          
    b.setBorder(BorderFactory.createLineBorder(DORADO, 2));
    return b;
}


/** Ruta del logo del menú, relativa a la carpeta desde la que se ejecuta el programa. */
private static final String RUTA_LOGO = "img/facena-unne-1024x320.png";
/** Rojo oscuro del logo: botones, títulos y encabezados de tablas. */
private static final Color ROJO   = new Color(0x7B1C1F);
/** Dorado del logo: bordes y letras de los botones. */
private static final Color DORADO = new Color(0xFFE600);
/** Azul grisáceo del logo: fondo de las pantallas. */
private static final Color AZUL   = new Color(0x45546A);
/** Fuente normal de la interfaz. */
private static final Font FUENTE         = new Font("Segoe UI", Font.PLAIN, 14);
/** Fuente en negrita de la interfaz. */
private static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Crea una etiqueta con el logo de la universidad, escalado al ancho indicado y manteniendo
     * su proporción. Si no se encuentra el archivo de la imagen, devuelve una etiqueta con el
     * texto "UNNE" en su lugar.
     * 
     * @param ancho Ancho en píxeles con el que se muestra el logo.
     * @return Etiqueta con el logo, o con el texto "UNNE" si no se pudo cargar la imagen.
     */
private JLabel etiquetaLogo(int ancho) {
    ImageIcon original = new ImageIcon(RUTA_LOGO);
    if (original.getIconWidth() <= 0) {          
        return new JLabel("UNNE");               
    }
    
    Image escalada = original.getImage().getScaledInstance(ancho, -1, Image.SCALE_SMOOTH);
    return new JLabel(new ImageIcon(escalada));
}

    /**
     * Realiza el préstamo de un libro a un socio con la fecha actual e informa el resultado en
     * pantalla. Si no se puede prestar (libro ya prestado, o socio con préstamos vencidos o que
     * superó su límite) o falla la base de datos, muestra el motivo. Luego actualiza las tablas.
     * 
     * @param s Socio que retira el libro.
     * @param l Libro que se presta.
     */
private void realizarPrestamo(Socio s, Libro l) {
    try {
        if (biblio.prestarLibro(Calendar.getInstance(), s, l)) {
            JOptionPane.showMessageDialog(this, "Préstamo realizado.");
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo prestar: el libro ya está prestado o el socio tiene "
                + "préstamos vencidos / superó su límite.",
                "No se pudo prestar", JOptionPane.WARNING_MESSAGE);
        }
        refrescarPrestamos();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * Registra la devolución de un libro con la fecha actual e informa el resultado en pantalla.
     * Si el libro no estaba prestado o falla la base de datos, muestra el error.
     * Luego actualiza las tablas.
     * 
     * @param l Libro que se devuelve.
     */
private void realizarDevolucion(Libro l) {
    try {
        biblio.devolverLibro(l);
        JOptionPane.showMessageDialog(this, "Devolución registrada.");
        refrescarPrestamos();
    } catch (LibroNoPrestadoException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "No se pudo devolver", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * Muestra en un cuadro de diálogo el socio que tiene el libro, o un aviso si el libro
     * se encuentra en la biblioteca.
     * 
     * @param l Libro que se consulta.
     */
private void mostrarQuienTiene(Libro l) {
    JOptionPane.showMessageDialog(this, biblio.quienTieneElLibro(l));
}

    /**
     * Obtiene el libro de la fila seleccionada en la tabla de libros, teniendo en cuenta que
     * la tabla puede estar filtrada u ordenada. Si no hay ninguna fila seleccionada, avisa en pantalla.
     * 
     * @return El libro seleccionado, o null si no hay ninguna fila seleccionada.
     */

private Libro libroSeleccionado() {
    int fila = tablaLibros.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccioná un libro de la tabla.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return null;
    }
    
    int filaModelo = tablaLibros.convertRowIndexToModel(fila);
    return biblio.getLibros().get(filaModelo);
}   

    /**
     * Presta el libro seleccionado en la tabla de libros: pide el D.N.I. del socio, muestra su
     * nombre y tipo para confirmar y realiza el préstamo. Avisa si no hay un libro seleccionado,
     * si el D.N.I. no es un número o si no existe un socio con ese D.N.I.
     */

private void prestarDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l == null) return;

    String texto = JOptionPane.showInputDialog(this,
            "DNI del socio que retira \"" + l.getTitulo() + "\":",
            "Prestar libro", JOptionPane.QUESTION_MESSAGE);
    if (texto == null) return;                         

    try {
        Socio s = biblio.buscarSocio(Integer.parseInt(texto.trim()));
        if (s == null) {
            JOptionPane.showMessageDialog(this, "No existe un socio con ese DNI.",
                    "Socio no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "¿Prestar \"" + l.getTitulo() + "\" a " + s.getNombre()
                + " (" + s.soyDeLaClase() + ")?",
                "Confirmar préstamo", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) realizarPrestamo(s, l);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "El DNI debe ser un número.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
    }
}

    /**
     * Registra la devolución del libro seleccionado en la tabla de libros.
     */

private void devolverDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l != null) realizarDevolucion(l);
}

    /**
     * Muestra quién tiene el libro seleccionado en la tabla de libros.
     */

private void quienTieneDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l != null) mostrarQuienTiene(l);
}

    /**
     * Aplica a un botón el estilo de la interfaz: fondo rojo, letras y borde dorados
     * y cursor de mano.
     * 
     * @param b Botón al que se le aplica el estilo.
     */
private void estilizarBoton(JButton b) {
    b.setBackground(ROJO);
    b.setForeground(DORADO);
    b.setFont(FUENTE_NEGRITA);
    b.setFocusPainted(false);
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));    
    b.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DORADO, 2),             
        BorderFactory.createEmptyBorder(6, 14, 6, 14)));       
}

    /**
     * Aplica a una tabla el estilo de la interfaz: filas más altas, líneas horizontales delgadas,
     * fila seleccionada en azul y encabezado de columnas rojo con letras doradas.
     * 
     * @param t Tabla a la que se le aplica el estilo.
     */
private void estilizarTabla(JTable t) {
    t.setFont(FUENTE);
    t.setRowHeight(28);                                   
    t.setFillsViewportHeight(true);                       
    t.setGridColor(new Color(0xD9DDE3));                  
    t.setShowVerticalLines(false);                        
    t.setSelectionBackground(AZUL);                       
    t.setSelectionForeground(Color.WHITE);

    JTableHeader h = t.getTableHeader();                 
    h.setBackground(ROJO);
    h.setForeground(DORADO);
    h.setFont(FUENTE_NEGRITA);
    h.setReorderingAllowed(false);                       
}


    /**
     * Envuelve el contenido de una pantalla con un encabezado que tiene el botón "Volver al menú"
     * y el título de la pantalla, sobre fondo blanco y con una línea dorada debajo.
     * 
     * @param titulo Título que se muestra en el encabezado.
     * @param contenido Panel con el contenido de la pantalla.
     * @return Panel con el encabezado arriba y el contenido debajo.
     */
private JPanel conEncabezado(String titulo, JPanel contenido) {
    JButton volver = new JButton("← Volver al menú");
    estilizarBoton(volver);
    volver.addActionListener(e -> irA("menu"));

    JLabel lblTitulo = new JLabel(titulo);
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitulo.setForeground(ROJO);
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));   

    JPanel encabezado = new JPanel(new BorderLayout());
    encabezado.setBackground(Color.WHITE);
    encabezado.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 4, 0, DORADO),   
        BorderFactory.createEmptyBorder(10, 16, 10, 16)));
    encabezado.add(volver, BorderLayout.WEST);
    encabezado.add(lblTitulo, BorderLayout.CENTER);

    JPanel envoltorio = new JPanel(new BorderLayout());
    envoltorio.add(encabezado, BorderLayout.NORTH);
    envoltorio.add(contenido, BorderLayout.CENTER);
    return envoltorio;
}

    /**
     * Arma la barra superior de la pantalla de Socios: una caja de búsqueda por D.N.I. o nombre
     * y un desplegable para filtrar por tipo. Los dos aplican el filtro a la tabla apenas cambian.
     * 
     * @return Panel con la barra de búsqueda y el filtro por tipo.
     */
private JPanel panelBusquedaSocios() {
    JLabel lblBuscar = new JLabel("Buscar por DNI o nombre:");
    lblBuscar.setFont(FUENTE_NEGRITA);
    lblBuscar.setForeground(Color.WHITE);

    txtBuscarSocio.setFont(FUENTE);
    txtBuscarSocio.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DORADO, 2),
        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    txtBuscarSocio.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e)  { aplicarFiltroSocios(); }
        public void removeUpdate(DocumentEvent e)  { aplicarFiltroSocios(); }
        public void changedUpdate(DocumentEvent e) { aplicarFiltroSocios(); }
    });

    JLabel lblTipo = new JLabel("Tipo:");
    lblTipo.setFont(FUENTE_NEGRITA);
    lblTipo.setForeground(Color.WHITE);
    cmbTipoSocio.setFont(FUENTE);
    cmbTipoSocio.addActionListener(e -> aplicarFiltroSocios());

    JPanel filtroTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    filtroTipo.setOpaque(false);
    filtroTipo.add(lblTipo);
    filtroTipo.add(cmbTipoSocio);

    JPanel busqueda = new JPanel(new BorderLayout(8, 0));
    busqueda.setOpaque(false);
    busqueda.add(lblBuscar, BorderLayout.WEST);
    busqueda.add(txtBuscarSocio, BorderLayout.CENTER);
    busqueda.add(filtroTipo, BorderLayout.EAST);
    return busqueda;
}

    /**
     * Filtra la tabla de socios combinando el texto de búsqueda y el tipo elegido. Cada palabra
     * del texto debe aparecer en el D.N.I. o en el nombre (sin distinguir mayúsculas de minúsculas)
     * y, si el tipo no es "Todos", el socio debe ser de ese tipo.
     * Sin filtros, se muestran todos los socios.
     */

private void aplicarFiltroSocios() {
    ArrayList<RowFilter<DefaultTableModel, Integer>> filtros = new ArrayList<>();

    
    String texto = txtBuscarSocio.getText().trim();
    if (!texto.isEmpty()) {
        for (String palabra : texto.split("\\s+")) {
            filtros.add(RowFilter.regexFilter("(?iu)" + Pattern.quote(palabra), 0, 1));
        }
    }

    
    String tipo = (String) cmbTipoSocio.getSelectedItem();
    if (!tipo.equals("Todos")) {
        filtros.add(RowFilter.regexFilter("^" + tipo + "$", 2));
    }

    if (filtros.isEmpty()) {
        filtroSocios.setRowFilter(null);                     
    } else {
        filtroSocios.setRowFilter(RowFilter.andFilter(filtros)); 
    }
}

    /**
     * Dibuja los elementos de los desplegables con un texto corto: nombre, D.N.I. y tipo
     * para los socios, y título y edición para los libros.
     */
private class RenderCombo extends DefaultListCellRenderer {
        /**
         * Devuelve el componente que dibuja un elemento de la lista desplegable.
         * 
         * @param lista Lista que se está dibujando.
         * @param valor Elemento a dibujar (un Socio o un Libro).
         * @param indice Posición del elemento en la lista.
         * @param seleccionado true si el elemento está seleccionado.
         * @param foco true si el elemento tiene el foco.
         * @return Componente con el texto del elemento.
         */
    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor,
            int indice, boolean seleccionado, boolean foco) {
        super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
        if (valor instanceof Socio s) {
            setText(s.getNombre() + " | DNI " + s.getDNI() + " | " + s.soyDeLaClase());
        } else if (valor instanceof Libro l) {
            setText(l.getTitulo() + " | ed. " + l.getEdicion());
        }
        return this;
    }
}   
    /**
     * Dibuja las celdas de la tabla de préstamos: muestra las fechas con formato (y un guion si
     * todavía no hay fecha de devolución) y pinta las filas vencidas: en rojo claro si siguen sin
     * devolverse y en amarillo claro si se devolvieron fuera de término.
     */
private class RenderPrestamos extends DefaultTableCellRenderer {
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        /**
         * Convierte el valor de una celda en el texto que se muestra: las fechas con formato
         * dd/MM/yyyy HH:mm y un guion cuando no hay valor.
         * 
         * @param valor Valor de la celda (una fecha, un texto o null).
         */
    @Override
    protected void setValue(Object valor) {
        if (valor instanceof Calendar c) {
            setText(fmt.format(c.getTime()));
        } else if (valor == null) {
            setText("-");                                  
        } else {
            super.setValue(valor);
        }
    }

        /**
         * Devuelve el componente que dibuja una celda, con el color de fondo según si el préstamo
         * está vencido. Las filas seleccionadas conservan el color de selección.
         * 
         * @param t Tabla que se está dibujando.
         * @param v Valor de la celda.
         * @param seleccionada true si la fila está seleccionada.
         * @param foco true si la celda tiene el foco.
         * @param fila Número de fila, en la tabla visible.
         * @param col Número de columna.
         * @return Componente que dibuja la celda.
         */
    @Override
    public Component getTableCellRendererComponent(JTable t, Object v,
            boolean seleccionada, boolean foco, int fila, int col) {
        Component c = super.getTableCellRendererComponent(t, v, seleccionada, foco, fila, col);
        if (!seleccionada) {
            boolean vencido = "Sí".equals(t.getValueAt(fila, 4));
            boolean pendiente = t.getValueAt(fila, 3) == null;
            if (vencido && pendiente) {
                c.setBackground(new Color(0xFFC9C9));      
            } else if (vencido) {
                c.setBackground(new Color(0xFFF0C9));     
            } else {
                c.setBackground(Color.WHITE);
            }
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}

    /**
     * Obtiene el socio de la fila seleccionada en la tabla de socios, teniendo en cuenta que
     * la tabla puede estar filtrada u ordenada. Si no hay ninguna fila seleccionada, avisa en pantalla.
     * 
     * @return El socio seleccionado, o null si no hay ninguna fila seleccionada.
     */
private Socio socioSeleccionado() {
    int fila = tablaSocios.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccioná un socio de la tabla.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return null;
    }
    
    return biblio.getSocios().get(tablaSocios.convertRowIndexToModel(fila));
}

    /**
     * Suma días de préstamo al docente seleccionado en la tabla de socios. Solo se permite a los
     * docentes responsables: avisa si el socio no es docente o no es responsable. Pide la cantidad
     * de días a sumar, valida que sea un número entero mayor a cero y actualiza las tablas.
     */
private void ampliarDiasDocente() {
    Socio s = socioSeleccionado();
    if (s == null) return;

    if (!(s instanceof Docente d)) {
        JOptionPane.showMessageDialog(this,
                "Solo se pueden ampliar los días de préstamo de los docentes.",
                "Socio no válido", JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!d.esResponsable()) {
        JOptionPane.showMessageDialog(this,
                d.getNombre() + " no es responsable: tiene o tuvo préstamos vencidos.",
                "No se pueden ampliar los días", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String texto = JOptionPane.showInputDialog(this,
            d.getNombre() + " tiene " + d.getDiasPrestamo() + " días de préstamo.\n"
            + "¿Cuántos días querés sumar?",
            "Ampliar días de préstamo", JOptionPane.QUESTION_MESSAGE);
    if (texto == null) return;                        

    try {
        int dias = Integer.parseInt(texto.trim());
        if (dias <= 0) {
            JOptionPane.showMessageDialog(this, "Ingresá un número mayor a cero.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (biblio.cambiarDiasDocente(d, dias)) {
            refrescarSocios();
            refrescarPrestamos();      
            JOptionPane.showMessageDialog(this,
                    "Ahora tiene " + d.getDiasPrestamo() + " días de préstamo.");
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Debe ingresar un número entero.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}