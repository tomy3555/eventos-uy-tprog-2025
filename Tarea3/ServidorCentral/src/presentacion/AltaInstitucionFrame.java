package presentacion;


import javax.swing.JButton;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JTextField;

import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

public class AltaInstitucionFrame extends JInternalFrame {
    private Set<String> nombresInstituciones = new HashSet<>(); 

    public AltaInstitucionFrame(IControladorUsuario ICU, IControladorEvento iCE) {
        super("Alta de Institución", true, true, true, true);
    	Set<String> nombresExistentes = ICU.getInstituciones();
        setBounds(220, 220, 400, 250);
        setVisible(true);
        setLayout(new BorderLayout());

        if (nombresExistentes != null) {
            nombresInstituciones.addAll(nombresExistentes);
        }

        JPanel panelDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField(18);
        panelDatos.add(lblNombre, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblDescripcion = new JLabel("Descripción:");
        JTextField txtDescripcion = new JTextField(18);
        panelDatos.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtDescripcion, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lblSitioWeb = new JLabel("Sitio web:");
        JTextField txtSitioWeb = new JTextField(18);
        panelDatos.add(lblSitioWeb, gbc);
        gbc.gridx = 1;
        panelDatos.add(txtSitioWeb, gbc);

        add(panelDatos, BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String sitioWeb = txtSitioWeb.getText().trim();
            if (nombre.isEmpty() || descripcion.isEmpty() || sitioWeb.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre, la descripción y el sitio web son obligatorios.");
                return;
            }
            if (nombresInstituciones.contains(nombre.toLowerCase())) {
                JOptionPane.showMessageDialog(this, "Ya existe una institución con ese nombre. Ingrese otro nombre o cancele.");
                return;
            }
            // Alta real de la institución
            try {
                ICU.altaInstitucion(nombre, descripcion, sitioWeb);
            } catch (excepciones.InstitucionYaExisteException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                return;
            } catch (IllegalStateException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Error al crear la institución: " + ex.getMessage());
                return;
            }
            nombresInstituciones.add(nombre.toLowerCase());
            JOptionPane.showMessageDialog(this, "Institución creada con éxito.");
            this.dispose();
        });

        btnCancelar.addActionListener(e -> this.dispose());
    }
}