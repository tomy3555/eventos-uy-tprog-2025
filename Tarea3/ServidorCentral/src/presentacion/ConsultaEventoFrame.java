package presentacion;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;

import java.lang.reflect.Field;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import logica.datatypes.DTEvento;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

public class ConsultaEventoFrame extends JInternalFrame {
    private IControladorEvento controladorEvento;
    private JComboBox<String> comboEventos;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField lblCategorias;
    private JComboBox<String> comboEdiciones;
    private IControladorUsuario controladorUsuario;
    private String[][] datosEventos;
    private String[][] categoriasEventos;
    private String[][] edicionesEventos;
    private JTextField txtFecha;
    private JTextField txtSigla;

    // imagen
    private JLabel lblImagenEvento;
    private String[] imagenesEventos; // paralelo a combo

    public ConsultaEventoFrame(IControladorUsuario iCU, IControladorEvento controladorEvento) {
        super("Consulta de Evento", true, true, true, true);
        this.controladorEvento = controladorEvento;
        this.controladorUsuario = iCU;
        setBounds(100, 100, 720, 460);
        setLayout(new BorderLayout());

        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelSeleccion.add(lblEvento);
        panelSeleccion.add(comboEventos);
        add(panelSeleccion, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        add(centro, BorderLayout.CENTER);

        JPanel panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        centro.add(panelDatos, BorderLayout.CENTER);

        JPanel panelCampos = new JPanel(new GridBagLayout());
        // Nombre
        GridBagConstraints gbcNombreLabel = new GridBagConstraints();
        gbcNombreLabel.insets = new Insets(5, 5, 5, 5);
        gbcNombreLabel.anchor = GridBagConstraints.WEST;
        gbcNombreLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcNombreLabel.gridx = 0;
        gbcNombreLabel.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbcNombreLabel);

        GridBagConstraints gbcNombreField = new GridBagConstraints();
        gbcNombreField.insets = new Insets(5, 5, 5, 5);
        gbcNombreField.anchor = GridBagConstraints.WEST;
        gbcNombreField.fill = GridBagConstraints.HORIZONTAL;
        gbcNombreField.gridx = 1;
        gbcNombreField.gridy = 0;
        txtNombre = new JTextField();
        txtNombre.setEditable(false);
        txtNombre.setColumns(20);
        panelCampos.add(txtNombre, gbcNombreField);

        // Descripción
        GridBagConstraints gbcDescLabel = new GridBagConstraints();
        gbcDescLabel.insets = new Insets(5, 5, 5, 5);
        gbcDescLabel.anchor = GridBagConstraints.WEST;
        gbcDescLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcDescLabel.gridx = 0;
        gbcDescLabel.gridy = 1;
        panelCampos.add(new JLabel("Descripción:"), gbcDescLabel);

        GridBagConstraints gbcDescField = new GridBagConstraints();
        gbcDescField.insets = new Insets(5, 5, 5, 5);
        gbcDescField.anchor = GridBagConstraints.WEST;
        gbcDescField.fill = GridBagConstraints.HORIZONTAL;
        gbcDescField.gridx = 1;
        gbcDescField.gridy = 1;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(txtNombre.getBackground());
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelCampos.add(scrollDesc, gbcDescField);

        // Categorías
        GridBagConstraints gbcCatLabel = new GridBagConstraints();
        gbcCatLabel.insets = new Insets(5, 5, 5, 5);
        gbcCatLabel.anchor = GridBagConstraints.WEST;
        gbcCatLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcCatLabel.gridx = 0;
        gbcCatLabel.gridy = 2;
        panelCampos.add(new JLabel("Categorías:"), gbcCatLabel);

        GridBagConstraints gbcCatField = new GridBagConstraints();
        gbcCatField.insets = new Insets(5, 5, 5, 5);
        gbcCatField.anchor = GridBagConstraints.WEST;
        gbcCatField.fill = GridBagConstraints.HORIZONTAL;
        gbcCatField.gridx = 1;
        gbcCatField.gridy = 2;
        JTextField txtCategorias = new JTextField();
        txtCategorias.setEditable(false);
        txtCategorias.setColumns(20);
        panelCampos.add(txtCategorias, gbcCatField);
        this.lblCategorias = txtCategorias;

        // Fecha
        GridBagConstraints gbcFechaLabel = new GridBagConstraints();
        gbcFechaLabel.insets = new Insets(5, 5, 5, 5);
        gbcFechaLabel.anchor = GridBagConstraints.WEST;
        gbcFechaLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcFechaLabel.gridx = 0;
        gbcFechaLabel.gridy = 3;
        panelCampos.add(new JLabel("Fecha:"), gbcFechaLabel);

        GridBagConstraints gbcFechaField = new GridBagConstraints();
        gbcFechaField.insets = new Insets(5, 5, 5, 5);
        gbcFechaField.anchor = GridBagConstraints.WEST;
        gbcFechaField.fill = GridBagConstraints.HORIZONTAL;
        gbcFechaField.gridx = 1;
        gbcFechaField.gridy = 3;
        txtFecha = new JTextField();
        txtFecha.setEditable(false);
        txtFecha.setColumns(20);
        panelCampos.add(txtFecha, gbcFechaField);

        // Sigla
        GridBagConstraints gbcSiglaLabel = new GridBagConstraints();
        gbcSiglaLabel.insets = new Insets(5, 5, 5, 5);
        gbcSiglaLabel.anchor = GridBagConstraints.WEST;
        gbcSiglaLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcSiglaLabel.gridx = 0;
        gbcSiglaLabel.gridy = 4;
        panelCampos.add(new JLabel("Sigla:"), gbcSiglaLabel);

        GridBagConstraints gbcSiglaField = new GridBagConstraints();
        gbcSiglaField.insets = new Insets(5, 5, 5, 5);
        gbcSiglaField.anchor = GridBagConstraints.WEST;
        gbcSiglaField.fill = GridBagConstraints.HORIZONTAL;
        gbcSiglaField.gridx = 1;
        gbcSiglaField.gridy = 4;
        txtSigla = new JTextField();
        txtSigla.setEditable(false);
        txtSigla.setColumns(20);
        panelCampos.add(txtSigla, gbcSiglaField);

        panelDatos.add(panelCampos);

        panelDatos.add(Box.createVerticalStrut(10));
        JPanel panelEdiciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEdiciones = new JLabel("Ediciones:");
        comboEdiciones = new JComboBox<>();
        panelEdiciones.add(lblEdiciones);
        panelEdiciones.add(comboEdiciones);
        panelDatos.add(panelEdiciones);

        // Panel lateral derecho: imagen
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblImagenEvento = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblImagenEvento.setBorder(BorderFactory.createTitledBorder("Imagen del evento"));
        lblImagenEvento.setPreferredSize(new Dimension(240, 180));
        derecha.add(lblImagenEvento, BorderLayout.NORTH);

        centro.add(derecha, BorderLayout.EAST);

        comboEventos.addActionListener(e -> mostrarDatosEvento());
        comboEdiciones.addActionListener(e -> abrirConsultaEdicion());
    }

    public void cargarEventos() {
        try {
            java.util.List<DTEvento> eventos = controladorEvento.listarEventos();
            String[] eventosArr = new String[eventos.size()];
            datosEventos = new String[eventos.size()][3];
            categoriasEventos = new String[eventos.size()][];
            edicionesEventos = new String[eventos.size()][];
            imagenesEventos = new String[eventos.size()];

            for (int i = 0; i < eventos.size(); i++) {
                DTEvento evento = eventos.get(i);
                eventosArr[i] = evento.getNombre();
                datosEventos[i][0] = evento.getNombre();
                datosEventos[i][1] = evento.getDescripcion();
                datosEventos[i][2] = evento.getFecha().toString();
                categoriasEventos[i] = evento.getCategorias().toArray(new String[0]);
                edicionesEventos[i] = evento.getEdiciones().toArray(new String[0]);
                // imagen
                try {
                    imagenesEventos[i] = (String) DTEvento.class.getMethod("getImagen").invoke(evento);
                } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException
                        | IllegalStateException | NullPointerException ex) {
                    imagenesEventos[i] = null;
                }

            }
            comboEventos.setModel(new DefaultComboBoxModel<>(eventosArr));
            comboEventos.revalidate();
            comboEventos.repaint();
            if (eventosArr.length > 0) {
                comboEventos.setSelectedIndex(0);
                mostrarDatosEvento();
            } else {
                txtNombre.setText("");
                txtDescripcion.setText("");
                txtFecha.setText("");
                lblCategorias.setText("");
                txtSigla.setText("");
                comboEdiciones.setModel(new DefaultComboBoxModel<>(new String[]{}));
                lblImagenEvento.setIcon(null);
                lblImagenEvento.setText("Sin imagen");
            }
        } catch (IllegalStateException | NullPointerException ex) {
            comboEventos.setModel(new DefaultComboBoxModel<>(new String[]{"No hay eventos"}));
            comboEventos.revalidate();
            comboEventos.repaint();
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtFecha.setText("");
            lblCategorias.setText("");
            txtSigla.setText("");
            comboEdiciones.setModel(new DefaultComboBoxModel<>(new String[]{}));
            lblImagenEvento.setIcon(null);
            lblImagenEvento.setText("Sin imagen");
        }
    }

    private void mostrarDatosEvento() {
        int idx = comboEventos.getSelectedIndex();
        if (idx < 0 || datosEventos == null || idx >= datosEventos.length) {
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtFecha.setText("");
            lblCategorias.setText("");
            txtSigla.setText("");
            comboEdiciones.setModel(new DefaultComboBoxModel<>(new String[]{}));
            lblImagenEvento.setIcon(null);
            lblImagenEvento.setText("Sin imagen");
            return;
        }
        txtNombre.setText(datosEventos[idx][0]);
        txtDescripcion.setText(datosEventos[idx][1]);
        txtFecha.setText(datosEventos[idx][2]);
        StringBuilder cats = new StringBuilder();
        for (String cat : categoriasEventos[idx]) {
            cats.append(cat).append(", ");
        }
        if (cats.length() > 2) cats.setLength(cats.length() - 2);
        lblCategorias.setText(cats.toString());

        // Sigla
        String sigla = "";
        try {
            java.util.List<DTEvento> eventos = controladorEvento.listarEventos();
            if (idx < eventos.size()) {
                sigla = eventos.get(idx).getSigla();
            }
        } catch (IllegalStateException | NullPointerException ex) {
            sigla = "";
        }
        txtSigla.setText(sigla);

        comboEdiciones.setModel(new DefaultComboBoxModel<>(edicionesEventos[idx]));
        comboEdiciones.revalidate();
        comboEdiciones.repaint();

        // Imagen
        String img = (imagenesEventos != null && idx < imagenesEventos.length) ? imagenesEventos[idx] : null;
        ImageIcon icon = loadIcon(img, 260, 180);
        lblImagenEvento.setIcon(icon);
        lblImagenEvento.setText(icon == null ? "Sin imagen" : null);
    }

    private void abrirConsultaEdicion() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEd = comboEdiciones.getSelectedIndex();
        if (idxEvento < 0 || idxEd < 0 || edicionesEventos == null
                || idxEvento >= edicionesEventos.length
                || idxEd >= edicionesEventos[idxEvento].length) {
            return;
        }

        String nombreEvento  = comboEventos.getItemAt(idxEvento);
        String nombreEdicion = edicionesEventos[idxEvento][idxEd];

        logica.clases.Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la edición seleccionada.");
            return;
        }
        String sigla = edicion.getSigla();
        controladorEvento.seleccionarEdicion(sigla);

        ConsultaEdicionEventoFrame frameEdicion =
                new ConsultaEdicionEventoFrame(controladorUsuario, controladorEvento, sigla);

        JDesktopPane desktop = getDesktopPane();
        if (desktop != null) {
            desktop.add(frameEdicion);
            frameEdicion.setVisible(true);
            frameEdicion.toFront();
        } else {
            frameEdicion.setVisible(true);
        }
    }
    
    public void preseleccionarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return;

        // 1) JComboBox<String> comboEventos
        try {
            Field f = this.getClass().getDeclaredField("comboEventos");
            f.setAccessible(true);
            Object val = f.get(this);
            if (val instanceof JComboBox) {
                @SuppressWarnings("unchecked")
                JComboBox<Object> combo = (JComboBox<Object>) val;
                int idx = -1;
                for (int i = 0; i < combo.getItemCount(); i++) {
                    Object it = combo.getItemAt(i);
                    if (it != null && nombre.equalsIgnoreCase(String.valueOf(it))) {
                        idx = i; break;
                    }
                }
                if (idx >= 0) {
                    combo.setSelectedIndex(idx);
                    // si tu frame expone una acción para consultar/mostrar:
                    invocarSiExiste("consultarSeleccion");
                    return;
                }
            }
        } catch (NoSuchFieldException ignore) {
            // sigue probando
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2) JTable tablaEventos (nombre en col 0 o 1)
        try {
            Field f = this.getClass().getDeclaredField("tablaEventos");
            f.setAccessible(true);
            Object val = f.get(this);
            if (val instanceof JTable) {
                JTable tabla = (JTable) val;
                TableModel model = tabla.getModel();
                int nameCol = model.getColumnCount() > 1 ? 1 : 0; // heurística común
                int found = -1;
                for (int r = 0; r < model.getRowCount(); r++) {
                    Object cell = model.getValueAt(r, Math.min(nameCol, model.getColumnCount() - 1));
                    if (cell != null && nombre.equalsIgnoreCase(String.valueOf(cell))) {
                        found = r; break;
                    }
                }
                if (found >= 0) {
                    int viewRow = tabla.convertRowIndexToView(found);
                    tabla.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                    tabla.scrollRectToVisible(tabla.getCellRect(viewRow, 0, true));
                    invocarSiExiste("consultarSeleccion");
                    return;
                }
            }
        } catch (NoSuchFieldException ignore) {
            // sigue probando
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3) JList<String> listaEventos
        try {
            Field f = this.getClass().getDeclaredField("listaEventos");
            f.setAccessible(true);
            Object val = f.get(this);
            if (val instanceof JList) {
                @SuppressWarnings("unchecked")
                JList<Object> lista = (JList<Object>) val;
                ListModel<Object> model = lista.getModel();
                int idx = -1;
                for (int i = 0; i < model.getSize(); i++) {
                    Object it = model.getElementAt(i);
                    if (it != null && nombre.equalsIgnoreCase(String.valueOf(it))) {
                        idx = i; break;
                    }
                }
                if (idx >= 0) {
                    lista.setSelectedIndex(idx);
                    lista.ensureIndexIsVisible(idx);
                    invocarSiExiste("consultarSeleccion");
                    return;
                }
            }
        } catch (NoSuchFieldException ignore) {
            // no hay lista, fin
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si no encontró nada, informar de forma amable (opcional)
        JOptionPane.showMessageDialog(
            this,
            "No se pudo preseleccionar el evento \"" + nombre + "\".\n" +
            "Verificá que aparezca en la lista/tabla y que el nombre coincida.",
            "Aviso",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Invoca un método sin parámetros si existe (por ejemplo, para disparar la consulta)
    private void invocarSiExiste(String methodName) {
        try {
            var m = this.getClass().getMethod(methodName);
            m.setAccessible(true);
            m.invoke(this);
        } catch (NoSuchMethodException ignore) {
            // es opcional, no es error
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==== IMÁGENES ====
    private static ImageIcon loadIcon(String imgName, int ancho, int altura) {
        if (imgName == null || imgName.isBlank()) return null;
        try {
            String cpPath = "img/" + imgName;
            java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(cpPath);
            Image base;
            if (url != null) {
                base = new ImageIcon(url).getImage();
            } else {
                java.io.File fileAux1 = new java.io.File("src/img/" + imgName);
                java.io.File fileAux2 = new java.io.File("img/" + imgName);
                java.io.File file = fileAux1.exists() ? fileAux1 : (fileAux2.exists() ? fileAux2 : null);
                if (file == null) return null;
                base = new ImageIcon(file.getAbsolutePath()).getImage();
            }
            Image scaled = base.getScaledInstance(ancho, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IllegalStateException | NullPointerException ex) {
            return null;
        }
    }
}
