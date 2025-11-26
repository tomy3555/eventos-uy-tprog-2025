package presentacion;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import excepciones.RegistroNoExiste;
import logica.datatypes.DTRegistro;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.util.Set;
import java.util.Vector;

public class ConsultaRegistroFrame extends JInternalFrame {
    // Definimos algunos paneles como atributos
    private JPanel panelSeleccion;
    private JComboBox<String> comboAsistentes;
    private JComboBox<String> comboRegistros;
    private Vector<String> asistentes;
    private IControladorUsuario controlUsr;

    // Detalle
    private JTextField txtId;
    private JTextField txtEdicion;
    private JTextField txtTipoRegistro;
    private JTextField txtFechaRegistro;
    private JTextField txtCosto;

    // --- Constructor normal
    public ConsultaRegistroFrame(IControladorUsuario icu, IControladorEvento iCE) {
        super("Consulta de Registro", true, true, true, true);
        setBounds(200, 200, 500, 350);
        setLayout(new BorderLayout());
        controlUsr = icu;

        // Barra superior
        panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAsistente = new JLabel("Asistente:");
        asistentes = new Vector<>();
        asistentes.addAll(controlUsr.listarAsistentes().keySet());
        comboAsistentes = new JComboBox<>(asistentes);

        JLabel lblRegistro = new JLabel("Registro:");
        comboRegistros = new JComboBox<>();

        panelSeleccion.add(lblAsistente);
        panelSeleccion.add(comboAsistentes);
        panelSeleccion.add(lblRegistro);
        panelSeleccion.add(comboRegistros);
        add(panelSeleccion, BorderLayout.NORTH);

        // Form de detalle
        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 10, 10));
        panelDatos.add(new JLabel("Identificador:"));
        txtId = new JTextField(); txtId.setEditable(false); panelDatos.add(txtId);

        panelDatos.add(new JLabel("Edición:"));
        txtEdicion = new JTextField(); txtEdicion.setEditable(false); panelDatos.add(txtEdicion);

        panelDatos.add(new JLabel("Tipo de registro:"));
        txtTipoRegistro = new JTextField(); txtTipoRegistro.setEditable(false); panelDatos.add(txtTipoRegistro);

        panelDatos.add(new JLabel("Fecha de registro:"));
        txtFechaRegistro = new JTextField(); txtFechaRegistro.setEditable(false); panelDatos.add(txtFechaRegistro);

        panelDatos.add(new JLabel("Costo:"));
        txtCosto = new JTextField(); txtCosto.setEditable(false); panelDatos.add(txtCosto);

        add(panelDatos, BorderLayout.CENTER);

        // Listeners
        comboAsistentes.addActionListener(e -> {
            String nick = (String) comboAsistentes.getSelectedItem();
            comboRegistros.removeAllItems();
            limpiarCampos();
            if (nick == null) return;
            Set<DTRegistro> registrosPorUsuario =
                    controlUsr.obtenerRegistrosAsistente(controlUsr.listarAsistentes().get(nick));
            for (DTRegistro reg : registrosPorUsuario) {
                comboRegistros.addItem(reg.getId());
            }
            panelSeleccion.revalidate();
            panelSeleccion.repaint();
        });

        comboRegistros.addActionListener(e -> {
            String idRegistro = (String) comboRegistros.getSelectedItem();
            limpiarCampos();
            if (idRegistro == null) return;
            DTRegistro datos = controlUsr.obtenerDatosRegistros(idRegistro);
            if (datos != null) {
                txtId.setText(datos.getId());
                txtEdicion.setText(datos.getEdicion());
                txtTipoRegistro.setText(datos.getTipoRegistro());
                txtFechaRegistro.setText(String.valueOf(datos.getFechaRegistro()));
                txtCosto.setText(String.valueOf(datos.getCosto()));
            }
        });

        setVisible(true);
    }

    // --- Constructor con PRESELECCIÓN (nick + idRegistro)
    public ConsultaRegistroFrame(IControladorUsuario icu,
                                 IControladorEvento iCE,
                                 String nickPreseleccionado,
                                 String idRegistroPreseleccionado) {
        this(icu, iCE); // construye UI y listeners

        // Seteamos el asistente (esto dispara el listener y carga los registros)
        if (nickPreseleccionado != null) {
            comboAsistentes.setSelectedItem(nickPreseleccionado);
        }

        // Después que el combo de registros se cargue, seleccionamos el registro
        if (idRegistroPreseleccionado != null) {
            SwingUtilities.invokeLater(() -> comboRegistros.setSelectedItem(idRegistroPreseleccionado));
        }
    }

    public void cargarAsistentes() throws RegistroNoExiste {
        // Este método se usa para refrescar los asistentes al entrar al caso de uso
        DefaultComboBoxModel<String> model;
        asistentes.clear();
        asistentes.addAll(controlUsr.listarAsistentes().keySet());
        model = new DefaultComboBoxModel<>(asistentes);
        comboAsistentes.setModel(model);
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtEdicion.setText("");
        txtTipoRegistro.setText("");
        txtFechaRegistro.setText("");
        txtCosto.setText("");
    }
}