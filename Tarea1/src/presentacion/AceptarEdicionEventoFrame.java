package presentacion;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import logica.interfaces.IControladorEvento;

import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

// NUEVO
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class AceptarEdicionEventoFrame extends JInternalFrame {
    private final IControladorEvento ice;

    private JComboBox<String> comboEventos;
    private JComboBox<String> comboEdiciones;
    private JButton btnAceptar;
    private JButton btnRechazar;

    public AceptarEdicionEventoFrame(IControladorEvento ICE) {
        super("Aceptar/Rechazar Edición de Evento", true, true, true, true);
        this.ice = ICE;
        setSize(500, 250);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        comboEventos = new JComboBox<>();
        comboEdiciones = new JComboBox<>();
        btnAceptar = new JButton("Aceptar Edición");
        btnRechazar = new JButton("Rechazar Edición");

        panel.add(new JLabel("Evento:"));
        panel.add(comboEventos);
        panel.add(new JLabel("Edición Ingresada:"));
        panel.add(comboEdiciones);
        panel.add(btnAceptar);
        panel.add(btnRechazar);

        add(panel, BorderLayout.CENTER);

        // Eventos
        comboEventos.addActionListener(e -> cargarEdiciones());

        btnAceptar.addActionListener(e -> cambiarEstado(true));
        btnRechazar.addActionListener(e -> cambiarEstado(false));

        // NUEVO: cargar datos al abrir y cuando la ventana se reactive
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameOpened(InternalFrameEvent event) {
                cargarEventos(); // primera carga
            }
            @Override public void internalFrameActivated(InternalFrameEvent event) {
                // refresco "barato": recarga eventos y posiciona en el actual si existía
                String seleccionado = (String) comboEventos.getSelectedItem();
                cargarEventos();
                if (seleccionado != null) comboEventos.setSelectedItem(seleccionado);
                cargarEdiciones();
            }
        });

        // Estado inicial
        setBotonesHabilitados(false);
    }

    public void cargarEventos() {
        comboEventos.removeAllItems();
        try {
            List<String> eventos = ice.listarEventosConEdicionesIngresadas();
            if (eventos == null || eventos.isEmpty()) {
                setBotonesHabilitados(false);
                comboEdiciones.removeAllItems();
                return;
            }
            for (String nombre : eventos) comboEventos.addItem(nombre);
            // Seleccionar el primero si hay
            if (comboEventos.getItemCount() > 0 && comboEventos.getSelectedItem() == null) {
                comboEventos.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            mostrarError("No se pudieron cargar los eventos", ex);
        }
        // después de cargar eventos, cargar sus ediciones
        cargarEdiciones();
    }

    private void cargarEdiciones() {
        comboEdiciones.removeAllItems();
        String evento = (String) comboEventos.getSelectedItem();
        if (evento == null) {
            setBotonesHabilitados(false);
            return;
        }
        try {
            List<String> ediciones = ice.listarEdicionesIngresadasDeEvento(evento);
            if (ediciones == null || ediciones.isEmpty()) {
                setBotonesHabilitados(false);
                return;
            }
            for (String ed : ediciones) comboEdiciones.addItem(ed);
            if (comboEdiciones.getItemCount() > 0 && comboEdiciones.getSelectedItem() == null) {
                comboEdiciones.setSelectedIndex(0);
            }
            setBotonesHabilitados(true);
        } catch (Exception ex) {
            setBotonesHabilitados(false);
            mostrarError("No se pudieron cargar las ediciones del evento seleccionado", ex);
        }
    }

    private void cambiarEstado(boolean aceptar) {
        String evento = (String) comboEventos.getSelectedItem();
        String edicion = (String) comboEdiciones.getSelectedItem();
        if (evento == null || edicion == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un evento y una edición", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            ice.cambiarEstadoEdicion(evento, edicion, aceptar);
            JOptionPane.showMessageDialog(this, "Edición " + (aceptar ? "confirmada" : "rechazada") + " correctamente.");
        } catch (Exception ex) {
            mostrarError("No se pudo cambiar el estado de la edición", ex);
        }

        // Refrescar combos luego del cambio (la edición ya no debería estar "Ingresada")
        cargarEdiciones();

        // Si ya no quedan ediciones "Ingresadas" para ese evento, deshabilitar y quizá recargar eventos
        if (comboEdiciones.getItemCount() == 0) {
            setBotonesHabilitados(false);
            // Opcional: si ya no hay eventos con ediciones ingresadas, volver a cargar la lista de eventos
            cargarEventos();
        }
    }

    // Helpers
    private void setBotonesHabilitados(boolean enabled) {
        btnAceptar.setEnabled(enabled);
        btnRechazar.setEnabled(enabled);
    }

    private void mostrarError(String titulo, Exception except) {
        JOptionPane.showMessageDialog(this,
                titulo + (except.getMessage() != null ? (": " + except.getMessage()) : ""),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
