package presentacion;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import logica.clases.Ediciones;
import logica.clases.TipoRegistro;
import logica.controladores.ControladorEvento;
import logica.datatypes.DTEvento;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.util.List;
import java.util.ArrayList;

public class ConsultaTipoRegistroFrame extends JInternalFrame {
    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JComboBox<String> comboTipos;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtCupo;
    private JTextField txtCosto;
    private List<DTEvento> eventosDTO;
    private List<String> edicionesActuales;
    private List<TipoRegistro> tiposActuales;

    /**
     * @wbp.parser.constructor
     */
    public ConsultaTipoRegistroFrame(IControladorUsuario iCU, IControladorEvento iCE) {
        super("Consulta de Tipo de Registro", true, true, true, true);
        setBounds(150, 150, 700, 350);
        getContentPane().setLayout(new BorderLayout());

        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelSeleccion.add(lblEvento);
        panelSeleccion.add(comboEventos);
        JLabel lblEdicion = new JLabel("Edición:");
        comboEdiciones = new JComboBox<>();
        panelSeleccion.add(lblEdicion);
        panelSeleccion.add(comboEdiciones);
        JLabel lblTipo = new JLabel("Tipo de Registro:");
        comboTipos = new JComboBox<>();
        panelSeleccion.add(lblTipo);
        panelSeleccion.add(comboTipos);
        getContentPane().add(panelSeleccion, BorderLayout.NORTH);

        JPanel panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelDatos.add(panelCampos);

        GridBagConstraints gbcNombreLabel = new GridBagConstraints();
        gbcNombreLabel.insets = new Insets(2, 2, 2, 2);
        gbcNombreLabel.anchor = GridBagConstraints.WEST;
        gbcNombreLabel.gridx = 0;
        gbcNombreLabel.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbcNombreLabel);

        GridBagConstraints gbcNombreField = new GridBagConstraints();
        gbcNombreField.insets = new Insets(2, 2, 2, 2);
        gbcNombreField.fill = GridBagConstraints.HORIZONTAL;
        gbcNombreField.gridx = 1;
        gbcNombreField.gridy = 0;
        txtNombre = new JTextField();
        txtNombre.setEditable(false);
        txtNombre.setColumns(15);
        panelCampos.add(txtNombre, gbcNombreField);

        GridBagConstraints gbcDescLabel = new GridBagConstraints();
        gbcDescLabel.insets = new Insets(2, 2, 2, 2);
        gbcDescLabel.anchor = GridBagConstraints.WEST;
        gbcDescLabel.gridx = 0;
        gbcDescLabel.gridy = 1;
        panelCampos.add(new JLabel("Descripción:"), gbcDescLabel);

        GridBagConstraints gbcDescField = new GridBagConstraints();
        gbcDescField.insets = new Insets(2, 2, 2, 2);
        gbcDescField.fill = GridBagConstraints.BOTH;
        gbcDescField.gridx = 1;
        gbcDescField.gridy = 1;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelCampos.add(scrollDesc, gbcDescField);

        GridBagConstraints gbcCupoLabel = new GridBagConstraints();
        gbcCupoLabel.insets = new Insets(2, 2, 2, 2);
        gbcCupoLabel.anchor = GridBagConstraints.WEST;
        gbcCupoLabel.gridx = 0;
        gbcCupoLabel.gridy = 2;
        panelCampos.add(new JLabel("Cupo:"), gbcCupoLabel);

        GridBagConstraints gbcCupoField = new GridBagConstraints();
        gbcCupoField.insets = new Insets(2, 2, 2, 2);
        gbcCupoField.fill = GridBagConstraints.HORIZONTAL;
        gbcCupoField.gridx = 1;
        gbcCupoField.gridy = 2;
        txtCupo = new JTextField();
        txtCupo.setEditable(false);
        txtCupo.setColumns(10);
        panelCampos.add(txtCupo, gbcCupoField);

        GridBagConstraints gbcCostoLabel = new GridBagConstraints();
        gbcCostoLabel.insets = new Insets(2, 2, 2, 2);
        gbcCostoLabel.anchor = GridBagConstraints.WEST;
        gbcCostoLabel.gridx = 0;
        gbcCostoLabel.gridy = 3;
        panelCampos.add(new JLabel("Costo:"), gbcCostoLabel);

        GridBagConstraints gbcCostoField = new GridBagConstraints();
        gbcCostoField.insets = new Insets(2, 2, 2, 2);
        gbcCostoField.fill = GridBagConstraints.HORIZONTAL;
        gbcCostoField.gridx = 1;
        gbcCostoField.gridy = 3;
        txtCosto = new JTextField();
        txtCosto.setEditable(false);
        txtCosto.setColumns(10);
        panelCampos.add(txtCosto, gbcCostoField);

        getContentPane().add(panelDatos, BorderLayout.CENTER);

        comboEventos.addActionListener(e -> cargarEdiciones());
        comboEdiciones.addActionListener(e -> cargarTipos());
        comboTipos.addActionListener(e -> mostrarDatosTipo());
    }

    public ConsultaTipoRegistroFrame(IControladorUsuario iCU, IControladorEvento iCE, String nombreEvento, String nombreEdicion, String nombreTipo) {
        this(iCU, iCE);
        cargarEventos();
        if (nombreEvento != null && nombreEdicion != null && nombreTipo != null) {
            for (int i = 0; i < comboEventos.getItemCount(); i++) {
                if (comboEventos.getItemAt(i).equals(nombreEvento)) {
                    comboEventos.setSelectedIndex(i);
                    break;
                }
            }
            cargarEdiciones();
            for (int j = 0; j < comboEdiciones.getItemCount(); j++) {
                if (comboEdiciones.getItemAt(j).equals(nombreEdicion)) {
                    comboEdiciones.setSelectedIndex(j);
                    break;
                }
            }
            cargarTipos();
            for (int k = 0; k < comboTipos.getItemCount(); k++) {
                if (comboTipos.getItemAt(k).equals(nombreTipo)) {
                    comboTipos.setSelectedIndex(k);
                    break;
                }
            }
            mostrarDatosTipo();
        }
    }

    public void cargarEventos() {
        try {
            ControladorEvento controlador = new ControladorEvento();
            eventosDTO = controlador.listarEventos();
            comboEventos.removeAllItems();
            for (DTEvento ev : eventosDTO) {
                comboEventos.addItem(ev.getNombre());
            }
            if (comboEventos.getItemCount() > 0) {
                comboEventos.setSelectedIndex(0);
                cargarEdiciones();
            } else {
                comboEdiciones.removeAllItems();
                comboTipos.removeAllItems();
                txtNombre.setText("");
                txtDescripcion.setText("");
                txtCupo.setText("");
                txtCosto.setText("");
            }
        } catch (IllegalStateException | NullPointerException ex) {
            comboEventos.setModel(new DefaultComboBoxModel<>(new String[]{"No hay eventos"}));
            comboEdiciones.removeAllItems();
            comboTipos.removeAllItems();
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtCupo.setText("");
            txtCosto.setText("");
        }
    }

    private void cargarEdiciones() {
        comboEdiciones.removeAllItems();
        comboTipos.removeAllItems();
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCupo.setText("");
        txtCosto.setText("");
        int idx = comboEventos.getSelectedIndex();
        if (idx < 0 || eventosDTO == null || idx >= eventosDTO.size()) return;
        DTEvento evento = eventosDTO.get(idx);
        edicionesActuales = evento.getEdiciones();
        for (String ed : edicionesActuales) {
            comboEdiciones.addItem(ed);
        }
        if (comboEdiciones.getItemCount() > 0) {
            comboEdiciones.setSelectedIndex(0);
            cargarTipos();
        }
    }

    private void cargarTipos() {
        comboTipos.removeAllItems();
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCupo.setText("");
        txtCosto.setText("");
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0 || eventosDTO == null || idxEvento >= eventosDTO.size() || edicionesActuales == null || idxEdicion >= edicionesActuales.size()) return;
        String nombreEvento = eventosDTO.get(idxEvento).getNombre();
        String nombreEdicion = edicionesActuales.get(idxEdicion);
        Ediciones edicion = new ControladorEvento().obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) return;
        tiposActuales = new ArrayList<>(edicion.getTiposRegistro());
        for (TipoRegistro tr : tiposActuales) {
            comboTipos.addItem(tr.getNombre());
        }
        if (comboTipos.getItemCount() > 0) {
            comboTipos.setSelectedIndex(0);
            mostrarDatosTipo();
        }
    }

    private void mostrarDatosTipo() {
        int idxTipo = comboTipos.getSelectedIndex();
        if (tiposActuales == null || idxTipo < 0 || idxTipo >= tiposActuales.size()) {
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtCupo.setText("");
            txtCosto.setText("");
            return;
        }
        TipoRegistro tipoReg = tiposActuales.get(idxTipo);
        txtNombre.setText(tipoReg.getNombre());
        txtDescripcion.setText(tipoReg.getDescripcion());
        txtCupo.setText(String.valueOf(tipoReg.getCupo()));
        txtCosto.setText(String.valueOf(tipoReg.getCosto()));
    }
}