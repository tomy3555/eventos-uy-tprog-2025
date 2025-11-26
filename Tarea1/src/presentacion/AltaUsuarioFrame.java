package presentacion;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


import com.toedter.calendar.JDateChooser;

import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.util.Vector;
import java.time.LocalDate;
import java.time.ZoneId;
import java.awt.event.ActionEvent;

import java.io.File;

public class AltaUsuarioFrame extends JInternalFrame {
    private IControladorUsuario controlUsr;
    private JPanel panelAsistente;
    private JPanel panelOrganizador;
    private Vector<String> instituciones;
    private JComboBox<String> comboInstitucion;    
    private JTextField textField;
    private JTextField textField1;
    private JTextField textField2;
    private JRadioButton rdbtnAsistente;
    private JRadioButton rdbtnOrganizador;
    private ButtonGroup grupoRol;
    private JTextField txtApellido;
    private JDateChooser txtFechaNacimiento;
    private JTextField txtDescripcion;
    private JTextField txtWeb;
    private JPasswordField txtContrasena;
    private JLabel lblArchivoImagen;
    private String imagenSeleccionada = null;

    public AltaUsuarioFrame(IControladorUsuario icu, IControladorEvento iCE) {
        controlUsr = icu;
        
        setTitle("Alta de usuario");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setBounds(100, 100, 550, 480);
        getContentPane().setLayout(null);

        // Campo nickname
        JLabel lblNickname = new JLabel("Nickname: ");
        lblNickname.setBounds(34, 11, 120, 14);
        getContentPane().add(lblNickname);

        textField = new JTextField();
        textField.setBounds(160, 8, 206, 20);
        getContentPane().add(textField);

        // Campo nombre
        JLabel lblNombre = new JLabel("Nombre: ");
        lblNombre.setBounds(34, 40, 120, 14);
        getContentPane().add(lblNombre);

        textField1 = new JTextField();
        textField1.setBounds(160, 37, 206, 20);
        getContentPane().add(textField1);

        // Campo correo
        JLabel lblCorreo = new JLabel("Correo electrónico: ");
        lblCorreo.setBounds(34, 70, 120, 14);
        getContentPane().add(lblCorreo);

        textField2 = new JTextField();
        textField2.setBounds(160, 67, 206, 20);
        getContentPane().add(textField2);

        // Contraseña
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(34, 100, 120, 14);
        getContentPane().add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(160, 97, 206, 20);
        getContentPane().add(txtContrasena);

        // Selector de imagen
        JLabel lblImagen = new JLabel("Imagen:");
        lblImagen.setBounds(34, 130, 120, 14);
        getContentPane().add(lblImagen);

        JButton btnImagen = new JButton("Seleccionar...");
        btnImagen.setBounds(160, 126, 110, 23);
        getContentPane().add(btnImagen);

        lblArchivoImagen = new JLabel("Ninguna seleccionada");
        lblArchivoImagen.setBounds(280, 130, 200, 14);
        getContentPane().add(lblArchivoImagen);

        btnImagen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar imagen de usuario");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes JPG y PNG", "jpg", "jpeg", "png"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                imagenSeleccionada = archivo.getName(); // o archivo.getAbsolutePath()
                lblArchivoImagen.setText(archivo.getName());
            }
        });

        // Rol
        rdbtnAsistente = new JRadioButton("Asistente");
        rdbtnAsistente.setBounds(92, 160, 109, 23);
        getContentPane().add(rdbtnAsistente);

        rdbtnOrganizador = new JRadioButton("Organizador");
        rdbtnOrganizador.setBounds(236, 160, 109, 23);
        getContentPane().add(rdbtnOrganizador);

        grupoRol = new ButtonGroup();
        grupoRol.add(rdbtnAsistente);
        grupoRol.add(rdbtnOrganizador);

        // Panel asistente
        panelAsistente = new JPanel();
        panelAsistente.setLayout(null);
        panelAsistente.setBounds(20, 190, 450, 100);

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(10, 10, 100, 20);
        panelAsistente.add(lblApellido);

        txtApellido = new JTextField();
        txtApellido.setBounds(120, 10, 200, 20);
        panelAsistente.add(txtApellido);

        JLabel lblFechaNac = new JLabel("Fecha Nacimiento:");
        lblFechaNac.setBounds(10, 40, 120, 20);
        panelAsistente.add(lblFechaNac);

        txtFechaNacimiento = new JDateChooser();
        txtFechaNacimiento.setBounds(140, 40, 180, 20);
        panelAsistente.add(txtFechaNacimiento);

        JLabel lblInstitucion = new JLabel("Institución:");
        lblInstitucion.setBounds(10, 70, 100, 20);
        panelAsistente.add(lblInstitucion);

        instituciones = new Vector<>();
        instituciones.add("Ninguna");
        instituciones.addAll(controlUsr.getInstituciones());
        comboInstitucion = new JComboBox<>(instituciones);
        comboInstitucion.setBounds(120, 70, 200, 20);
        panelAsistente.add(comboInstitucion);

        getContentPane().add(panelAsistente);
        panelAsistente.setVisible(false);

        // Panel organizador
        panelOrganizador = new JPanel();
        panelOrganizador.setLayout(null);
        panelOrganizador.setBounds(20, 190, 450, 80);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(10, 10, 100, 20);
        panelOrganizador.add(lblDescripcion);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(120, 10, 200, 20);
        panelOrganizador.add(txtDescripcion);

        JLabel lblWeb = new JLabel("Sitio Web:");
        lblWeb.setBounds(10, 40, 100, 20);
        panelOrganizador.add(lblWeb);

        txtWeb = new JTextField();
        txtWeb.setBounds(120, 40, 200, 20);
        panelOrganizador.add(txtWeb);

        getContentPane().add(panelOrganizador);
        panelOrganizador.setVisible(false);

        toggleCamposAdicionales(false, false);

        rdbtnAsistente.addActionListener(e -> {
            panelAsistente.setVisible(true);
            panelOrganizador.setVisible(false);
            toggleCamposAdicionales(true, false);
        });

        rdbtnOrganizador.addActionListener(e -> {
            panelAsistente.setVisible(false);
            panelOrganizador.setVisible(true);
            toggleCamposAdicionales(false, true);
        });

        // Botón aceptar
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setBounds(92, 390, 100, 25);
        getContentPane().add(btnAceptar);
        btnAceptar.addActionListener(this::cmdRegistroUsuarioActionPerformed);

        // Botón cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(236, 390, 100, 25);
        getContentPane().add(btnCancelar);
        btnCancelar.addActionListener(ev -> {
            limpiarFormulario();
            setVisible(false);
        });
    }

    protected void cmdRegistroUsuarioActionPerformed(ActionEvent event) {
        if (checkFormulario()) {
            try {
                String nickname = textField.getText();
                String nombre = textField1.getText();
                String correo = textField2.getText();
                String contrasena = new String(txtContrasena.getPassword());
                String imagen = imagenSeleccionada; // puede ser null

                if (rdbtnOrganizador.isSelected()) {
                    controlUsr.altaUsuario(
                        nickname, nombre, correo,
                        txtDescripcion.getText(), txtWeb.getText(),
                        null, null, null,
                        true, contrasena, imagen
                    );
                } else {
                    LocalDate fechaNac = txtFechaNacimiento.getDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    String institucion = comboInstitucion.getSelectedItem().toString();
                    if (institucion.equals("Ninguna")) institucion = null;

                    controlUsr.altaUsuario(
                        nickname, nombre, correo,
                        "", "", txtApellido.getText(),
                        fechaNac, institucion,
                        false, contrasena, imagen
                    );
                }

                JOptionPane.showMessageDialog(this, "Usuario creado con éxito.",
                        "Registrar Usuario", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                setVisible(false);
            } catch (excepciones.UsuarioYaExisteException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe un usuario con el nickname o correo ingresado.",
                        "Error", JOptionPane.ERROR_MESSAGE);

            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error interno: " + ex.getMessage(),
                        "Registrar Usuario", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean checkFormulario() {
        String nickname = textField.getText();
        String nombre = textField1.getText();
        String correo = textField2.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (nickname.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar nickname, nombre, correo y contraseña.",
                    "Registrar Usuario", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!rdbtnAsistente.isSelected() && !rdbtnOrganizador.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.",
                    "Registrar Usuario", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void toggleCamposAdicionales(boolean asistente, boolean organizador) {
        txtApellido.setEnabled(asistente);
        txtFechaNacimiento.setEnabled(asistente);
        comboInstitucion.setEnabled(asistente);
        txtDescripcion.setEnabled(organizador);
        txtWeb.setEnabled(organizador);
    }

    private void limpiarFormulario() {
        textField.setText("");
        textField1.setText("");
        textField2.setText("");
        txtContrasena.setText("");
        imagenSeleccionada = null;
        lblArchivoImagen.setText("Ninguna seleccionada");
        rdbtnAsistente.setSelected(false);
        rdbtnOrganizador.setSelected(false);
        txtApellido.setText("");
        txtFechaNacimiento.setDate(null);
        txtDescripcion.setText("");
        txtWeb.setText("");
        grupoRol.clearSelection();
        panelAsistente.setVisible(false);
        panelOrganizador.setVisible(false);
    }

    public void cargarInstituciones() {
        DefaultComboBoxModel<String> model;
        instituciones.clear();
        instituciones.add("Ninguna");
        instituciones.addAll(controlUsr.getInstituciones());
        model = new DefaultComboBoxModel<>(instituciones);
        comboInstitucion.setModel(model);
    }
}
