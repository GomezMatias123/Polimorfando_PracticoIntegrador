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
        private final CardLayout cartas = new CardLayout();      // el "administrador" de cartas
        private final JPanel contenedor = new JPanel(cartas);    // la baraja: usa ese layout

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

    private JPanel panelLibros() {
    JPanel panel = new JPanel(new BorderLayout(8, 12));
    panel.setBackground(AZUL);                                              // fondo azul del logo
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    // --- Arriba: búsqueda
    JLabel lblBuscar = new JLabel("Buscar por título:");
    lblBuscar.setFont(FUENTE_NEGRITA);
    lblBuscar.setForeground(Color.WHITE);
    txtBuscarLibro.setFont(FUENTE);
    txtBuscarLibro.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DORADO, 2),
        BorderFactory.createEmptyBorder(4, 8, 4, 8)));

    JPanel busqueda = new JPanel(new BorderLayout(8, 0));
    busqueda.setOpaque(false);                                              // deja ver el fondo azul
    busqueda.add(lblBuscar, BorderLayout.WEST);
    busqueda.add(txtBuscarLibro, BorderLayout.CENTER);
    panel.add(busqueda, BorderLayout.NORTH);

    // --- Centro: tabla con filtro
    TableRowSorter<DefaultTableModel> filtro = new TableRowSorter<>(modeloLibros);
    tablaLibros.setRowSorter(filtro);
    tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    estilizarTabla(tablaLibros);                                            // nueva
    txtBuscarLibro.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e)  { aplicarFiltroLibros(filtro); }
        public void removeUpdate(DocumentEvent e)  { aplicarFiltroLibros(filtro); }
        public void changedUpdate(DocumentEvent e) { aplicarFiltroLibros(filtro); }
    });

    JScrollPane scroll = new JScrollPane(tablaLibros);
    scroll.setBorder(BorderFactory.createLineBorder(DORADO, 2));            // marco dorado
    scroll.getViewport().setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);

    // --- Abajo: botones
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
        estilizarBoton(b);                                                  // mismo estilo para los cuatro
        botones.add(b);
    }
    panel.add(botones, BorderLayout.SOUTH);
    return panel;
}

private void aplicarFiltroLibros(TableRowSorter<DefaultTableModel> filtro) {
    String texto = txtBuscarLibro.getText().trim();
    if (texto.isEmpty()) {
        filtro.setRowFilter(null);   // sin texto: se muestran todos
    } else {
        // (?iu) ignora mayúsculas y tildes de mayúsculas; quote evita que símbolos como . o ( se interpreten como regex; 0 = columna Título
        filtro.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(texto), 0));
    }
}

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

    // --- Abajo: botones a la izquierda, conteo a la derecha
    JButton btnEst = new JButton("Agregar estudiante");
    btnEst.addActionListener(e -> agregarSocio(true));
    JButton btnDoc = new JButton("Agregar docente");
    btnDoc.addActionListener(e -> agregarSocio(false));

    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    botones.setOpaque(false);
    for (JButton b : new JButton[]{btnEst, btnDoc}) {
        estilizarBoton(b);
        botones.add(b);
    }

    lblConteoSocios.setFont(FUENTE_NEGRITA);
    lblConteoSocios.setForeground(DORADO);

    JPanel abajo = new JPanel(new BorderLayout());
    abajo.setOpaque(false);
    abajo.add(botones, BorderLayout.WEST);
    abajo.add(lblConteoSocios, BorderLayout.EAST);     // esquina inferior derecha
    panel.add(abajo, BorderLayout.SOUTH);
    return panel;
}

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

private JPanel panelPrestamos() {
    JPanel panel = new JPanel(new BorderLayout(8, 12));
    panel.setBackground(AZUL);
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    // --- Arriba: los dos desplegables, cada uno con su etiqueta
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

    // --- Centro: tabla de préstamos
    tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    estilizarTabla(tablaPrestamos);
    // (paso 2: renderer y orden)
    tablaPrestamos.setDefaultRenderer(Object.class, new RenderPrestamos());

TableRowSorter<DefaultTableModel> orden = new TableRowSorter<>(modeloPrestamos);
tablaPrestamos.setRowSorter(orden);
orden.setSortKeys(List.of(
    new RowSorter.SortKey(3, SortOrder.ASCENDING),    // 1º: Devolución, los pendientes arriba
    new RowSorter.SortKey(4, SortOrder.DESCENDING))); // 2º: dentro de los pendientes, vencidos primero

    JScrollPane scroll = new JScrollPane(tablaPrestamos);
    scroll.setBorder(BorderFactory.createLineBorder(DORADO, 2));
    scroll.getViewport().setBackground(Color.WHITE);
    panel.add(scroll, BorderLayout.CENTER);

    // --- Abajo: botones a la izquierda, pista a la derecha
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
                p.getFechaRetiro(),          // Calendar: se ordena como fecha
                dev,                         // null = todavía no se devolvió
                vencido ? "Sí" : "No"
            });
            filasPrestamos.add(p);           // la fila i del modelo es el préstamo i de esta lista
        }
    }
    refrescarLibros();
}

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

private void devolver() {
    int fila = tablaPrestamos.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccioná en la tabla el préstamo que se devuelve.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }
    // la fila visible puede estar ordenada: se convierte a la fila real del modelo
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

private void quienTiene() {
    Libro l = (Libro) cmbLibros.getSelectedItem();
    if (l == null) return;
    mostrarQuienTiene(l);
}

// Cambia la carta visible
private void irA(String nombre) {
    cartas.show(contenedor, nombre);
}

// Pantalla del menú: tres botones centrados
private JPanel panelMenu() {
    // --- Encabezado: logo arriba y título abajo, apilados
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
    titulo.setForeground(ROJO);                        // movida: ahora titulo ya existe
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    encabezado.add(logo);
    encabezado.add(Box.createVerticalStrut(15));
    encabezado.add(titulo);

    // --- Botones
    JPanel botones = new JPanel(new GridLayout(3, 1, 0, 12));
    botones.setOpaque(false);                          // movida: ahora botones ya existe
    botones.add(botonMenu("Libros", "libros"));
    botones.add(botonMenu("Socios", "socios"));
    botones.add(botonMenu("Préstamos", "prestamos"));

    JPanel centro = new JPanel(new GridBagLayout());
    centro.setBackground(AZUL);                        // movida: ahora centro ya existe
    centro.add(botones);

    // --- Armado: encabezado arriba, botones ocupando el resto
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(encabezado, BorderLayout.NORTH);
    panel.add(centro, BorderLayout.CENTER);
    return panel;
}

// Crea un botón que, al hacer clic, muestra la carta indicada
private JButton botonMenu(String texto, String destino) {
    JButton b = new JButton(texto);
    b.setPreferredSize(new Dimension(280, 48));   // ancho y alto del botón
    b.addActionListener(e -> irA(destino));
    b.setBackground(ROJO);
    b.setForeground(DORADO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 16));
    b.setFocusPainted(false);                          // saca el recuadro punteado al hacer clic
    b.setBorder(BorderFactory.createLineBorder(DORADO, 2));
    return b;
}



private static final String RUTA_LOGO = "img/facena-unne-1024x320.png";
private static final Color ROJO   = new Color(0x7B1C1F);
private static final Color DORADO = new Color(0xFFE600);
private static final Color AZUL   = new Color(0x45546A);
private static final Font FUENTE         = new Font("Segoe UI", Font.PLAIN, 14);
private static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);

private JLabel etiquetaLogo(int ancho) {
    ImageIcon original = new ImageIcon(RUTA_LOGO);
    if (original.getIconWidth() <= 0) {          // no encontró el archivo
        return new JLabel("UNNE");               // muestra texto y no se rompe
    }
    // alto = -1: se calcula solo, manteniendo la proporción del logo
    Image escalada = original.getImage().getScaledInstance(ancho, -1, Image.SCALE_SMOOTH);
    return new JLabel(new ImageIcon(escalada));
}

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

private void mostrarQuienTiene(Libro l) {
    JOptionPane.showMessageDialog(this, biblio.quienTieneElLibro(l));
}
// Devuelve el libro de la fila seleccionada, o null si no hay ninguna
private Libro libroSeleccionado() {
    int fila = tablaLibros.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccioná un libro de la tabla.",
                "Dato inválido", JOptionPane.WARNING_MESSAGE);
        return null;
    }
    // si la tabla está filtrada u ordenada, la fila visible no coincide con la del modelo
    int filaModelo = tablaLibros.convertRowIndexToModel(fila);
    return biblio.getLibros().get(filaModelo);
}

private void prestarDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l == null) return;

    String texto = JOptionPane.showInputDialog(this,
            "DNI del socio que retira \"" + l.getTitulo() + "\":",
            "Prestar libro", JOptionPane.QUESTION_MESSAGE);
    if (texto == null) return;                         // tocó Cancelar

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

private void devolverDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l != null) realizarDevolucion(l);
}

private void quienTieneDesdeLibros() {
    Libro l = libroSeleccionado();
    if (l != null) mostrarQuienTiene(l);
}

// Aplica a un botón el mismo estilo del menú: fondo rojo, letras doradas, borde dorado
private void estilizarBoton(JButton b) {
    b.setBackground(ROJO);
    b.setForeground(DORADO);
    b.setFont(FUENTE_NEGRITA);
    b.setFocusPainted(false);
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));    // la manito al pasar el mouse
    b.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DORADO, 2),             // borde dorado
        BorderFactory.createEmptyBorder(6, 14, 6, 14)));       // aire adentro del botón
}
private void estilizarTabla(JTable t) {
    t.setFont(FUENTE);
    t.setRowHeight(28);                                   // filas más altas
    t.setFillsViewportHeight(true);                       // el espacio vacío debajo se pinta blanco
    t.setGridColor(new Color(0xD9DDE3));                  // líneas suaves
    t.setShowVerticalLines(false);                        // solo líneas horizontales
    t.setSelectionBackground(AZUL);                       // fila elegida: azul del logo, texto blanco
    t.setSelectionForeground(Color.WHITE);

    JTableHeader h = t.getTableHeader();                  // la fila de títulos de las columnas
    h.setBackground(ROJO);
    h.setForeground(DORADO);
    h.setFont(FUENTE_NEGRITA);
    h.setReorderingAllowed(false);                        // no se pueden arrastrar las columnas
}

// Igual que conBarraVolver, pero con el encabezado del menú: fondo blanco, título rojo y línea dorada
private JPanel conEncabezado(String titulo, JPanel contenido) {
    JButton volver = new JButton("← Volver al menú");
    estilizarBoton(volver);
    volver.addActionListener(e -> irA("menu"));

    JLabel lblTitulo = new JLabel(titulo);
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitulo.setForeground(ROJO);
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));   // separa el título del botón

    JPanel encabezado = new JPanel(new BorderLayout());
    encabezado.setBackground(Color.WHITE);
    encabezado.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 4, 0, DORADO),   // línea dorada abajo
        BorderFactory.createEmptyBorder(10, 16, 10, 16)));
    encabezado.add(volver, BorderLayout.WEST);
    encabezado.add(lblTitulo, BorderLayout.CENTER);

    JPanel envoltorio = new JPanel(new BorderLayout());
    envoltorio.add(encabezado, BorderLayout.NORTH);
    envoltorio.add(contenido, BorderLayout.CENTER);
    return envoltorio;
}

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

// Combina los dos filtros: texto (DNI o nombre) y tipo
private void aplicarFiltroSocios() {
    ArrayList<RowFilter<DefaultTableModel, Integer>> filtros = new ArrayList<>();

    // cada palabra escrita debe aparecer en la columna 0 (DNI) o 1 (Nombre)
    String texto = txtBuscarSocio.getText().trim();
    if (!texto.isEmpty()) {
        for (String palabra : texto.split("\\s+")) {
            filtros.add(RowFilter.regexFilter("(?iu)" + Pattern.quote(palabra), 0, 1));
        }
    }

    // si el desplegable no está en "Todos", la columna 2 (Tipo) tiene que coincidir exacta
    String tipo = (String) cmbTipoSocio.getSelectedItem();
    if (!tipo.equals("Todos")) {
        filtros.add(RowFilter.regexFilter("^" + tipo + "$", 2));
    }

    if (filtros.isEmpty()) {
        filtroSocios.setRowFilter(null);                      // sin filtros: se ven todos
    } else {
        filtroSocios.setRowFilter(RowFilter.andFilter(filtros)); // tienen que cumplirse todos
    }
}
private class RenderCombo extends DefaultListCellRenderer {
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
private class RenderPrestamos extends DefaultTableCellRenderer {
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // convierte el valor de la celda en texto
    @Override
    protected void setValue(Object valor) {
        if (valor instanceof Calendar c) {
            setText(fmt.format(c.getTime()));
        } else if (valor == null) {
            setText("-");                                  // sin fecha de devolución
        } else {
            super.setValue(valor);
        }
    }

    // decide el color de fondo de la fila
    @Override
    public Component getTableCellRendererComponent(JTable t, Object v,
            boolean seleccionada, boolean foco, int fila, int col) {
        Component c = super.getTableCellRendererComponent(t, v, seleccionada, foco, fila, col);
        if (!seleccionada) {
            boolean vencido = "Sí".equals(t.getValueAt(fila, 4));
            boolean pendiente = t.getValueAt(fila, 3) == null;
            if (vencido && pendiente) {
                c.setBackground(new Color(0xFFC9C9));      // sin devolver y vencido: rojo claro
            } else if (vencido) {
                c.setBackground(new Color(0xFFF0C9));      // devuelto fuera de término: amarillo claro
            } else {
                c.setBackground(Color.WHITE);
            }
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}
}