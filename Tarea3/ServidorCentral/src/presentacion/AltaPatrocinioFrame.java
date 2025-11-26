package presentacion;

import javax.swing.JButton;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JTextField;

import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import javax.swing.JComboBox;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Insets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

public class AltaPatrocinioFrame extends JInternalFrame {
    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JComboBox<String> comboInstituciones;
    private JComboBox<String> comboTipoGratuito;
    private JComboBox<String> comboNivel;
    private JTextField txtAporte;
    private JTextField txtCantidadGratuitos;
    private JTextField txtCodigo;
    private String[] eventos;
    private String[][] edicionesPorEvento;
    private String[][] tiposPorEdicion;
    private String[] instituciones;
    private double[] costosTipoRegistro;
    private Set<String> codigosPatrocinio = new HashSet<>();
    private Set<String> patrociniosInstitucionEdicion = new HashSet<>();

    public AltaPatrocinioFrame(IControladorUsuario iCU, IControladorEvento iCE) {
        super("Alta de Patrocinio", true, true, true, true);
        setBounds(250, 250, 600, 400);
        setVisible(false);
        setLayout(new BorderLayout());

        JPanel panelSeleccion = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelSeleccion.add(lblEvento, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(comboEventos, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblEdicion = new JLabel("Edición:");
        comboEdiciones = new JComboBox<>();
        panelSeleccion.add(lblEdicion, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(comboEdiciones, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblInstitucion = new JLabel("Institución:");
        comboInstituciones = new JComboBox<>();
        panelSeleccion.add(lblInstitucion, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(comboInstituciones, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblNivel = new JLabel("Nivel de Patrocinio:");
        String[] niveles = {"Platino", "Oro", "Plata", "Bronce"};
        comboNivel = new JComboBox<>(niveles);
        panelSeleccion.add(lblNivel, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(comboNivel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblAporte = new JLabel("Aporte económico:");
        txtAporte = new JTextField(10);
        panelSeleccion.add(lblAporte, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(txtAporte, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblTipoGratuito = new JLabel("Tipo de Registro Gratuito:");
        comboTipoGratuito = new JComboBox<>();
        panelSeleccion.add(lblTipoGratuito, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(comboTipoGratuito, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblCantidadGratuitos = new JLabel("Cantidad Registros Gratuitos:");
        txtCantidadGratuitos = new JTextField(10);
        panelSeleccion.add(lblCantidadGratuitos, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(txtCantidadGratuitos, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblCodigo = new JLabel("Código de Patrocinio:");
        txtCodigo = new JTextField(12);
        panelSeleccion.add(lblCodigo, gbc);
        gbc.gridx = 1;
        panelSeleccion.add(txtCodigo, gbc);

        add(panelSeleccion, BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        comboEventos.addActionListener(e -> {
            cargarEdiciones();
            cargarTipos();
        });
        comboEdiciones.addActionListener(e -> {
            cargarTipos();
        });

        btnAceptar.addActionListener(e -> {
            int idxEvento = comboEventos.getSelectedIndex();
            int idxEdicion = comboEdiciones.getSelectedIndex();
            int idxTipoGratuito = comboTipoGratuito.getSelectedIndex();
            int idxInstitucion = comboInstituciones.getSelectedIndex();
            String nivel = (String) comboNivel.getSelectedItem();
            String aporteStr = txtAporte.getText().trim();
            String cantidadGratuitosStr = txtCantidadGratuitos.getText().trim();
            String codigo = txtCodigo.getText().trim();
            if (idxEvento < 0 || idxEdicion < 0 || idxTipoGratuito < 0 || idxInstitucion < 0 || nivel == null || aporteStr.isEmpty() || cantidadGratuitosStr.isEmpty() || codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }
            double aporte;
            int cantidadGratuitos;
            try {
                aporte = Double.parseDouble(aporteStr);
                cantidadGratuitos = Integer.parseInt(cantidadGratuitosStr);
            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Aporte y cantidad deben ser numéricos.");
                return;
            }
            
            try {
                cantidadGratuitos = Integer.parseInt(cantidadGratuitosStr);
                if (cantidadGratuitos < 0) {
                    throw new NumberFormatException("La cantidad de gratuitos no puede ser negativa");
                }
            	
            } catch (IllegalStateException | NullPointerException ex) {
            	JOptionPane.showMessageDialog(this, ex.getMessage());
                return;
            }
            if (codigosPatrocinio.contains(codigo.toLowerCase())) {
                JOptionPane.showMessageDialog(this, "Ya existe un patrocinio con ese código. Ingrese otro código o cancele.");
                return;
            }
            String clavePatrocinio = comboInstituciones.getItemAt(idxInstitucion).toLowerCase() + "-" + comboEdiciones.getItemAt(idxEdicion).toLowerCase();
            if (patrociniosInstitucionEdicion.contains(clavePatrocinio)) {
                JOptionPane.showMessageDialog(this, "Ya existe un patrocinio de esta institución para la edición seleccionada.");
                return;
            }
            double costoTipo = costosTipoRegistro.length > idxTipoGratuito ? costosTipoRegistro[idxTipoGratuito] : 0.0;
            System.out.println("costo del tipo: " + costoTipo);
            double totalGratis = costoTipo * cantidadGratuitos;
            System.out.println("cant gratis: " + cantidadGratuitos);
            System.out.println("total gratis: " + totalGratis);
            if (totalGratis > aporte * 0.2) {
                JOptionPane.showMessageDialog(this, "El costo de los registros gratuitos supera el 20% del aporte económico. Modifique los valores o cancele.");
                return;
            }
            try {
                logica.controladores.ControladorEvento controlador = new logica.controladores.ControladorEvento();
                String nombreEvento = comboEventos.getItemAt(idxEvento);
                String nombreEdicion = comboEdiciones.getItemAt(idxEdicion);
                String nombreInstitucion = comboInstituciones.getItemAt(idxInstitucion);
                String tipoRegistroGratuito = comboTipoGratuito.getItemAt(idxTipoGratuito);
                logica.clases.Ediciones edicion = controlador.obtenerEdicion(nombreEvento, nombreEdicion);
                logica.clases.Institucion institucion = logica.manejadores.ManejadorUsuario.getInstancia().findInstitucion(nombreInstitucion);
                logica.clases.TipoRegistro tipoRegistro = edicion != null ? edicion.getTipoRegistro(tipoRegistroGratuito) : null;
                logica.enumerados.DTNivel nivelEnum = logica.enumerados.DTNivel.valueOf(nivel.toUpperCase());
                java.time.LocalDate fechaHoy = java.time.LocalDate.now();
                controlador.altaPatrocinio(
                    edicion,
                    institucion,
                    nivelEnum,
                    tipoRegistro,
                    (int) aporte,
                    fechaHoy,
                    cantidadGratuitos,
                    codigo
                );
            } catch (excepciones.ValorPatrocinioExcedidoException ex) {
                JOptionPane.showMessageDialog(this,
                    "El valor del patrocinio excede el permitido: " + ex.getMessage(),
                    "Error de validación", JOptionPane.ERROR_MESSAGE);

            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al dar de alta el patrocinio: " + ex.getMessage(),
                    "Error interno", JOptionPane.ERROR_MESSAGE);
            }
            codigosPatrocinio.add(codigo.toLowerCase());
            patrociniosInstitucionEdicion.add(clavePatrocinio);
            String fechaAlta = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            JOptionPane.showMessageDialog(this, "Patrocinio creado con éxito:\nEvento: " + comboEventos.getSelectedItem() +
                    "\nEdición: " + comboEdiciones.getSelectedItem() +
                    "\nInstitución: " + comboInstituciones.getSelectedItem() +
                    "\nNivel: " + nivel +
                    "\nAporte: " + aporte +
                    "\nTipo gratuito: " + comboTipoGratuito.getSelectedItem() +
                    "\nCantidad gratuitos: " + cantidadGratuitos +
                    "\nCódigo: " + codigo +
                    "\nFecha de alta: " + fechaAlta);
            txtAporte.setText("");
            txtCantidadGratuitos.setText("");
            txtCodigo.setText("");
            this.dispose();
        });
        btnCancelar.addActionListener(e -> this.dispose());
    }

    public void cargarDatos() {
        logica.controladores.ControladorEvento controlador = new logica.controladores.ControladorEvento();
        java.util.List<logica.datatypes.DTEvento> listaEventos = controlador.listarEventos();
        eventos = new String[listaEventos.size()];
        edicionesPorEvento = new String[listaEventos.size()][];
        java.util.List<String[]> tiposList = new java.util.ArrayList<>();
        java.util.List<Double> costosList = new java.util.ArrayList<>();
        for (int iter = 0; iter < listaEventos.size(); iter++) {
            logica.datatypes.DTEvento eventIter = listaEventos.get(iter);
            eventos[iter] = eventIter.getNombre();
            java.util.List<String> edicionesAux = controlador.listarEdicionesEvento(eventIter.getNombre());
            edicionesPorEvento[iter] = edicionesAux.toArray(new String[0]);
            for (String nombreEdicion : edicionesAux) {
                logica.clases.Ediciones edicionSelected = controlador.obtenerEdicion(eventIter.getNombre(), nombreEdicion);
                java.util.List<String> tipos = new java.util.ArrayList<>();
                if (edicionSelected != null) {
                    for (logica.clases.TipoRegistro tipoRegistroEdicion : edicionSelected.getTiposRegistro()) {
                        tipos.add(tipoRegistroEdicion.getNombre());
                        costosList.add((double) tipoRegistroEdicion.getCosto());
                    }
                }
                tiposList.add(tipos.toArray(new String[0]));
            }
        }
        tiposPorEdicion = tiposList.toArray(new String[0][0]);
        costosTipoRegistro = costosList.stream().mapToDouble(Double::doubleValue).toArray();
        instituciones = logica.manejadores.ManejadorUsuario.getInstancia().getInstituciones().toArray(new String[0]);
        codigosPatrocinio = new HashSet<>();
        for (var patrocinioIter : logica.manejadores.ManejadorAuxiliar.getInstancia().listarPatrocinios()) {
            if (patrocinioIter != null && patrocinioIter.getCodigoPatrocinio() != null)
                codigosPatrocinio.add(patrocinioIter.getCodigoPatrocinio().toLowerCase());
        }
        patrociniosInstitucionEdicion = new HashSet<>();
        for (var patrocinioIter : logica.manejadores.ManejadorAuxiliar.getInstancia().listarPatrocinios()) {
            if (patrocinioIter != null && patrocinioIter.getInstitucion() != null && patrocinioIter.getEdicion() != null && patrocinioIter.getInstitucion().getNombre() != null && patrocinioIter.getEdicion().getNombre() != null)
                patrociniosInstitucionEdicion.add(patrocinioIter.getInstitucion().getNombre().toLowerCase() + "-" + patrocinioIter.getEdicion().getNombre().toLowerCase());
        }
        // Cargar combos
        comboEventos.removeAllItems();
        for (String eventoIter : eventos) comboEventos.addItem(eventoIter);
        comboInstituciones.removeAllItems();
        for (String inst : instituciones) comboInstituciones.addItem(inst);
        if (eventos.length > 0) {
            comboEventos.setSelectedIndex(0);
            cargarEdiciones();
            cargarTipos();
        } else {
            comboEdiciones.removeAllItems();
            comboTipoGratuito.removeAllItems();
        }
    }

    private void cargarEdiciones() {
        comboEdiciones.removeAllItems();
        int idxEvento = comboEventos.getSelectedIndex();
        if (idxEvento < 0 || edicionesPorEvento == null || edicionesPorEvento.length <= idxEvento) return;
        for (String edicionIter : edicionesPorEvento[idxEvento]) comboEdiciones.addItem(edicionIter);
        if (comboEdiciones.getItemCount() > 0) comboEdiciones.setSelectedIndex(0);
    }
    private void cargarTipos() {
        comboTipoGratuito.removeAllItems();
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxTipo = 0;
        for (int iter = 0; iter < idxEvento; iter++) {
            idxTipo += edicionesPorEvento[iter].length;
        }
        idxTipo += idxEdicion;
        if (idxEvento < 0 || idxEdicion < 0 || tiposPorEdicion == null || tiposPorEdicion.length <= idxTipo) return;
        for (String tipo : tiposPorEdicion[idxTipo]) {
            comboTipoGratuito.addItem(tipo);
        }
        if (comboTipoGratuito.getItemCount() > 0) comboTipoGratuito.setSelectedIndex(0);
    }
}