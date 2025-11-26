package presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Comparator;
import java.util.stream.Collectors;

import logica.datatypes.DTTopEvento;

public class TopEventosFrame extends JInternalFrame {

    private final Supplier<List<DTTopEvento>> loader;
    private final Consumer<String> onOpenEvento;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel vacioLabel;
    private JButton btnActualizar;

    public TopEventosFrame(Supplier<List<DTTopEvento>> loader,
                           Consumer<String> onOpenEvento) {
        super("Top 5 eventos más visitados", true, true, true, true);
        this.loader = Objects.requireNonNull(loader, "loader");
        this.onOpenEvento = Objects.requireNonNull(onOpenEvento, "onOpenEvento");
        initUI();
        recargar(); // carga inicial
    }

    private void initUI() {
        setSize(700, 420);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Eventos más visitados");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> recargar());
        header.add(btnActualizar, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"#", "Evento", "Visitas"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2 -> Integer.class;
                    default -> String.class;
                };
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(440);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);

        // Doble clic → abrir consulta
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    int row = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                    String nombre = (String) modelo.getValueAt(row, 1);
                    if (nombre != null && !nombre.isBlank()) {
                        onOpenEvento.accept(nombre);
                    }
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Pie “sin datos”
        vacioLabel = new JLabel("Sin visitas registradas todavía.", SwingConstants.CENTER);
        vacioLabel.setFont(vacioLabel.getFont().deriveFont(Font.ITALIC));
        vacioLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        add(vacioLabel, BorderLayout.SOUTH);
    }

    private void setBusy(boolean busy) {
        btnActualizar.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                       : Cursor.getDefaultCursor());
    }

    public void recargar() {
        setBusy(true);
        SwingWorker<List<DTTopEvento>, Void> w = new SwingWorker<>() {
            @Override protected List<DTTopEvento> doInBackground() {
                try {
                    List<DTTopEvento> data = loader.get();
                    if (data == null) data = List.of();
                    System.out.println("[TOP] loader devolvió " + data.size() + " items");
                    for (DTTopEvento t : data) {
                        System.out.println("   · " + t.getNombreEvento() + " → " + t.getVisitas());
                    }
                    return data.stream()
                            .sorted(Comparator
                                    .comparingInt(DTTopEvento::getVisitas).reversed()
                                    .thenComparing(DTTopEvento::getNombreEvento,
                                            Comparator.nullsLast(String::compareToIgnoreCase)))
                            .limit(5)
                            .collect(Collectors.toList());
                } catch (Exception ex) {
                    System.out.println("[TOP][ERROR] " + ex.getClass().getName() + " : " + ex.getMessage());
                    return List.of();
                }
            }

            @Override protected void done() {
                try {
                    List<DTTopEvento> top = get();
                    modelo.setRowCount(0);
                    int rank = 1;
                    for (DTTopEvento t : top) {
                        modelo.addRow(new Object[]{ rank++, t.getNombreEvento(), t.getVisitas() });
                    }
                    vacioLabel.setVisible(top.isEmpty());
                } catch (Exception ex) {
                    System.out.println("[TOP][ERROR done()] " + ex.getClass().getName() + " : " + ex.getMessage());
                    modelo.setRowCount(0);
                    vacioLabel.setVisible(true);
                } finally {
                    setBusy(false);
                }
            }
        };
        w.execute();
    }
}
