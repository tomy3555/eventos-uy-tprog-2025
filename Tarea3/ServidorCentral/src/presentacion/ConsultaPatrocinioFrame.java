package presentacion;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;


public class ConsultaPatrocinioFrame extends JInternalFrame {
    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JComboBox<String> comboPatrocinios;
    private JTextField txtInstitucion;
    private JTextField txtNivel;
    private JTextField txtTipoRegistro;
    private JTextField txtAporte;
    private JTextField txtFecha;
    private JTextField txtCantidadRegistros;
    private JTextField txtCodigo;
    private String[] eventos;
    private String[][] edicionesPorEvento;
    private String[][] patrociniosPorEdicion;
    private String[][] datosPatrocinio;

    public ConsultaPatrocinioFrame(IControladorUsuario iCU, IControladorEvento iCE) {
        super("Consulta de Patrocinio", true, true, true, true);
        setBounds(220, 220, 500, 320);
        setVisible(false);
        setLayout(new BorderLayout());

        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelSeleccion.add(lblEvento);
        panelSeleccion.add(comboEventos);
        JLabel lblEdicion = new JLabel("Edición:");
        comboEdiciones = new JComboBox<>();
        panelSeleccion.add(lblEdicion);
        panelSeleccion.add(comboEdiciones);
        JLabel lblPatrocinio = new JLabel("Patrocinio:");
        comboPatrocinios = new JComboBox<>();
        panelSeleccion.add(lblPatrocinio);
        panelSeleccion.add(comboPatrocinios);
        add(panelSeleccion, BorderLayout.NORTH);

        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 10, 10));
        panelDatos.add(new JLabel("Institución:"));
        txtInstitucion = new JTextField();
        txtInstitucion.setEditable(false);
        panelDatos.add(txtInstitucion);
        panelDatos.add(new JLabel("Nivel:"));
        txtNivel = new JTextField();
        txtNivel.setEditable(false);
        panelDatos.add(txtNivel);
        panelDatos.add(new JLabel("Tipo de registro:"));
        txtTipoRegistro = new JTextField();
        txtTipoRegistro.setEditable(false);
        panelDatos.add(txtTipoRegistro);
        panelDatos.add(new JLabel("Aporte:"));
        txtAporte = new JTextField();
        txtAporte.setEditable(false);
        panelDatos.add(txtAporte);
        panelDatos.add(new JLabel("Fecha:"));
        txtFecha = new JTextField();
        txtFecha.setEditable(false);
        panelDatos.add(txtFecha);
        panelDatos.add(new JLabel("Cantidad Registros Gratuitos:"));
        txtCantidadRegistros = new JTextField();
        txtCantidadRegistros.setEditable(false);
        panelDatos.add(txtCantidadRegistros);
        panelDatos.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        txtCodigo.setEditable(false);
        panelDatos.add(txtCodigo);
        add(panelDatos, BorderLayout.CENTER);

        // Listeners para cascada
        comboEventos.addActionListener(e -> cargarEdiciones());
        comboEdiciones.addActionListener(e -> cargarPatrocinios());
        comboPatrocinios.addActionListener(e -> mostrarDatosPatrocinio());
    }

    public ConsultaPatrocinioFrame(IControladorUsuario iCU, IControladorEvento iCE, String nombreEvento, String nombreEdicion, String codigoPatrocinio) {
        this(iCU, iCE);
        cargarDatos();
        if (nombreEvento != null && nombreEdicion != null && codigoPatrocinio != null) {
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
            cargarPatrocinios();
            for (int k = 0; k < comboPatrocinios.getItemCount(); k++) {
                if (comboPatrocinios.getItemAt(k).equals(codigoPatrocinio)) {
                    comboPatrocinios.setSelectedIndex(k);
                    break;
                }
            }
            mostrarDatosPatrocinio();
        }
    }

    public void cargarDatos() {
        logica.controladores.ControladorEvento controlador = new logica.controladores.ControladorEvento();
        java.util.List<logica.datatypes.DTEvento> listaEventos = controlador.listarEventos();
        eventos = new String[listaEventos.size()];
        edicionesPorEvento = new String[listaEventos.size()][];
        java.util.List<String[]> patsList = new java.util.ArrayList<>();
        java.util.List<String[]> datosList = new java.util.ArrayList<>();
        for (int i = 0; i < listaEventos.size(); i++) {
            logica.datatypes.DTEvento evento = listaEventos.get(i);
            eventos[i] = evento.getNombre();
            java.util.List<String> eds = controlador.listarEdicionesEvento(evento.getNombre());
            edicionesPorEvento[i] = eds.toArray(new String[0]);
            for (String ed : eds) {
                logica.clases.Ediciones edi = controlador.obtenerEdicion(evento.getNombre(), ed);
                java.util.List<String> pats = new java.util.ArrayList<>();
                java.util.List<String> datosPat = new java.util.ArrayList<>();
                if (edi != null) {
                    for (logica.clases.Patrocinio p : edi.getPatrocinios()) {
                        pats.add(p.getCodigoPatrocinio());
                        String datos = "Institución: " + (p.getInstitucion() != null ? p.getInstitucion().getNombre() : "") +
                                "\nNivel: " + (p.getNivel() != null ? p.getNivel().toString() : "") +
                                "\nTipo Registro: " + (p.getTipoRegistro() != null ? p.getTipoRegistro().getNombre() : "") +
                                "\nAporte: " + p.getAporte() +
                                "\nFecha: " + (p.getFechaPatrocinio() != null ? p.getFechaPatrocinio().toString() : "") +
                                "\nCantidad Registros Gratuitos: " + p.getCantidadRegistros() +
                                "\nCódigo: " + p.getCodigoPatrocinio();
                        datosPat.add(datos);
                    }
                }
                patsList.add(pats.toArray(new String[0]));
                datosList.add(datosPat.toArray(new String[0]));
            }
        }
        patrociniosPorEdicion = patsList.toArray(new String[0][0]);
        datosPatrocinio = datosList.toArray(new String[0][0]);
        comboEventos.removeAllItems();
        for (String ev : eventos) comboEventos.addItem(ev);
        if (eventos.length > 0) {
            comboEventos.setSelectedIndex(0);
            cargarEdiciones();
        } else {
            comboEdiciones.removeAllItems();
            comboPatrocinios.removeAllItems();
            // Limpiar campos
            txtInstitucion.setText("");
            txtNivel.setText("");
            txtTipoRegistro.setText("");
            txtAporte.setText("");
            txtFecha.setText("");
            txtCantidadRegistros.setText("");
            txtCodigo.setText("");
        }
    }

    private void cargarEdiciones() {
        int idx = comboEventos.getSelectedIndex();
        comboEdiciones.removeAllItems();
        comboPatrocinios.removeAllItems();
        // Limpiar campos
        txtInstitucion.setText("");
        txtNivel.setText("");
        txtTipoRegistro.setText("");
        txtAporte.setText("");
        txtFecha.setText("");
        txtCantidadRegistros.setText("");
        txtCodigo.setText("");
        if (idx < 0 || edicionesPorEvento == null || idx >= edicionesPorEvento.length) return;
        String[] ediciones = edicionesPorEvento[idx];
        for (String ed : ediciones) comboEdiciones.addItem(ed);
        if (ediciones.length > 0) {
            comboEdiciones.setSelectedIndex(0);
            cargarPatrocinios();
        }
    }

    private void cargarPatrocinios() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        comboPatrocinios.removeAllItems();
        // Limpiar campos
        txtInstitucion.setText("");
        txtNivel.setText("");
        txtTipoRegistro.setText("");
        txtAporte.setText("");
        txtFecha.setText("");
        txtCantidadRegistros.setText("");
        txtCodigo.setText("");
        if (idxEvento < 0 || idxEdicion < 0) return;
        // Calcular el índice global de la edición
        int idxGlobal = 0;
        for (int i = 0; i < idxEvento; i++) {
            idxGlobal += edicionesPorEvento[i].length;
        }
        idxGlobal += idxEdicion;
        if (patrociniosPorEdicion == null || idxGlobal >= patrociniosPorEdicion.length) return;
        String[] pats = patrociniosPorEdicion[idxGlobal];
        for (String pat : pats) comboPatrocinios.addItem(pat);
        if (pats.length > 0) {
            comboPatrocinios.setSelectedIndex(0);
            mostrarDatosPatrocinio();
        }
    }

    private void mostrarDatosPatrocinio() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxPatrocinio = comboPatrocinios.getSelectedIndex();
        // Limpiar campos
        txtInstitucion.setText("");
        txtNivel.setText("");
        txtTipoRegistro.setText("");
        txtAporte.setText("");
        txtFecha.setText("");
        txtCantidadRegistros.setText("");
        txtCodigo.setText("");
        if (idxEvento < 0 || idxEdicion < 0 || idxPatrocinio < 0) return;
        // Calcular el índice global de la edición
        int idxGlobal = 0;
        for (int i = 0; i < idxEvento; i++) {
            idxGlobal += edicionesPorEvento[i].length;
        }
        idxGlobal += idxEdicion;
        if (datosPatrocinio == null || idxGlobal >= datosPatrocinio.length) return;
        String[] datos = datosPatrocinio[idxGlobal];
        if (idxPatrocinio >= datos.length) return;
        // Parsear los datos
        String datosStr = datos[idxPatrocinio];
        String[] lines = datosStr.split("\n");
        for (String line : lines) {
            if (line.startsWith("Institución:")) txtInstitucion.setText(line.substring(12).trim());
            else if (line.startsWith("Nivel:")) txtNivel.setText(line.substring(6).trim());
            else if (line.startsWith("Tipo Registro:")) txtTipoRegistro.setText(line.substring(14).trim());
            else if (line.startsWith("Aporte:")) txtAporte.setText(line.substring(7).trim());
            else if (line.startsWith("Fecha:")) txtFecha.setText(line.substring(6).trim());
            else if (line.startsWith("Cantidad Registros Gratuitos:")) txtCantidadRegistros.setText(line.substring(29).trim());
            else if (line.startsWith("Código:")) txtCodigo.setText(line.substring(7).trim());
        }
    }
}