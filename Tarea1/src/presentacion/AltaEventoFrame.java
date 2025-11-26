package presentacion;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.Rectangle;
import excepciones.EventoYaExisteException;
import logica.controladores.ControladorEvento;
import logica.datatypes.DTCategorias;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import com.toedter.calendar.JDateChooser;

public class AltaEventoFrame extends JInternalFrame {
    //private Runnable abrirConsultaRunnable;
    //public void setAbrirConsultaRunnable(Runnable runCU) { this.abrirConsultaRunnable = runCU; }
    private JList<String> listCategorias;
    private DefaultListModel<String> listModelCategorias;
    private java.util.List<Boolean> categoriasSeleccionadasFlags;
    
    public void cargarCategorias() {
        listModelCategorias.clear();
        categoriasSeleccionadasFlags = new java.util.ArrayList<>();
        try {
            java.util.List<String> categorias = new java.util.ArrayList<>(logica.manejadores.ManejadorAuxiliar.getInstancia().listarCategorias());
            for (String cat : categorias) {
                listModelCategorias.addElement(cat);
                categoriasSeleccionadasFlags.add(false);
            }
        } catch (IllegalStateException | NullPointerException ex) {
            listModelCategorias.addElement("No se pudieron cargar las categorías.");
            categoriasSeleccionadasFlags.add(false);
        }
    }
    
    public AltaEventoFrame(IControladorUsuario iCU, IControladorEvento iCE) {
        super("Alta de Evento", true, true, true, true);
        setBounds(new Rectangle(50, 50, 550, 400));
        setVisible(true);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[3];
        gridBagLayout.rowHeights = new int[7];
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        GridBagConstraints gbc_lblNombre = new GridBagConstraints();
        gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
        gbc_lblNombre.anchor = GridBagConstraints.WEST;
        gbc_lblNombre.gridx = 0;
        gbc_lblNombre.gridy = 0;
        getContentPane().add(lblNombre, gbc_lblNombre);

        JTextField txtNombre = new JTextField();
        GridBagConstraints gbc_txtNombre = new GridBagConstraints();
        gbc_txtNombre.insets = new Insets(0, 0, 5, 0);
        gbc_txtNombre.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtNombre.gridx = 1;
        gbc_txtNombre.gridy = 0;
        getContentPane().add(txtNombre, gbc_txtNombre);
        txtNombre.setColumns(15);

        // Descripción
        JLabel lblDescripcion = new JLabel("Descripción:");
        GridBagConstraints gbc_lblDescripcion = new GridBagConstraints();
        gbc_lblDescripcion.insets = new Insets(0, 0, 5, 5);
        gbc_lblDescripcion.anchor = GridBagConstraints.WEST;
        gbc_lblDescripcion.gridx = 0;
        gbc_lblDescripcion.gridy = 1;
        getContentPane().add(lblDescripcion, gbc_lblDescripcion);

        JTextField txtDescripcion = new JTextField();
        GridBagConstraints gbc_txtDescripcion = new GridBagConstraints();
        gbc_txtDescripcion.insets = new Insets(0, 0, 5, 0);
        gbc_txtDescripcion.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDescripcion.gridx = 1;
        gbc_txtDescripcion.gridy = 1;
        getContentPane().add(txtDescripcion, gbc_txtDescripcion);
        txtDescripcion.setColumns(15);

        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        GridBagConstraints gbc_lblFecha = new GridBagConstraints();
        gbc_lblFecha.insets = new Insets(0, 0, 5, 5);
        gbc_lblFecha.anchor = GridBagConstraints.WEST;
        gbc_lblFecha.gridx = 0;
        gbc_lblFecha.gridy = 2;
        getContentPane().add(lblFecha, gbc_lblFecha);

        JDateChooser dateChooserFecha = new JDateChooser();
        GridBagConstraints gbc_dateChooserFecha = new GridBagConstraints();
        gbc_dateChooserFecha.insets = new Insets(0, 0, 5, 0);
        gbc_dateChooserFecha.fill = GridBagConstraints.HORIZONTAL;
        gbc_dateChooserFecha.gridx = 1;
        gbc_dateChooserFecha.gridy = 2;
        getContentPane().add(dateChooserFecha, gbc_dateChooserFecha);

        // Sigla
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

        // Categorías
        JLabel lblCategoria = new JLabel("Categorías:");
        GridBagConstraints gbc_lblCategoria = new GridBagConstraints();
        gbc_lblCategoria.insets = new Insets(0, 0, 5, 5);
        gbc_lblCategoria.anchor = GridBagConstraints.WEST;
        gbc_lblCategoria.gridx = 0;
        gbc_lblCategoria.gridy = 4;
        getContentPane().add(lblCategoria, gbc_lblCategoria);

        listModelCategorias = new DefaultListModel<>();
        listCategorias = new JList<>(listModelCategorias);
        listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollCategorias = new JScrollPane(listCategorias);
        scrollCategorias.setPreferredSize(new Dimension(200, 80));
        GridBagConstraints gbc_listCategorias = new GridBagConstraints();
        gbc_listCategorias.gridx = 1;
        gbc_listCategorias.gridy = 4;
        gbc_listCategorias.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(scrollCategorias, gbc_listCategorias);

        cargarCategorias();

        // --- NUEVO: Seleccionar imagen ---
        JLabel lblImagen = new JLabel("Imagen:");
        GridBagConstraints gbc_lblImagen = new GridBagConstraints();
        gbc_lblImagen.insets = new Insets(0, 0, 5, 5);
        gbc_lblImagen.anchor = GridBagConstraints.WEST;
        gbc_lblImagen.gridx = 0;
        gbc_lblImagen.gridy = 5;
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
        gbc_panelImg.gridy = 5;
        getContentPane().add(panelImg, gbc_panelImg);

        final String[] imagenSeleccionada = {null};
        btnImagen.addActionListener(e -> {
            JFileChooser fileSelected = new JFileChooser();
            fileSelected.setDialogTitle("Seleccionar imagen del evento");
            fileSelected.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes JPG y PNG", "jpg", "jpeg", "png"));
            if (fileSelected.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = fileSelected.getSelectedFile();
                imagenSeleccionada[0] = archivo.getName();
                lblArchivo.setText(archivo.getName());
            }
        });

        // Botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        GridBagConstraints gbc_panelBotones = new GridBagConstraints();
        gbc_panelBotones.gridx = 1;
        gbc_panelBotones.gridy = 6;
        gbc_panelBotones.gridwidth = 2;
        gbc_panelBotones.insets = new Insets(10, 5, 5, 5);
        gbc_panelBotones.anchor = GridBagConstraints.EAST;
        getContentPane().add(panelBotones, gbc_panelBotones);

        // Acción aceptar
        btnAceptar.addActionListener(ev -> {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            java.util.Date fechaDate = dateChooserFecha.getDate();
            String sigla = txtSigla.getText().trim();

            java.util.List<String> categoriasSeleccionadas = listCategorias.getSelectedValuesList();

            if (nombre.isEmpty() || descripcion.isEmpty() || fechaDate == null || sigla.isEmpty() || categoriasSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos deben estar completos y debe seleccionar al menos una categoría.");
                return;
            }

            try {
                java.time.LocalDate fechaAlta = fechaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                ControladorEvento controlador = new ControladorEvento();
                DTCategorias dtCategorias = new DTCategorias(categoriasSeleccionadas);

                controlador.altaEvento(nombre, descripcion, fechaAlta, sigla, dtCategorias, imagenSeleccionada[0]);

                JOptionPane.showMessageDialog(this, "Evento registrado con éxito.");
                this.dispose();

            } catch (EventoYaExisteException ex) {
                JOptionPane.showMessageDialog(this, "Ya existe un evento con el nombre: '" + nombre + "'.",
                    "Error - Evento ya existente", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar el evento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(ev -> this.dispose());
    }

}