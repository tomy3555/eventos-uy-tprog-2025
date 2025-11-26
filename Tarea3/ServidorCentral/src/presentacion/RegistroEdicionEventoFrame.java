package presentacion;


import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import logica.fabrica;
import logica.clases.Asistente;
import logica.clases.Ediciones;
import logica.clases.Patrocinio;
import logica.clases.Registro;
import logica.clases.TipoRegistro;
import logica.controladores.ControladorEvento;
import logica.datatypes.DTEvento;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.time.LocalDate;

public class RegistroEdicionEventoFrame extends JInternalFrame {
    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JComboBox<String> comboTipos;
    private JComboBox<String> comboAsistentes;
    private JTextArea txtInfo;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JTextField txtCodigoPatrocinio;

    private List<DTEvento> eventos;

    private List<logica.clases.Asistente> asistentes;
    private ControladorEvento controladorEvento;
    private IControladorUsuario controladorUsuario;

    public RegistroEdicionEventoFrame(IControladorUsuario iCU, IControladorEvento iCE) {
        super("Registro a Edición de Evento", true, true, true, true);
        setBounds(180, 180, 600, 350);
        setVisible(false);
        setLayout(new BorderLayout());
        controladorEvento = new ControladorEvento();
        controladorUsuario = fabrica.getInstance().getIControladorUsuario();

        JPanel panelSeleccion = new JPanel();
        panelSeleccion.setLayout(new BoxLayout(panelSeleccion, BoxLayout.Y_AXIS));
        JPanel panelEventoEdicion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEvento = new JLabel("Evento:");
        comboEventos = new JComboBox<>();
        panelEventoEdicion.add(lblEvento);
        panelEventoEdicion.add(comboEventos);
        JLabel lblEdicion = new JLabel("Edición:");
        comboEdiciones = new JComboBox<>();
        panelEventoEdicion.add(lblEdicion);
        panelEventoEdicion.add(comboEdiciones);
        panelSeleccion.add(panelEventoEdicion);
        // Panel para tipo y costo en una línea
        JPanel panelTipoCosto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTipo = new JLabel("Tipo de Registro:");
        comboTipos = new JComboBox<>();
        JLabel lblCosto = new JLabel("Costo:");
        JTextField txtCosto = new JTextField(8);
        txtCosto.setEditable(false);
        panelTipoCosto.add(lblTipo);
        panelTipoCosto.add(comboTipos);
        panelTipoCosto.add(lblCosto);
        panelTipoCosto.add(txtCosto);
        panelSeleccion.add(panelTipoCosto);
        add(panelSeleccion, BorderLayout.NORTH);

        // Panel para el combo de asistentes debajo
        JPanel panelAsistentes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAsistente = new JLabel("Asistente:"); // Cambiado a 'Asistente'
        comboAsistentes = new JComboBox<>();
        panelAsistentes.add(lblAsistente);
        panelAsistentes.add(comboAsistentes);
        // Campo para código de patrocinio (solo visible si corresponde)
        JLabel lblCodigoPatrocinio = new JLabel("Código Patrocinio:");
        txtCodigoPatrocinio = new JTextField(12);
        lblCodigoPatrocinio.setVisible(false);
        txtCodigoPatrocinio.setVisible(false);
        panelAsistentes.add(lblCodigoPatrocinio);
        panelAsistentes.add(txtCodigoPatrocinio);
        add(panelAsistentes, BorderLayout.CENTER);

        txtInfo = new JTextArea(5, 40);
        txtInfo.setEditable(false);
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);

        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.PAGE_END);

        comboEventos.addActionListener(e -> {
            cargarEdiciones();
            actualizarVisibilidadCodigoPatrocinio();
        });
        comboEdiciones.addActionListener(e -> {
            cargarTipos();
            actualizarVisibilidadCodigoPatrocinio();
        });
        comboTipos.addActionListener(e -> {
            mostrarInfo();
            actualizarCosto(txtCosto);
        });
        comboAsistentes.addActionListener(e -> {
            mostrarInfo();
            actualizarVisibilidadCodigoPatrocinio();
        });

        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> this.dispose());
    }

    public void cargarDatos() {
        // Cargar eventos
        eventos = controladorEvento.listarEventos();
        comboEventos.removeAllItems();
        for (DTEvento ev : eventos) {
            comboEventos.addItem(ev.getNombre());
        }
        if (comboEventos.getItemCount() > 0) {
            comboEventos.setSelectedIndex(0);
        }
        // Cargar solo asistentes
        Map<String, logica.clases.Asistente> mapAsistentes = controladorUsuario.listarAsistentes();
        asistentes = new ArrayList<>(mapAsistentes.values());
        comboAsistentes.removeAllItems();
        for (logica.clases.Asistente a : asistentes) {
            comboAsistentes.addItem(a.getNickname());
        }
        if (comboAsistentes.getItemCount() > 0) {
            comboAsistentes.setSelectedIndex(0);
        }
        // Inicializar combos dependientes
        cargarEdiciones();
    }

    private void cargarEdiciones() {
        comboEdiciones.removeAllItems();
        int idxEvento = comboEventos.getSelectedIndex();
        if (idxEvento < 0) return;
        DTEvento evento = eventos.get(idxEvento);
        for (String ed : evento.getEdiciones()) {
            comboEdiciones.addItem(ed);
        }
        if (comboEdiciones.getItemCount() > 0) {
            comboEdiciones.setSelectedIndex(0);
        }
        cargarTipos();
        // NO tocar comboAsistentes aquí, siempre debe estar disponible
    }

    private void cargarTipos() {
        comboTipos.removeAllItems();
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0) return;
        String nombreEvento = eventos.get(idxEvento).getNombre();
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) return;
        for (TipoRegistro tr : edicion.getTiposRegistro()) {
            comboTipos.addItem(tr.getNombre());
        }
        if (comboTipos.getItemCount() > 0) {
            comboTipos.setSelectedIndex(0);
        }
        // Actualizar costo después de cargar tipos
        JPanel panelSeleccion = (JPanel) getContentPane().getComponent(0);
        JPanel panelTipoCosto = (JPanel) panelSeleccion.getComponent(1);
        JTextField txtCosto = (JTextField) panelTipoCosto.getComponent(3);
        actualizarCosto(txtCosto);
        mostrarInfo();
    }

    private void mostrarInfo() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxTipo = comboTipos.getSelectedIndex();
        int idxAsistente = comboAsistentes.getSelectedIndex();
        txtInfo.setText("");
        if (idxEvento < 0 || idxEdicion < 0 || idxTipo < 0 || idxAsistente < 0) return;
        String nombreEvento = eventos.get(idxEvento).getNombre();
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        String nombreTipo = (String) comboTipos.getSelectedItem();
        String nicknameAsistente = (String) comboAsistentes.getSelectedItem();
        Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) return;
        TipoRegistro tipo = edicion.getTipoRegistro(nombreTipo);
        if (tipo == null) return;
        // Calcular cupo disponible
        int cantidadRegistrados = 0;
        for (Registro reg : edicion.getRegistros().values()) {
            if (reg.getTipoRegistro().equals(tipo)) {
                cantidadRegistrados++;
            }
        }
        int cupoDisponible = tipo.getCupo() - cantidadRegistrados;
        // Verificar si el usuario ya está registrado
        logica.clases.Usuario usuario = asistentes.get(idxAsistente);
        boolean yaRegistrado = false;
        if (usuario instanceof Asistente) {
            Asistente asistente = (Asistente) usuario;
            for (Registro reg : asistente.getRegistros().values()) {
                if (reg.getEdicion().equals(edicion)) {
                    yaRegistrado = true;
                    break;
                }
            }
        }
        txtInfo.setText("Cupo disponible: " + cupoDisponible +
            "\nCosto: " + tipo.getCosto() +
            (yaRegistrado ? "\nEl usuario ya está registrado a esta edición." : ""));
    }

    private void actualizarCosto(JTextField txtCosto) {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxTipo = comboTipos.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0 || idxTipo < 0) {
            txtCosto.setText("");
            return;
        }
        String nombreEvento = eventos.get(idxEvento).getNombre();
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        String nombreTipo = (String) comboTipos.getSelectedItem();
        Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) {
            txtCosto.setText("");
            return;
        }
        TipoRegistro tipo = edicion.getTipoRegistro(nombreTipo);
        if (tipo == null) {
            txtCosto.setText("");
            return;
        }
        txtCosto.setText(String.valueOf(tipo.getCosto()));
    }

    private void registrar() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxTipo = comboTipos.getSelectedIndex();
        int idxAsistente = comboAsistentes.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0 || idxTipo < 0 || idxAsistente < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar evento, edición, tipo y usuario.");
            return;
        }
        String nombreEvento = eventos.get(idxEvento).getNombre();
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        String nombreTipo = (String) comboTipos.getSelectedItem();
        String nicknameAsistente = (String) comboAsistentes.getSelectedItem();
        String codigoPatrocinioIngresado = txtCodigoPatrocinio.getText().trim();
        Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        TipoRegistro tipo = edicion.getTipoRegistro(nombreTipo);
        logica.clases.Usuario usuario = asistentes.get(idxAsistente);
        if (edicion == null || tipo == null || usuario == null) {
            JOptionPane.showMessageDialog(this, "Datos inválidos para el registro.");
            return;
        }
        // Solo permitir registrar si es Asistente
        if (!(usuario instanceof Asistente)) {
            JOptionPane.showMessageDialog(this, "Solo los usuarios de tipo Asistente pueden registrarse.");
            return;
        }
        Asistente asistente = (Asistente) usuario;
        // Verificar cupo y registro previo
        int cantidadRegistrados = 0;
        for (Registro reg : edicion.getRegistros().values()) {
            if (reg.getTipoRegistro().equals(tipo)) {
                cantidadRegistrados++;
            }
        }
        if (cantidadRegistrados >= tipo.getCupo()) {
            JOptionPane.showMessageDialog(this, "No hay cupo disponible para este tipo de registro.");
            return;
        }
        for (Registro reg : asistente.getRegistros().values()) {
            if (reg.getEdicion().equals(edicion)) {
                JOptionPane.showMessageDialog(this, "El usuario ya está registrado a esta edición.");
                return;
            }
        }
        float costo = tipo.getCosto();
        // Lógica de patrocinio: si el usuario pertenece a una institución patrocinadora de la edición y el código es correcto, costo = 0
        boolean esPatrocinado = false;
        String codigoPatrocinioValido = null;
        if (usuario.getInstitucion() != null) {
            for (Patrocinio pat : edicion.getPatrocinios()) {
                if (pat.getInstitucion().getNombre().equalsIgnoreCase(usuario.getInstitucion().getNombre())) {
                    codigoPatrocinioValido = pat.getCodigoPatrocinio();
                    if (!codigoPatrocinioValido.isEmpty() && codigoPatrocinioValido.equalsIgnoreCase(codigoPatrocinioIngresado)) {
                        esPatrocinado = true;
                        break;
                    }
                }
            }
        }
        if (esPatrocinado) {
            costo = 0;
        }
        try {
            LocalDate fechaRegistro = LocalDate.now();
            LocalDate fechaInicio = edicion.getFechaInicio();
            StringBuilder stringBuild = new StringBuilder();
            stringBuild.append(usuario.getNickname() + " "+ edicion.getNombre());
            controladorEvento.altaRegistroEdicionEvento(stringBuild.toString(), usuario, controladorEvento.consultaEvento(nombreEvento), edicion, tipo, fechaRegistro, costo, fechaInicio);
            JOptionPane.showMessageDialog(this, "Registro realizado correctamente.");
            this.dispose();
        } catch (excepciones.CupoTipoRegistroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, "No quedan cupos disponibles para este tipo de registro.", "Sin cupo", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage());
        }
    }

    private void actualizarVisibilidadCodigoPatrocinio() {
        int idxEvento = comboEventos.getSelectedIndex();
        int idxEdicion = comboEdiciones.getSelectedIndex();
        int idxAsistente = comboAsistentes.getSelectedIndex();
        if (idxEvento < 0 || idxEdicion < 0 || idxAsistente < 0) {
            txtCodigoPatrocinio.setVisible(false);
            ((JLabel) ((JPanel) txtCodigoPatrocinio.getParent()).getComponent(2)).setVisible(false);
            return;
        }
        String nombreEvento = eventos.get(idxEvento).getNombre();
        String nombreEdicion = (String) comboEdiciones.getSelectedItem();
        Ediciones edicion = controladorEvento.obtenerEdicion(nombreEvento, nombreEdicion);
        logica.clases.Usuario usuario = asistentes.get(idxAsistente);
        boolean mostrar = false;
        if (edicion != null && usuario != null && usuario.getInstitucion() != null) {
            for (Patrocinio pat : edicion.getPatrocinios()) {
                if (pat.getInstitucion().getNombre().equalsIgnoreCase(usuario.getInstitucion().getNombre())) {
                    mostrar = true;
                    break;
                }
            }
        }
        txtCodigoPatrocinio.setVisible(mostrar);
        ((JLabel) ((JPanel) txtCodigoPatrocinio.getParent()).getComponent(2)).setVisible(mostrar);
    }
}