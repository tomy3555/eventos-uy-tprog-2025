package presentacion;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;
import logica.interfaces.IControladorUsuario;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ModificarDatosUsuarioFrame extends JInternalFrame {
    public ModificarDatosUsuarioFrame(IControladorUsuario ICU, String[] usuarios, String[][] datosUsuarios) {
        super("Modificar Datos de Usuario", true, true, true, true);

        setBounds(80, 80, 500, 450);
        setVisible(true);
        setLayout(new BorderLayout());

        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblUsuario = new JLabel("Usuario:");
        JComboBox<String> comboUsuarios = new JComboBox<>(usuarios);
        panelSeleccion.add(lblUsuario);
        panelSeleccion.add(comboUsuarios);
        add(panelSeleccion, BorderLayout.NORTH);

        JPanel panelDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblNick = new JLabel("Nick:");
        JTextField txtNick = new JTextField();
        txtNick.setEditable(false);
        panelDatos.add(lblNick, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtNick, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblCorreo = new JLabel("Correo:");
        JTextField txtCorreo = new JTextField();
        txtCorreo.setEditable(false);
        panelDatos.add(lblCorreo, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtCorreo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        panelDatos.add(lblNombre, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblApellido = new JLabel("Apellido:");
        JTextField txtApellido = new JTextField();
        panelDatos.add(lblApellido, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtApellido, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblFechaNac = new JLabel("Fecha de nacimiento (YYYY-MM-DD):");
        JTextField txtFechaNac = new JTextField();
        panelDatos.add(lblFechaNac, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtFechaNac, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblDescripcion = new JLabel("Descripción:");
        JTextField txtDescripcion = new JTextField();
        panelDatos.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtDescripcion, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblSitioWeb = new JLabel("Sitio web:");
        JTextField txtSitioWeb = new JTextField();
        panelDatos.add(lblSitioWeb, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtSitioWeb, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblInstitucion = new JLabel("Institución:");
        String[] instituciones = logica.manejadores.ManejadorUsuario.getInstancia().getInstituciones().toArray(new String[0]);
        JComboBox<String> comboInstitucion = new JComboBox<>(instituciones);
        panelDatos.add(lblInstitucion, gbc);
        gbc.gridx = 1;
        panelDatos.add(comboInstitucion, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        add(panelDatos, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Cambios");
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnGuardar);
        add(panelBoton, BorderLayout.SOUTH);

        // Función para habilitar/deshabilitar campos según tipo de usuario
        Runnable actualizarCampos = () -> {
            int idx = comboUsuarios.getSelectedIndex();
            if (idx < 0) return;
            boolean esAsistente = !datosUsuarios[idx][3].isEmpty() || !datosUsuarios[idx][4].isEmpty();
            txtApellido.setEnabled(esAsistente);
            txtFechaNac.setEnabled(esAsistente);
            comboInstitucion.setEnabled(esAsistente);
            lblInstitucion.setEnabled(esAsistente);
            txtDescripcion.setEnabled(!esAsistente);
            txtSitioWeb.setEnabled(!esAsistente);
        };

        // Cargar datos del usuario seleccionado
        comboUsuarios.addActionListener(e -> {
            int idx = comboUsuarios.getSelectedIndex();
            if (idx < 0) return;
            txtNick.setText(datosUsuarios[idx][0]);
            txtCorreo.setText(datosUsuarios[idx][1]);
            txtNombre.setText(datosUsuarios[idx][2]);
            txtApellido.setText(datosUsuarios[idx][3]);
            txtFechaNac.setText(datosUsuarios[idx][4]);
            txtDescripcion.setText(datosUsuarios[idx][5]);
            txtSitioWeb.setText(datosUsuarios[idx][6]);
            comboInstitucion.setSelectedItem(datosUsuarios[idx][6]);
            actualizarCampos.run();
        });

        // Inicializar con el primer usuario
        if (usuarios.length > 0) {
            comboUsuarios.setSelectedIndex(0);
            // Precargar los datos del primer usuario
            txtNick.setText(datosUsuarios[0][0]);
            txtCorreo.setText(datosUsuarios[0][1]);
            txtNombre.setText(datosUsuarios[0][2]);
            txtApellido.setText(datosUsuarios[0][3]);
            txtFechaNac.setText(datosUsuarios[0][4]);
            txtDescripcion.setText(datosUsuarios[0][5]);
            txtSitioWeb.setText(datosUsuarios[0][6]);
            comboInstitucion.setSelectedItem(datosUsuarios[0][6]);
            actualizarCampos.run();
        }

        btnGuardar.addActionListener(e -> {
            int idx = comboUsuarios.getSelectedIndex();
            if (idx < 0) return;
            String nickname = txtNick.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String fechaNacStr = txtFechaNac.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String sitioWeb = txtSitioWeb.getText().trim();
            String institucion = comboInstitucion.isEnabled() ? (String) comboInstitucion.getSelectedItem() : null;
            java.time.LocalDate fechaNac = null;
            if (txtFechaNac.isEnabled() && !fechaNacStr.isEmpty()) {
                try {
                    fechaNac = java.time.LocalDate.parse(fechaNacStr);
                } catch (IllegalStateException | NullPointerException ex) {
                    JOptionPane.showMessageDialog(this, "Fecha de nacimiento inválida. Use formato YYYY-MM-DD.");
                    return;
                }
            }
            try {
                ICU.modificarDatosUsuario(nickname, nombre, descripcion, sitioWeb, apellido, fechaNac, institucion, null);
                JOptionPane.showMessageDialog(this, "Datos actualizados correctamente para " + nickname);
                this.dispose();
            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
            } catch (UsuarioNoExisteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UsuarioTipoIncorrectoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
    }
}