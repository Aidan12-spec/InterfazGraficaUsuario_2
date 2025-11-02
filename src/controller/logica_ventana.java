package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import vista.ventana;
import modelo.*;


public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {

    private ventana delegado;
    private String nombres, email, telefono, categoria = "";
    private persona persona;
    private List<persona> contactos;
    private boolean favorito = false;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public logica_ventana(ventana delegado) {
        this.delegado = delegado;
        this.tableModel = (DefaultTableModel) delegado.tbl_contactos.getModel();
        this.sorter = new TableRowSorter<>(tableModel);
        delegado.tbl_contactos.setRowSorter(sorter);
        cargarContactosRegistrados();

        // Eventos para botones y elementos
        delegado.btn_add.addActionListener(this);
        delegado.btn_eliminar.addActionListener(this);
        delegado.btn_modificar.addActionListener(this);
        delegado.cmb_categoria.addItemListener(this);
        delegado.chb_favorito.addItemListener(this);
        delegado.btn_exportar.addActionListener(this);

        delegado.tbl_contactos.getSelectionModel().addListSelectionListener(this);

        delegado.tbl_contactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    delegado.popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        delegado.menuItemEliminar.addActionListener(this);
        delegado.menuItemModificar.addActionListener(this);

        delegado.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_A) {
                        actionPerformed(new ActionEvent(delegado.btn_add, ActionEvent.ACTION_PERFORMED, null));
                    } else if (e.getKeyCode() == KeyEvent.VK_E) {
                        actionPerformed(new ActionEvent(delegado.btn_eliminar, ActionEvent.ACTION_PERFORMED, null));
                    } else if (e.getKeyCode() == KeyEvent.VK_M) {
                        actionPerformed(new ActionEvent(delegado.btn_modificar, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
            }
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyTyped(KeyEvent e) {}
        });
        delegado.setFocusable(true);

        delegado.txt_buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { buscarContactos(); }
            @Override public void removeUpdate(DocumentEvent e) { buscarContactos(); }
            @Override public void changedUpdate(DocumentEvent e) { buscarContactos(); }
        });
    }

    private void incializacionCampos() {
        nombres = delegado.txt_nombres.getText();
        email = delegado.txt_email.getText();
        telefono = delegado.txt_telefono.getText();
        categoria = (String) delegado.cmb_categoria.getSelectedItem();
        favorito = delegado.chb_favorito.isSelected();
    }

    private void cargarContactosRegistrados() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    contactos = new personaDAO(new persona()).leerArchivo();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(delegado, "Existen problemas al cargar todos los contactos");
                }
                return null;
            }
            @Override
            protected void done() {
                tableModel.setRowCount(0);
                for (persona contacto : contactos) {
                    tableModel.addRow(new Object[]{
                        contacto.getNombre(),
                        contacto.getTelefono(),
                        contacto.getEmail(),
                        contacto.getCategoria(),
                        contacto.isFavorito() // Boolean, no texto
                    });
                }
                actualizarEstadisticas();
                delegado.progressBar.setValue(100);
            }
        };
        delegado.progressBar.setValue(0);
        worker.execute();
    }

    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_telefono.setText("");
        delegado.txt_email.setText("");
        delegado.cmb_categoria.setSelectedIndex(0);
        delegado.chb_favorito.setSelected(false);
        incializacionCampos();
        cargarContactosRegistrados();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        incializacionCampos();
        if (e.getSource() == delegado.btn_add) {
            if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) {
                if (!categoria.equals("Elija una Categoria") && !categoria.isEmpty()) {
                    persona = new persona(nombres, telefono, email, categoria, favorito);
                    if (new personaDAO(persona).escribirArchivo()) {
                        limpiarCampos();
                        JOptionPane.showMessageDialog(delegado, "El Contacto ha sido Registrado!!!");
                    } else {
                        JOptionPane.showMessageDialog(delegado, "No se pudo guardar todos los archivos. Intentelo de nuevo");
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, "Seleccione una Categoria!!!");
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Deben estar llenados todos los campos!!!");
            }
        } else if (e.getSource() == delegado.btn_eliminar || e.getSource() == delegado.menuItemEliminar) {
            eliminarContacto();
        } else if (e.getSource() == delegado.btn_modificar || e.getSource() == delegado.menuItemModificar) {
            modificarContacto();
        } else if (e.getSource() == delegado.btn_exportar) {
            exportarCSV();
        }
    }

    private void eliminarContacto() {
        int row = delegado.tbl_contactos.getSelectedRow();
        if (row != -1) {
            int modelRow = delegado.tbl_contactos.convertRowIndexToModel(row);
            int confirm = JOptionPane.showConfirmDialog(delegado, "¿Estás seguro de eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                contactos.remove(modelRow);
                try {
                    new personaDAO(new persona()).actualizarContactos(contactos);
                    cargarContactosRegistrados();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(delegado, "El Contacto ha sido eliminado");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(delegado, "Error al eliminar el contacto");
                }
            }
        } else {
            JOptionPane.showMessageDialog(delegado, "Seleccione un contacto para eliminar");
        }
    }

    private void modificarContacto() {
        int row = delegado.tbl_contactos.getSelectedRow();
        if (row != -1) {
            int modelRow = delegado.tbl_contactos.convertRowIndexToModel(row);
            if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) {
                if (!categoria.equals("Seleccione una Categoria") && !categoria.isEmpty()) {
                    contactos.get(modelRow).setNombre(nombres);
                    contactos.get(modelRow).setTelefono(telefono);
                    contactos.get(modelRow).setEmail(email);
                    contactos.get(modelRow).setCategoria(categoria);
                    contactos.get(modelRow).setFavorito(favorito);
                    try {
                        new personaDAO(new persona()).actualizarContactos(contactos);
                        cargarContactosRegistrados();
                        limpiarCampos();
                        JOptionPane.showMessageDialog(delegado, "El Contacto ha sido modificado!!!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(delegado, "Error al modificar el contacto");
                    }
                } else {
                    JOptionPane.showMessageDialog(delegado, "Seleccione una Categoria!!!");
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Deben estar llenados todos los campos!!!");
            }
        } else {
            JOptionPane.showMessageDialog(delegado, "Seleccione un contacto para modificar");
        }
    }

    private void buscarContactos() {
        String busqueda = delegado.txt_buscar.getText().toLowerCase();
        sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + busqueda, 0));
    }

    private void actualizarEstadisticas() {
        int familia = 0, amigo = 0, trabajo = 0;
        for (persona p : contactos) {
            switch (p.getCategoria()) {
                case "Familia":
                    familia++;
                    break;
                case "Amigo":
                    amigo++;
                    break;
                case "Trabajo":
                    trabajo++;
                    break;
            }
        }
        delegado.txt_estadisticas.setText("Estadísticas de Contactos:\n\n"
                + "Familia: " + familia + "\n"
                + "Amigos: " + amigo + "\n"
                + "Trabajo: " + trabajo + "\n"
                + "Total: " + contactos.size());
    }

    private void exportarCSV() {
        try (FileWriter writer = new FileWriter("Gestion_contactos.csv")) {
            writer.write("Nombre,Telefono,Email,Categoria,Favorito\n");
            for (persona p : contactos) {
                writer.write(p.getNombre() + "," + p.getTelefono() + "," + p.getEmail() + "," + p.getCategoria() + "," + p.isFavorito() + "\n");
            }
            JOptionPane.showMessageDialog(delegado, "Contactos exportados a 'Gestion_contactos.csv'");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(delegado, "Error al exportar");
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = delegado.tbl_contactos.getSelectedRow();
            if (row != -1) {
                int modelRow = delegado.tbl_contactos.convertRowIndexToModel(row);
                cargarContacto(modelRow);
            }
        }
    }

    private void cargarContacto(int index) {
        delegado.txt_nombres.setText(contactos.get(index).getNombre());
        delegado.txt_telefono.setText(contactos.get(index).getTelefono());
        delegado.txt_email.setText(contactos.get(index).getEmail());
        delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
        delegado.cmb_categoria.setSelectedItem(contactos.get(index).getCategoria());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == delegado.cmb_categoria) {
            categoria = (String) delegado.cmb_categoria.getSelectedItem();
        } else if (e.getSource() == delegado.chb_favorito) {
            favorito = delegado.chb_favorito.isSelected();
        }
    }
}
