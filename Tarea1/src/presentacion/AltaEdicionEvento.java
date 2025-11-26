package presentacion;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import java.time.LocalDate;
import com.toedter.calendar.JDateChooser;

import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

public class AltaEdicionEvento extends JInternalFrame {
	private JComboBox<String> comboEvento;
	private JComboBox<String> comboOrganizador;
	private logica.interfaces.IControladorUsuario controladorUsuario;
	private JDateChooser dateChooserInicio;
	private JDateChooser dateChooserFin;
	private JDateChooser dateChooserAlta;
	
	public AltaEdicionEvento(IControladorUsuario controladorUsuario, IControladorEvento iCE) {
	    super("Alta de Edición de Evento", true, true, true, true);
	    this.controladorUsuario = controladorUsuario;
	    setBounds(new Rectangle(60, 60, 600, 450));
	    setVisible(true);

	    GridBagLayout gridBagLayout = new GridBagLayout();
	    gridBagLayout.columnWidths = new int[3];
	    gridBagLayout.rowHeights = new int[11];
	    gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
	    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
	    getContentPane().setLayout(gridBagLayout);

	    // Campos previos (idénticos a los tuyos)
	    JLabel lblEvento = new JLabel("Evento:");
	    GridBagConstraints gbc_lblEvento = new GridBagConstraints();
	    gbc_lblEvento.insets = new Insets(0, 0, 5, 5);
	    gbc_lblEvento.anchor = GridBagConstraints.WEST;
	    gbc_lblEvento.gridx = 0;
	    gbc_lblEvento.gridy = 0;
	    getContentPane().add(lblEvento, gbc_lblEvento);

	    comboEvento = new JComboBox<>();
	    GridBagConstraints gbc_comboEvento = new GridBagConstraints();
	    gbc_comboEvento.insets = new Insets(0, 0, 5, 0);
	    gbc_comboEvento.fill = GridBagConstraints.HORIZONTAL;
	    gbc_comboEvento.gridx = 1;
	    gbc_comboEvento.gridy = 0;
	    getContentPane().add(comboEvento, gbc_comboEvento);

	    JLabel lblOrganizador = new JLabel("Organizador:");
	    GridBagConstraints gbc_lblOrganizador = new GridBagConstraints();
	    gbc_lblOrganizador.insets = new Insets(0, 0, 5, 5);
	    gbc_lblOrganizador.anchor = GridBagConstraints.WEST;
	    gbc_lblOrganizador.gridx = 0;
	    gbc_lblOrganizador.gridy = 1;
	    getContentPane().add(lblOrganizador, gbc_lblOrganizador);

	    comboOrganizador = new JComboBox<>();
	    GridBagConstraints gbc_comboOrganizador = new GridBagConstraints();
	    gbc_comboOrganizador.insets = new Insets(0, 0, 5, 0);
	    gbc_comboOrganizador.fill = GridBagConstraints.HORIZONTAL;
	    gbc_comboOrganizador.gridx = 1;
	    gbc_comboOrganizador.gridy = 1;
	    getContentPane().add(comboOrganizador, gbc_comboOrganizador);

	    JLabel lblNombre = new JLabel("Nombre de Edición:");
	    GridBagConstraints gbc_lblNombre = new GridBagConstraints();
	    gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
	    gbc_lblNombre.anchor = GridBagConstraints.WEST;
	    gbc_lblNombre.gridx = 0;
	    gbc_lblNombre.gridy = 2;
	    getContentPane().add(lblNombre, gbc_lblNombre);

	    JTextField txtNombre = new JTextField();
	    GridBagConstraints gbc_txtNombre = new GridBagConstraints();
	    gbc_txtNombre.insets = new Insets(0, 0, 5, 0);
	    gbc_txtNombre.fill = GridBagConstraints.HORIZONTAL;
	    gbc_txtNombre.gridx = 1;
	    gbc_txtNombre.gridy = 2;
	    getContentPane().add(txtNombre, gbc_txtNombre);
	    txtNombre.setColumns(15);

	    JLabel lblSigla = new JLabel("Sigla:");
	    GridBagConstraints gbc_lblSigla = new GridBagConstraints();
	    gbc_lblSigla.insets = new Insets(0, 0, 5, 5);
	    gbc_lblSigla.anchor = GridBagConstraints.WEST;
	    gbc_lblSigla.gridx = 0;
	    gbc_lblSigla.gridy = 3;
	    getContentPane().add(lblSigla, gbc_lblSigla);

	    JTextField txtSigla = new JTextField();
	    GridBagConstraints gbc_txtSigla = new GridBagConstraints();
	    gbc_txtSigla.insets = new Insets(0, 0, 5, 0);
	    gbc_txtSigla.fill = GridBagConstraints.HORIZONTAL;
	    gbc_txtSigla.gridx = 1;
	    gbc_txtSigla.gridy = 3;
	    getContentPane().add(txtSigla, gbc_txtSigla);
	    txtSigla.setColumns(10);

	    JLabel lblCiudad = new JLabel("Ciudad:");
	    GridBagConstraints gbc_lblCiudad = new GridBagConstraints();
	    gbc_lblCiudad.insets = new Insets(0, 0, 5, 5);
	    gbc_lblCiudad.anchor = GridBagConstraints.WEST;
	    gbc_lblCiudad.gridx = 0;
	    gbc_lblCiudad.gridy = 4;
	    getContentPane().add(lblCiudad, gbc_lblCiudad);

	    JTextField txtCiudad = new JTextField();
	    GridBagConstraints gbc_txtCiudad = new GridBagConstraints();
	    gbc_txtCiudad.insets = new Insets(0, 0, 5, 0);
	    gbc_txtCiudad.fill = GridBagConstraints.HORIZONTAL;
	    gbc_txtCiudad.gridx = 1;
	    gbc_txtCiudad.gridy = 4;
	    getContentPane().add(txtCiudad, gbc_txtCiudad);
	    txtCiudad.setColumns(10);

	    JLabel lblPais = new JLabel("País:");
	    GridBagConstraints gbc_lblPais = new GridBagConstraints();
	    gbc_lblPais.insets = new Insets(0, 0, 5, 5);
	    gbc_lblPais.anchor = GridBagConstraints.WEST;
	    gbc_lblPais.gridx = 0;
	    gbc_lblPais.gridy = 5;
	    getContentPane().add(lblPais, gbc_lblPais);

	    JTextField txtPais = new JTextField();
	    GridBagConstraints gbc_txtPais = new GridBagConstraints();
	    gbc_txtPais.insets = new Insets(0, 0, 5, 0);
	    gbc_txtPais.fill = GridBagConstraints.HORIZONTAL;
	    gbc_txtPais.gridx = 1;
	    gbc_txtPais.gridy = 5;
	    getContentPane().add(txtPais, gbc_txtPais);
	    txtPais.setColumns(10);

	    JLabel lblFechaInicio = new JLabel("Fecha de inicio:");
	    GridBagConstraints gbc_lblFechaInicio = new GridBagConstraints();
	    gbc_lblFechaInicio.insets = new Insets(0, 0, 5, 5);
	    gbc_lblFechaInicio.anchor = GridBagConstraints.WEST;
	    gbc_lblFechaInicio.gridx = 0;
	    gbc_lblFechaInicio.gridy = 6;
	    getContentPane().add(lblFechaInicio, gbc_lblFechaInicio);

	    dateChooserInicio = new JDateChooser();
	    GridBagConstraints gbc_dateChooserInicio = new GridBagConstraints();
	    gbc_dateChooserInicio.insets = new Insets(0, 0, 5, 0);
	    gbc_dateChooserInicio.fill = GridBagConstraints.HORIZONTAL;
	    gbc_dateChooserInicio.gridx = 1;
	    gbc_dateChooserInicio.gridy = 6;
	    getContentPane().add(dateChooserInicio, gbc_dateChooserInicio);

	    JLabel lblFechaFin = new JLabel("Fecha de fin:");
	    GridBagConstraints gbc_lblFechaFin = new GridBagConstraints();
	    gbc_lblFechaFin.insets = new Insets(0, 0, 5, 5);
	    gbc_lblFechaFin.anchor = GridBagConstraints.WEST;
	    gbc_lblFechaFin.gridx = 0;
	    gbc_lblFechaFin.gridy = 7;
	    getContentPane().add(lblFechaFin, gbc_lblFechaFin);

	    dateChooserFin = new JDateChooser();
	    GridBagConstraints gbc_dateChooserFin = new GridBagConstraints();
	    gbc_dateChooserFin.insets = new Insets(0, 0, 5, 0);
	    gbc_dateChooserFin.fill = GridBagConstraints.HORIZONTAL;
	    gbc_dateChooserFin.gridx = 1;
	    gbc_dateChooserFin.gridy = 7;
	    getContentPane().add(dateChooserFin, gbc_dateChooserFin);

	    JLabel lblFechaAlta = new JLabel("Fecha de alta:");
	    GridBagConstraints gbc_lblFechaAlta = new GridBagConstraints();
	    gbc_lblFechaAlta.insets = new Insets(0, 0, 5, 5);
	    gbc_lblFechaAlta.anchor = GridBagConstraints.WEST;
	    gbc_lblFechaAlta.gridx = 0;
	    gbc_lblFechaAlta.gridy = 8;
	    getContentPane().add(lblFechaAlta, gbc_lblFechaAlta);

	    dateChooserAlta = new JDateChooser();
	    GridBagConstraints gbc_dateChooserAlta = new GridBagConstraints();
	    gbc_dateChooserAlta.insets = new Insets(0, 0, 5, 0);
	    gbc_dateChooserAlta.fill = GridBagConstraints.HORIZONTAL;
	    gbc_dateChooserAlta.gridx = 1;
	    gbc_dateChooserAlta.gridy = 8;
	    getContentPane().add(dateChooserAlta, gbc_dateChooserAlta);

	    // --- NUEVO: seleccionar imagen ---
	    JLabel lblImagen = new JLabel("Imagen:");
	    GridBagConstraints gbc_lblImagen = new GridBagConstraints();
	    gbc_lblImagen.insets = new Insets(0, 0, 5, 5);
	    gbc_lblImagen.anchor = GridBagConstraints.WEST;
	    gbc_lblImagen.gridx = 0;
	    gbc_lblImagen.gridy = 9;
	    getContentPane().add(lblImagen, gbc_lblImagen);

	    JLabel lblArchivo = new JLabel("Ninguna seleccionada");
	    JButton btnImagen = new JButton("Seleccionar...");
	    JPanel panelImg = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
	    panelImg.add(lblArchivo);
	    panelImg.add(btnImagen);

	    GridBagConstraints gbc_panelImg = new GridBagConstraints();
	    gbc_panelImg.insets = new Insets(0, 0, 5, 0);
	    gbc_panelImg.fill = GridBagConstraints.HORIZONTAL;
	    gbc_panelImg.gridx = 1;
	    gbc_panelImg.gridy = 9;
	    getContentPane().add(panelImg, gbc_panelImg);

	    final String[] imagenSeleccionada = {null};
	    btnImagen.addActionListener(e -> {
	        JFileChooser fileSelected = new JFileChooser();
	        fileSelected.setDialogTitle("Seleccionar imagen de la edición");
	        fileSelected.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes JPG y PNG", "jpg", "jpeg", "png"));
	        if (fileSelected.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	            java.io.File archivo = fileSelected.getSelectedFile();
	            imagenSeleccionada[0] = archivo.getName(); // o archivo.getAbsolutePath() si preferís
	            lblArchivo.setText(archivo.getName());
	        }
	    });

	    // --- Botones ---
	    JButton btnAceptar = new JButton("Aceptar");
	    JButton btnCancelar = new JButton("Cancelar");

	    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    panelBotones.add(btnAceptar);
	    panelBotones.add(btnCancelar);

	    GridBagConstraints gbc_panelBotones = new GridBagConstraints();
	    gbc_panelBotones.gridx = 1;
	    gbc_panelBotones.gridy = 10;
	    gbc_panelBotones.gridwidth = 2;
	    gbc_panelBotones.insets = new Insets(10, 5, 5, 5);
	    gbc_panelBotones.anchor = GridBagConstraints.EAST;
	    getContentPane().add(panelBotones, gbc_panelBotones);

	    // Acción aceptar
	    btnAceptar.addActionListener(ev -> {
	        try {
	            String eventoNombre = (String) comboEvento.getSelectedItem();
	            String organizadorNick = (String) comboOrganizador.getSelectedItem();
	            String nombre = txtNombre.getText().trim();
	            String sigla = txtSigla.getText().trim();
	            String ciudad = txtCiudad.getText().trim();
	            String pais = txtPais.getText().trim();

	            java.util.Date fechaInicioSelected = dateChooserInicio.getDate();
	            java.util.Date fechaFinalSelected = dateChooserFin.getDate();
	            java.util.Date fechaAltaSelected = dateChooserAlta.getDate();

	            if (eventoNombre == null || organizadorNick == null || nombre.isEmpty() ||
	                sigla.isEmpty() || ciudad.isEmpty() || pais.isEmpty() ||
	                fechaInicioSelected == null || fechaFinalSelected == null || fechaAltaSelected == null) {
	                JOptionPane.showMessageDialog(this, "Todos los campos deben estar completos.");
	                return;
	            }

	            LocalDate fInicio = fechaInicioSelected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	            LocalDate fFin = fechaFinalSelected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	            LocalDate fAlta = fechaAltaSelected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

	            logica.controladores.ControladorEvento controladorEvento = new logica.controladores.ControladorEvento();
	            logica.manejadores.ManejadorEvento manejadorEvento = logica.manejadores.ManejadorEvento.getInstancia();
	            logica.clases.Eventos evento = manejadorEvento.obtenerEvento(eventoNombre);
	            logica.clases.Organizador organizador = controladorUsuario.listarOrganizadores().get(organizadorNick);

	            if (evento == null || organizador == null) {
	                JOptionPane.showMessageDialog(this, "No se pudo encontrar el evento u organizador seleccionado.");
	                return;
	            }

	            controladorEvento.altaEdicionEvento(
	                evento, organizador, nombre, sigla, "",
	                fInicio, fFin, fAlta, ciudad, pais,
	                imagenSeleccionada[0]  // <<--- nueva imagen o null
	            );

	            JOptionPane.showMessageDialog(this, "Edición registrada con éxito.");
	            this.dispose();

	        } catch (excepciones.FechasCruzadasException ex) {
	            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de fechas", JOptionPane.ERROR_MESSAGE);
	        } catch (excepciones.EdicionYaExisteException ex) {
	            JOptionPane.showMessageDialog(this, "Ya existe una edición con el nombre: '" + ex.getMessage() + "'");
	        } catch (excepciones.EventoYaExisteException ex) {
	            JOptionPane.showMessageDialog(this, "Ya existe un evento con ese nombre o sigla.", "Error", JOptionPane.ERROR_MESSAGE);
	        } catch (IllegalStateException | NullPointerException ex) {
	            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    btnCancelar.addActionListener(ev -> this.dispose());
	}

	public void cargarEventos() {
	    logica.controladores.ControladorEvento controlador = new logica.controladores.ControladorEvento();
	    java.util.List<logica.datatypes.DTEvento> eventos;
	    try {
//	        eventos = controlador.listarEventos();
	    	eventos = controlador.listarEventosVigentes();
	        
	    } catch (IllegalStateException | NullPointerException ex) {
	        eventos = java.util.Collections.emptyList();
	    }

	    comboEvento.removeAllItems();
	    if (eventos.isEmpty()) {
	        comboEvento.setModel(new DefaultComboBoxModel<>(new String[]{"No hay eventos"}));
	        return;
	    }

	    for (logica.datatypes.DTEvento ev : eventos) {
	        comboEvento.addItem(ev.getNombre());
	    }
	    comboEvento.setSelectedIndex(0);
	}


	public void cargarOrganizadores() {
	    comboOrganizador.removeAllItems();

	    if (controladorUsuario == null) {
	        comboOrganizador.setModel(new DefaultComboBoxModel<>(new String[]{"No hay organizadores"}));
	        return;
	    }

	    java.util.Map<String, logica.clases.Organizador> organizadores =
	            java.util.Collections.emptyMap();

	    // Intentamos listar sin capturar Exception genérico (Checkstyle-safe)
	    try {
	        organizadores = controladorUsuario.listarOrganizadores();
	    } catch (IllegalStateException | NullPointerException ex) {
	        // Control de errores específicos, permitido por Checkstyle
	        organizadores = java.util.Collections.emptyMap();
	    }

	    if (organizadores.isEmpty()) {
	        comboOrganizador.setModel(new DefaultComboBoxModel<>(new String[]{"No hay organizadores"}));
	        return;
	    }

	    for (String nick : organizadores.keySet()) {
	        comboOrganizador.addItem(nick);
	    }

	    if (comboOrganizador.getItemCount() > 0) {
	        comboOrganizador.setSelectedIndex(0);
	    }
	}




}