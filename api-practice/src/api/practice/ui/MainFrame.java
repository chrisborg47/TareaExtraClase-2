package api.practice.ui;

import api.practice.model.User;
import api.practice.service.ApiServiceException;
import api.practice.service.DummyJsonApiService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * JFrame principal de la práctica.
 * Contiene el menú funcional solicitado mediante botones y área de resultados.
 */
public class MainFrame extends JFrame {

    private final DummyJsonApiService apiService;

    private JTextField txtUserId;
    private JTable tblUsuarios;
    private JTextArea txtResultado;
    private DefaultTableModel tableModel;

    public MainFrame() {
        this.apiService = new DummyJsonApiService();

        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Práctica - Consumo de API REST con Java Swing");
        setSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Sistema de Consulta de Usuarios (DummyJSON)", SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelControles.setBorder(BorderFactory.createTitledBorder("Opciones del Menú"));

        JButton btnMostrarTodos = new JButton("1. Mostrar todos los usuarios");
        JButton btnBuscarPorId = new JButton("2. Buscar usuario por ID");
        JButton btnMostrarCorreos = new JButton("3. Mostrar correos");
        JButton btnSalir = new JButton("4. Salir");

        JLabel lblUserId = new JLabel("ID usuario:");
        txtUserId = new JTextField(8);

        panelControles.add(btnMostrarTodos);
        panelControles.add(lblUserId);
        panelControles.add(txtUserId);
        panelControles.add(btnBuscarPorId);
        panelControles.add(btnMostrarCorreos);
        panelControles.add(btnSalir);

        add(panelControles, BorderLayout.SOUTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre Completo", "Edad", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsuarios = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(tblUsuarios);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Listado de Usuarios"));

        txtResultado = new JTextArea(10, 30);
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Detalle / Mensajes"));

        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        panelCentral.add(scrollResultado, BorderLayout.EAST);

        add(panelCentral, BorderLayout.CENTER);

        btnMostrarTodos.addActionListener(e -> mostrarTodosLosUsuarios());
        btnBuscarPorId.addActionListener(e -> buscarUsuarioPorId());
        btnMostrarCorreos.addActionListener(e -> mostrarCorreosUsuarios());
        btnSalir.addActionListener(e -> salirAplicacion());
    }

    /**
     * Opción 1: consulta /users y carga el JTable.
     */
    private void mostrarTodosLosUsuarios() {
        limpiarResultados();

        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return apiService.obtenerTodosLosUsuarios();
            }

            @Override
            protected void done() {
                try {
                    List<User> usuarios = get();
                    for (User user : usuarios) {
                        tableModel.addRow(new Object[]{
                            user.getId(),
                            user.getFullName(),
                            user.getAge(),
                            user.getEmail()
                        });
                    }
                    txtResultado.setText("Se cargaron " + usuarios.size() + " usuarios correctamente.");
                } catch (Exception ex) {
                    mostrarErrorAmigable("Error al consultar usuarios", ex);
                }
            }
        }.execute();
    }

    /**
     * Opción 2: valida ID, consulta /users/{id} y muestra detalle solicitado.
     */
    private void buscarUsuarioPorId() {
        limpiarResultados();

        String textoId = txtUserId.getText().trim();

        if (textoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un ID.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(textoId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser numérico.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final int idFinal = userId;
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return apiService.obtenerUsuarioPorId(idFinal);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    String detalle = "Usuario encontrado:\n"
                            + "Nombre: " + user.getFirstName() + "\n"
                            + "Apellido: " + user.getLastName() + "\n"
                            + "Edad: " + user.getAge() + "\n"
                            + "Email: " + user.getEmail() + "\n"
                            + "Teléfono: " + user.getPhone() + "\n"
                            + "Ciudad: " + user.getCity();

                    txtResultado.setText(detalle);
                    tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getFullName(),
                        user.getAge(),
                        user.getEmail()
                    });
                } catch (Exception ex) {
                    mostrarErrorAmigable("No se pudo buscar el usuario por ID", ex);
                }
            }
        }.execute();
    }

    /**
     * Opción 3: consulta /users y muestra únicamente correos en área de texto.
     */
    private void mostrarCorreosUsuarios() {
        limpiarResultados();

        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                return apiService.obtenerCorreosDeUsuarios();
            }

            @Override
            protected void done() {
                try {
                    List<String> correos = get();

                    if (correos.isEmpty()) {
                        txtResultado.setText("No se encontraron correos para mostrar.");
                        return;
                    }

                    StringBuilder builder = new StringBuilder("Lista de correos:\n");
                    for (String correo : correos) {
                        builder.append(correo).append("\n");
                    }

                    txtResultado.setText(builder.toString());
                } catch (Exception ex) {
                    mostrarErrorAmigable("Error al cargar correos", ex);
                }
            }
        }.execute();
    }

    /**
     * Opción 4: cierra la aplicación de forma correcta.
     */
    private void salirAplicacion() {
        dispose();
    }

    private void limpiarResultados() {
        tableModel.setRowCount(0);
        txtResultado.setText("");
    }

    /**
     * Centraliza la presentación de errores de servicio y errores inesperados.
     */
    private void mostrarErrorAmigable(String contexto, Exception ex) {
        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
        String mensaje;

        if (causa instanceof ApiServiceException) {
            mensaje = causa.getMessage();
        } else {
            mensaje = "Error inesperado: " + causa.getMessage();
        }

        txtResultado.setText(contexto + ":\n" + mensaje);
        JOptionPane.showMessageDialog(this, contexto + "\n" + mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
