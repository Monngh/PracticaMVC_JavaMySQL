package Controlador;

import Modelo.MDB;
import Vista.usuarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gaelo
 *
 */
public class ControladorUsuario implements ActionListener {

    usuarios vista;
    MDB _model;

    // No es necesario que estas sean variables de clase si solo se usan dentro de un método
    // List<ArrayList<String>> datosObtenidos = new ArrayList<ArrayList<String>>();
    // ArrayList<String> renglonObtenido = new ArrayList<String>();

    public ControladorUsuario(usuarios vista, MDB _model) {
        this.vista = vista;
        this._model = _model;

        // Bindeo de listeners
        this.vista.cmdAltas.addActionListener(this);
        this.vista.cmdBajas.addActionListener(this);
        this.vista.cmdConsultas.addActionListener(this);
        this.vista.cmdModificaciones.addActionListener(this);
        this.vista.cmdBuscarID.addActionListener(this);

        this.vista.jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Este método ahora solo actúa como un despachador
        if (ae.getSource() == vista.cmdAltas) {
            altaUsuario();
        } else if (ae.getSource() == vista.cmdModificaciones) {
            modificarUsuario();
        } else if (ae.getSource() == vista.cmdBajas) {
            bajaUsuario();
        } else if (ae.getSource() == vista.cmdConsultas) {
            llenarTabla();
        } else if (ae.getSource() == vista.cmdBuscarID) {
            buscarPorID();
        }
    }

    // --- MÉTODOS PRIVADOS PARA CADA ACCIÓN ---

    private void altaUsuario() {
        String id = vista.txtID.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String usuario = vista.txtUser.getText().trim();
        String psw = vista.txtPsw.getText().trim();

        // --- VALIDACIÓN ---
        if (id.isEmpty() || nombre.isEmpty() || usuario.isEmpty() || psw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos para dar de alta.");
            return; // Detiene la ejecución
        }

        String leyenda = _model.registrarAlta("tbusuarios", 
                "" + id + " , '" + nombre + "' , '" + usuario + "' , '" + psw + "'");
        
        JOptionPane.showMessageDialog(null, leyenda);
        llenarTabla();
        limpiarCampos();
    }

    private void modificarUsuario() {
        String id = vista.txtID.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String usuario = vista.txtUser.getText().trim();
        String psw = vista.txtPsw.getText().trim();

        // --- VALIDACIÓN ---
        if (id.isEmpty() || nombre.isEmpty() || usuario.isEmpty() || psw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un registro y asegúrese de que todos los campos estén llenos.");
            return; // Detiene la ejecución
        }

        String leyenda = _model.modificarRegistro("tbusuarios", 
                "nombre='" + nombre + "' , usuario = '" + usuario + "' ,  psw= '" + psw + "'", "id = " + id + "");
        
        JOptionPane.showMessageDialog(null, leyenda);

        // Refrescar la tabla y limpiar campos después de la acción
        llenarTabla();
        limpiarCampos();
    }

    private void bajaUsuario() {
        String id = vista.txtID.getText().trim();

        // --- VALIDACIÓN ---
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese o seleccione el ID del usuario a eliminar.");
            return; // Detiene la ejecución
        }
        
        // --- CONFIRMACIÓN ---
        int respuesta = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar al usuario con ID: " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            String leyenda = _model.borrarRegistro("TbUsuarios", "id = " + id);
            JOptionPane.showMessageDialog(null, leyenda);

            // Refrescar la tabla y limpiar campos después de la acción
            llenarTabla();
            limpiarCampos();
        }
        // Si la respuesta es NO, simplemente no hace nada.
    }

    private void llenarTabla() {
        String[] columnNames = {"ID", "Nombre", "Usuario", "Password"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        List<ArrayList<String>> datosObtenidos = _model.consultarDatos("tbusuarios", "id, nombre, usuario, psw", "1=1");

        for (ArrayList<String> row : datosObtenidos) {
            // Revisamos que la fila no sea el indicador de "NO"
            if (!row.get(0).equals("NO")) {
                Object[] rowData = row.toArray();
                tableModel.addRow(rowData);
            }
        }
        
        // Recuerda cambiar 'jTable1' por el nombre real de tu tabla
        vista.jTable1.setModel(tableModel);
    }

    private void buscarPorID() {
        String idBusqueda = vista.txtID.getText().trim();

        // --- VALIDACIÓN---
        if (idBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID para buscar.");
            return; 
        }

        List<ArrayList<String>> datosObtenidos = _model.consultarDatos("tbusuarios", "nombre, usuario, psw", "id = " + idBusqueda);

        if (datosObtenidos.size() > 0) {
            ArrayList<String> renglonObtenido = datosObtenidos.get(0);

            if (!renglonObtenido.get(0).equals("NO")) {
                vista.txtNombre.setText(renglonObtenido.get(0));
                vista.txtUser.setText(renglonObtenido.get(1));
                vista.txtPsw.setText(renglonObtenido.get(2));
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún usuario con el ID: " + idBusqueda);
                limpiarCamposMenosID(); // Limpiamos campos excepto el ID
            }
        }
    }

    // --- MÉTODOS DE AYUDA ---

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = vista.jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            vista.txtID.setText(vista.jTable1.getValueAt(selectedRow, 0).toString());
            vista.txtNombre.setText(vista.jTable1.getValueAt(selectedRow, 1).toString());
            vista.txtUser.setText(vista.jTable1.getValueAt(selectedRow, 2).toString());
            vista.txtPsw.setText(vista.jTable1.getValueAt(selectedRow, 3).toString());
        }
    }
    
    // Método útil para limpiar los campos después de una acción
    private void limpiarCampos() {
        vista.txtID.setText("");
        vista.txtNombre.setText("");
        vista.txtUser.setText("");
        vista.txtPsw.setText("");
    }

    // Método útil para limpiar campos después de una búsqueda fallida
    private void limpiarCamposMenosID() {
        vista.txtNombre.setText("");
        vista.txtUser.setText("");
        vista.txtPsw.setText("");
    }
}