package presentacion;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.BorderFactory;

import javax.swing.JScrollPane;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.GridLayout;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import logica.clases.Ediciones;
import logica.clases.Patrocinio;
import logica.clases.TipoRegistro;
import logica.datatypes.DTEvento;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

public class ConsultaEdicionEventoFrame extends JInternalFrame {

    // private final IControladorUsuario icu;
    private final IControladorEvento  ice;

    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;

    private JTextField txtNombreEdicion;
    private JTextField txtSigla;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtFechaAlta;
    private JTextField txtCiudad;
    private JTextField txtPais;
    private JTextField txtOrganizador;

    private JComboBox<String> comboTiposRegistro;
    private JComboBox<String> comboPatrocinios;

    private String[][] edicionesEventos;
    private boolean cambiando = false;
    private JPanel panelGridRegistros;
    private JScrollPane scrollGridRegistros;

    // Imagen
    private JLabel lblImagenEdicion;

    /**
     * @wbp.parser.constructor
     */
    public ConsultaEdicionEventoFrame(IControladorUsuario iCU, IControladorEvento ICE) {
        super("Consulta Edición de Evento", true, true, true, true);
        // this.icu = iCU;
        this.ice = ICE;

        comboTiposRegistro = new JComboBox<>();
        comboPatrocinios = new JComboBox<>();

        setBounds(100, 100, 900, 560);
        setLayout(new BorderLayout());

        // Selección
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.add(new JLabel("Evento:"));
        comboEventos = new JComboBox<>();
        panelSeleccion.add(comboEventos);
        panelSeleccion.add(new JLabel("Edición:"));
        comboEdiciones = new JComboBox<>();
        panelSeleccion.add(comboEdiciones);
        getContentPane().add(panelSeleccion, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 8, 6));
        center.add(panelDatos, BorderLayout.CENTER);

        panelDatos.add(labelR("Nombre Edición:"));  txtNombreEdicion = roField(panelDatos);
        panelDatos.add(labelR("Sigla:"));           txtSigla         = roField(panelDatos);
        panelDatos.add(labelR("Fecha Inicio:"));    txtFechaInicio   = roField(panelDatos);
        panelDatos.add(labelR("Fecha Fin:"));       txtFechaFin      = roField(panelDatos);
        panelDatos.add(labelR("Fecha Alta:"));      txtFechaAlta     = roField(panelDatos);
        panelDatos.add(labelR("Ciudad:"));          txtCiudad        = roField(panelDatos);
        panelDatos.add(labelR("País:"));            txtPais          = roField(panelDatos);
        panelDatos.add(labelR("Organizador:"));     txtOrganizador   = roField(panelDatos);

        panelDatos.add(labelR("Tipos de Registro:")); panelDatos.add(comboTiposRegistro);
        panelDatos.add(labelR("Patrocinios:"));       panelDatos.add(comboPatrocinios);

        JLabel lblRegistros = new JLabel("Registros de la edición:");
        panelDatos.add(lblRegistros);
        panelDatos.add(new JLabel());

        panelGridRegistros = new JPanel();
        panelGridRegistros.setLayout(new GridLayout(0, 3, 8, 2));
        panelGridRegistros.add(new JLabel("Asistente", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Tipo de registro", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Costo", SwingConstants.CENTER));
        scrollGridRegistros = new JScrollPane(panelGridRegistros);
        panelDatos.add(scrollGridRegistros);
        panelDatos.add(new JLabel());

        // Lateral derecho con imagen
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        lblImagenEdicion = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblImagenEdicion.setPreferredSize(new Dimension(280, 210));
        derecha.add(lblImagenEdicion, BorderLayout.NORTH);
        center.add(derecha, BorderLayout.EAST);

        comboEventos.addActionListener(e -> { if (!cambiando) cargarEdicionesEvento(); });
        comboEdiciones.addActionListener(e -> { if (!cambiando) mostrarDatosEdicion(); });

        comboTiposRegistro.addActionListener(e -> {
            if (!comboTiposRegistro.isPopupVisible()) return;
            int eIdx = comboEventos.getSelectedIndex();
            int dIdx = comboEdiciones.getSelectedIndex();
            int tIdx = comboTiposRegistro.getSelectedIndex();
            if (eIdx < 0 || dIdx < 0 || tIdx < 0) return;
            String nombreEvento  = comboEventos.getItemAt(eIdx);
            String nombreEdicion = comboEdiciones.getItemAt(dIdx);
            String nombreTipo    = comboTiposRegistro.getItemAt(tIdx);
            ConsultaTipoRegistroFrame frame = new ConsultaTipoRegistroFrame(iCU, ICE, nombreEvento, nombreEdicion, nombreTipo);
            abrirEnDesktop(frame);
        });

        comboPatrocinios.addActionListener(e -> {
            if (!comboPatrocinios.isPopupVisible()) return;
            int eIdx = comboEventos.getSelectedIndex();
            int dIdx = comboEdiciones.getSelectedIndex();
            int pIdx = comboPatrocinios.getSelectedIndex();
            if (eIdx < 0 || dIdx < 0 || pIdx < 0) return;
            String nombreEvento  = comboEventos.getItemAt(eIdx);
            String nombreEdicion = comboEdiciones.getItemAt(dIdx);
            String codigoPat     = comboPatrocinios.getItemAt(pIdx);
            ConsultaPatrocinioFrame frame = new ConsultaPatrocinioFrame(iCU, ICE, nombreEvento, nombreEdicion, codigoPat);
            abrirEnDesktop(frame);
        });

        cargarEventos();
    }

    /**
     * @wbp.parser.constructor
     */
    public ConsultaEdicionEventoFrame(IControladorUsuario iCU, IControladorEvento ICE,
                                      String nombreEvento, String nombreEdicion) {
        this(iCU, ICE);
        if (nombreEvento != null) seleccionarItemPorTexto(comboEventos, nombreEvento);
        cargarEdicionesEvento();
        if (nombreEdicion != null) seleccionarItemPorTexto(comboEdiciones, nombreEdicion);
        mostrarDatosEdicion();
    }

    /**
     * @wbp.parser.constructor
     */
    public ConsultaEdicionEventoFrame(IControladorUsuario iCU, IControladorEvento ICE, String siglaEdicion) {
        this(iCU, ICE);
        if (siglaEdicion == null || siglaEdicion.isEmpty()) return;

        Ediciones edicion = ICE.obtenerEdicionPorSigla(siglaEdicion);
        if (edicion == null || !esAceptada(edicion)) return; // solo aceptadas

        String nombreEdicion = edicion.getNombre();
        String nombreEvento = ICE.encontrarEventoPorSigla(siglaEdicion);
        if (nombreEvento == null || nombreEvento.isEmpty()) return;

        seleccionarItemPorTexto(comboEventos, nombreEvento);
        cargarEdicionesEvento();
        seleccionarItemPorTexto(comboEdiciones, nombreEdicion);
        mostrarDatosEdicion();
    }

    public void cargarEventos() {
        List<DTEvento> eventos = ice.listarEventos();

        // Vamos a construir SOLO eventos que tengan al menos una edición ACEPTADA,
        // y precargar para cada evento el arreglo de ediciones ACEPTADAS.
        List<String> nombresEventosAceptados = new ArrayList<>();
        List<String[]> edsAceptadasPorEvento = new ArrayList<>();

        for (DTEvento dto : eventos) {
            String nombreEv = dto.getNombre();
            List<String> aceptadas = new ArrayList<>();
            // dto.getEdiciones() trae nombres; validamos estado uno por uno
            for (String edName : dto.getEdiciones()) {
                Ediciones edicion = ice.obtenerEdicion(nombreEv, edName);
                if (esAceptada(edicion)) {
                    aceptadas.add(edName);
                }
            }
            if (!aceptadas.isEmpty()) {
                nombresEventosAceptados.add(nombreEv);
                edsAceptadasPorEvento.add(aceptadas.toArray(new String[0]));
            }
        }

        String[] arr = nombresEventosAceptados.toArray(new String[0]);
        edicionesEventos = edsAceptadasPorEvento.toArray(new String[0][]);

        cambiando = true;
        comboEventos.setModel(new DefaultComboBoxModel<>(arr));
        if (arr.length > 0) comboEventos.setSelectedIndex(0);
        cambiando = false;

        cargarEdicionesEvento();
    }

    private void cargarEdicionesEvento() {
        int idx = comboEventos.getSelectedIndex();
        cambiando = true;
        comboEdiciones.removeAllItems();
        if (idx >= 0 && edicionesEventos != null && idx < edicionesEventos.length) {
            String[] eds = edicionesEventos[idx];
            for (String ed : eds) comboEdiciones.addItem(ed);
            if (eds.length > 0) comboEdiciones.setSelectedIndex(0);
        }
        cambiando = false;
        mostrarDatosEdicion();
    }

    private void mostrarDatosEdicion() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEd     = comboEdiciones.getSelectedIndex();

        limpiarCampos();

        if (idxEvento < 0 || idxEd < 0 || edicionesEventos == null
                || idxEvento >= edicionesEventos.length) return;

        String nombreEvento  = comboEventos.getItemAt(idxEvento);
        String nombreEdicion = comboEdiciones.getItemAt(idxEd);

        Ediciones edicion = ice.obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) return;

        // SEGURIDAD: si por alguna razón llega una no aceptada, no la mostramos
        if (!esAceptada(edicion)) {
            return;
        }

        txtNombreEdicion.setText(edicion.getNombre());
        txtSigla.setText(edicion.getSigla());
        txtFechaInicio.setText(String.valueOf(edicion.getFechaInicio()));
        txtFechaFin.setText(String.valueOf(edicion.getFechaFin()));
        txtFechaAlta.setText(String.valueOf(edicion.getFechaAlta()));
        txtCiudad.setText(edicion.getCiudad());
        txtPais.setText(edicion.getPais());
        txtOrganizador.setText(edicion.getOrganizador() != null ? edicion.getOrganizador().getNickname() : "");

        comboTiposRegistro.removeAllItems();
        for (TipoRegistro tr : edicion.getTiposRegistro()) comboTiposRegistro.addItem(tr.getNombre());

        comboPatrocinios.removeAllItems();
        for (Patrocinio p : edicion.getPatrocinios()) comboPatrocinios.addItem(p.getCodigoPatrocinio());

        panelGridRegistros.removeAll();
        panelGridRegistros.add(new JLabel("Asistente", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Tipo de registro", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Costo", SwingConstants.CENTER));
        for (logica.clases.Registro reg : edicion.getRegistros().values()) {
            panelGridRegistros.add(new JLabel(reg.getUsuario().getNickname()));
            panelGridRegistros.add(new JLabel(reg.getTipoRegistro().getNombre()));
            panelGridRegistros.add(new JLabel(String.valueOf(reg.getCosto())));
        }
        panelGridRegistros.revalidate();
        panelGridRegistros.repaint();

        // Imagen
        ImageIcon icon = loadIcon(edicion.getImagen(), 300, 210);
        lblImagenEdicion.setIcon(icon);
        lblImagenEdicion.setText(icon == null ? "Sin imagen" : null);
    }

    // ====== Helpers ======

    // Aceptada si getEstado() existe y es "ACEPTADA"/"Aceptada" (String) o enum Aceptada
    private static boolean esAceptada(Ediciones edicion) {
        if (edicion == null) return false;
        try {
            Object estado = edicion.getEstado(); // soporta String o Enum
            if (estado == null) return false;
            String est = String.valueOf(estado);
            return "ACEPTADA".equalsIgnoreCase(est) || "Aceptada".equalsIgnoreCase(est);
        } catch (Throwable t) {
            // Si la clase no tiene estado, por seguridad no la mostramos
            return false;
        }
    }

    private void limpiarCampos() {
        txtNombreEdicion.setText("");
        txtSigla.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtFechaAlta.setText("");
        txtCiudad.setText("");
        txtPais.setText("");
        txtOrganizador.setText("");
        comboTiposRegistro.removeAllItems();
        comboPatrocinios.removeAllItems();
        panelGridRegistros.removeAll();
        panelGridRegistros.add(new JLabel("Asistente", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Tipo de registro", SwingConstants.CENTER));
        panelGridRegistros.add(new JLabel("Costo", SwingConstants.CENTER));
        panelGridRegistros.revalidate();
        panelGridRegistros.repaint();
        lblImagenEdicion.setIcon(null);
        lblImagenEdicion.setText("Sin imagen");
    }

    private void seleccionarItemPorTexto(JComboBox<String> combo, String texto) {
        if (texto == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (texto.equals(combo.getItemAt(i))) {
                cambiando = true;
                combo.setSelectedIndex(i);
                cambiando = false;
                return;
            }
        }
    }

    private JLabel labelR(String string) {
        JLabel label = new JLabel(string);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private JTextField roField(JPanel parent) {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        parent.add(txt);
        return txt;
    }

    private void abrirEnDesktop(JInternalFrame frame) {
        JDesktopPane desk = getDesktopPane();
        if (desk != null) desk.add(frame);
        frame.setVisible(true);
        frame.toFront();
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
