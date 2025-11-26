package presentacion;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Insets;

import java.awt.FlowLayout;

//import logica.clases.Eventos;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;
import excepciones.TipoRegistroYaExisteException;
//import java.util.List;

public class AltaTipoRegistroFrame extends JInternalFrame {
    private IControladorEvento controlador;
//  private List<Eventos> listaEventos;
    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCosto;
    private JTextField txtCupo;

    public AltaTipoRegistroFrame(IControladorUsuario iCU, IControladorEvento controlador) {
        super("Alta de Tipo de Registro", true, true, true, true);
        this.controlador = controlador;
        setBounds(120, 120, 500, 350);
        setLayout(new BorderLayout());

        // Panel selección evento y edición
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelSeleccion.add(lblEvento);
        panelSeleccion.add(comboEventos);
        JLabel lblEdicion = new JLabel("Edición:");
        comboEdiciones = new JComboBox<>();
        panelSeleccion.add(lblEdicion);
        panelSeleccion.add(comboEdiciones);
        add(panelSeleccion, BorderLayout.NORTH);

        // Panel datos tipo de registro
        JPanel panelDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblNombre = new JLabel("Nombre:");
        txtNombre = new JTextField(15);
        panelDatos.add(lblNombre, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblDescripcion = new JLabel("Descripción:");
        txtDescripcion = new JTextField(15);
        panelDatos.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtDescripcion, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblCosto = new JLabel("Costo:");
        txtCosto = new JTextField(10);
        panelDatos.add(lblCosto, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtCosto, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblCupo = new JLabel("Cupo:");
        txtCupo = new JTextField(10);
        panelDatos.add(lblCupo, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtCupo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        add(panelDatos, BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        comboEventos.addActionListener(e -> cargarEdiciones());
        btnAceptar.addActionListener(e -> altaTipoRegistro());
        btnCancelar.addActionListener(e -> this.dispose());

        cargarEventos();
    }

    private java.util.List<logica.datatypes.DTEvento> eventosDTO;
    public void cargarEventos() {
        try {
            eventosDTO = controlador.listarEventos();
            comboEventos.removeAllItems();
            for (logica.datatypes.DTEvento ev : eventosDTO) {
                comboEventos.addItem(ev.getNombre());
            }
            if (comboEventos.getItemCount() > 0) {
                comboEventos.setSelectedIndex(0);
                cargarEdiciones();
            }
        } catch (IllegalStateException | NullPointerException ex) {
            comboEventos.setModel(new DefaultComboBoxModel<>(new String[]{"No hay eventos"}));
            comboEdiciones.setModel(new DefaultComboBoxModel<>(new String[]{}));
        }
    }

    private void cargarEdiciones() {
        comboEdiciones.removeAllItems();
        int idx = comboEventos.getSelectedIndex();
        if (idx >= 0 && eventosDTO != null && idx < eventosDTO.size()) {
            logica.datatypes.DTEvento evento = eventosDTO.get(idx);
            java.util.List<String> ediciones = evento.getEdiciones();
            for (String ed : ediciones) {
                comboEdiciones.addItem(ed);
            }
        }
    }

    private void altaTipoRegistro() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0 || eventosDTO == null || idxEvento >= eventosDTO.size()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un evento y una edición.");
            return;
        }
        logica.datatypes.DTEvento eventoDTO = eventosDTO.get(idxEvento);
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        logica.clases.Ediciones edicion = controlador.obtenerEdicion(eventoDTO.getNombre(), nombreEdicion);
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String costoStr = txtCosto.getText().trim();
        String cupoStr = txtCupo.getText().trim();
        if (nombre.isEmpty() || descripcion.isEmpty() || costoStr.isEmpty() || cupoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }
        float costo;
        int cupo;
        try {
            costo = Float.parseFloat(costoStr);
            cupo = Integer.parseInt(cupoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Costo debe ser un número decimal y cupo debe ser un número entero.");
            return;
        }
        try {
            controlador.altaTipoRegistro(edicion, nombre, descripcion, costo, cupo);
            JOptionPane.showMessageDialog(this, "Tipo de registro creado exitosamente.");
            dispose();
        } catch (TipoRegistroYaExisteException ex) {
			JOptionPane.showMessageDialog(this, "El tipo de registro " + nombre + " ya existe en esta edición.");
        } catch (excepciones.CupoTipoRegistroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, "Cupo inválido: debe ser mayor a 0");
        } catch (excepciones.CostoTipoRegistroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, "Costo inválido: debe ser mayor o igual a 0");
        } catch (IllegalStateException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}