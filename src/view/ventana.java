package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Font;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import controlador.logica_ventana;

public class ventana extends JFrame {

    public JPanel contentPane;
    public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
    public JCheckBox chb_favorito;
    public JComboBox<String> cmb_categoria;
    public JButton btn_add, btn_modificar, btn_eliminar, btn_exportar;
    public JTable tbl_contactos;
    public JProgressBar progressBar;
    public JTextArea txt_estadisticas;
    public JPopupMenu popupMenu;
    public JMenuItem menuItemEliminar, menuItemModificar;
    private JLabel lbl_buscar;
    private JComboBox<String> cmb_idioma;
    private ResourceBundle bundle;
    private Locale currentLocale;

    public ventana() {
        currentLocale = new Locale("es"); // idioma default
        bundle = ResourceBundle.getBundle("resources.messages", currentLocale);
// Antes (declara una nueva variable local, que no se usa fuera)
JLabel lbl_buscar = new JLabel(bundle.getString("label.search"));

        setTitle(bundle.getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1026, 748);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Selector idioma
        cmb_idioma = new JComboBox<>(new String[] {"Español", "Inglés", "Francés"});
        cmb_idioma.setBounds(800, 10, 150, 30);
        contentPane.add(cmb_idioma);

        cmb_idioma.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) cmb_idioma.getSelectedItem();
                Locale nuevoLocale;
                if (seleccion.equals("Inglés")) nuevoLocale = new Locale("en");
                else if (seleccion.equals("Francés")) nuevoLocale = new Locale("fr");
                else nuevoLocale = new Locale("es");
                actualizarIdioma(nuevoLocale);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 50, 1012, 661);
        contentPane.add(tabbedPane);

        JPanel panelContactos = new JPanel(null);
        tabbedPane.addTab(bundle.getString("tab.contacts"), null, panelContactos, null);

        JLabel lbl_nombres = new JLabel(bundle.getString("label.names"));
        lbl_nombres.setBounds(25, 41, 89, 20);
        lbl_nombres.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_nombres.setForeground(new Color(0, 70, 140));
        panelContactos.add(lbl_nombres);

        txt_nombres = new JTextField();
        txt_nombres.setBounds(124, 28, 427, 31);
        txt_nombres.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panelContactos.add(txt_nombres);

        JLabel lbl_telefono = new JLabel(bundle.getString("label.phone"));
        lbl_telefono.setBounds(25, 80, 89, 20);
        lbl_telefono.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_telefono.setForeground(new Color(0, 70, 140));
        panelContactos.add(lbl_telefono);

        txt_telefono = new JTextField();
        txt_telefono.setBounds(124, 69, 427, 31);
        txt_telefono.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panelContactos.add(txt_telefono);

        JLabel lbl_email = new JLabel(bundle.getString("label.email"));
        lbl_email.setBounds(25, 120, 89, 20);
        lbl_email.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_email.setForeground(new Color(0, 70, 140));
        panelContactos.add(lbl_email);

        txt_email = new JTextField();
        txt_email.setBounds(124, 110, 427, 31);
        txt_email.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panelContactos.add(txt_email);

        // Declarado como atributo y aquí inicializado
        lbl_buscar = new JLabel(bundle.getString("label.search"));
        lbl_buscar.setBounds(25, 590, 192, 20);
        lbl_buscar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_buscar.setForeground(new Color(0, 70, 140));
        panelContactos.add(lbl_buscar);

        txt_buscar = new JTextField();
        txt_buscar.setBounds(220, 585, 776, 31);
        txt_buscar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panelContactos.add(txt_buscar);

        chb_favorito = new JCheckBox(bundle.getString("label.favorite"));
        chb_favorito.setBounds(24, 170, 193, 25);
        chb_favorito.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chb_favorito.setForeground(new Color(46, 139, 87));
        panelContactos.add(chb_favorito);

        cmb_categoria = new JComboBox<>();
        cmb_categoria.setBounds(300, 167, 251, 31);
        cmb_categoria.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panelContactos.add(cmb_categoria);

        actualizarComboCategoria();

        btn_add = new JButton(bundle.getString("button.add"));
        btn_add.setBounds(601, 70, 125, 65);
        btn_add.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn_add.setBackground(new Color(46, 139, 87));
        btn_add.setForeground(Color.YELLOW);
        panelContactos.add(btn_add);

        btn_modificar = new JButton(bundle.getString("button.modify"));
        btn_modificar.setBounds(736, 70, 125, 65);
        btn_modificar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn_modificar.setBackground(new Color(255, 215, 0));
        btn_modificar.setForeground(Color.BLUE);
        panelContactos.add(btn_modificar);

        btn_eliminar = new JButton(bundle.getString("button.delete"));
        btn_eliminar.setBounds(871, 69, 125, 65);
        btn_eliminar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn_eliminar.setBackground(new Color(220, 20, 60));
        btn_eliminar.setForeground(Color.RED);
        panelContactos.add(btn_eliminar);

        btn_exportar = new JButton(bundle.getString("button.export"));
        btn_exportar.setBounds(601, 150, 125, 30);
        btn_exportar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn_exportar.setBackground(new Color(255, 215, 0));
        btn_exportar.setForeground(Color.GREEN);
        panelContactos.add(btn_exportar);

        progressBar = new JProgressBar();
        progressBar.setBounds(25, 200, 971, 20);
        progressBar.setStringPainted(true);
        panelContactos.add(progressBar);

        tbl_contactos = new JTable();
        tbl_contactos.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    bundle.getString("label.names"),
                    bundle.getString("label.phone"),
                    bundle.getString("label.email"),
                    bundle.getString("category.name"),
                    bundle.getString("label.favorite")
                }
        ) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tbl_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrTabla = new JScrollPane(tbl_contactos);
        scrTabla.setBounds(25, 242, 971, 398);
        panelContactos.add(scrTabla);

        popupMenu = new JPopupMenu();
        menuItemEliminar = new JMenuItem(bundle.getString("menu.delete"));
        menuItemModificar = new JMenuItem(bundle.getString("menu.modify"));
        popupMenu.add(menuItemEliminar);
        popupMenu.add(menuItemModificar);
        tbl_contactos.setComponentPopupMenu(popupMenu);

        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(null);
        tabbedPane.addTab(bundle.getString("tab.stats"), null, panelEstadisticas, null);

        txt_estadisticas = new JTextArea();
        txt_estadisticas.setEditable(false);
        txt_estadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JScrollPane scrEstadisticas = new JScrollPane(txt_estadisticas);
        scrEstadisticas.setBounds(25, 25, 950, 650);
        panelEstadisticas.add(scrEstadisticas);

        new logica_ventana(this);
    }

    private void actualizarComboCategoria() {
        cmb_categoria.removeAllItems();
        cmb_categoria.addItem(bundle.getString("category.select"));
        cmb_categoria.addItem(bundle.getString("category.family"));
        cmb_categoria.addItem(bundle.getString("category.friends"));
        cmb_categoria.addItem(bundle.getString("category.work"));
    }

    // Método para actualizar textos a nuevo idioma
    public void actualizarIdioma(Locale locale) {
        this.bundle = ResourceBundle.getBundle("resources.messages", locale);

        setTitle(bundle.getString("app.title"));
        btn_add.setText(bundle.getString("button.add"));
        btn_modificar.setText(bundle.getString("button.modify"));
        btn_eliminar.setText(bundle.getString("button.delete"));
        btn_exportar.setText(bundle.getString("button.export"));
        actualizarComboCategoria();
        menuItemEliminar.setText(bundle.getString("menu.delete"));
        menuItemModificar.setText(bundle.getString("menu.modify"));

        DefaultTableModel model = (DefaultTableModel) tbl_contactos.getModel();
        model.setColumnIdentifiers(new String[]{
            bundle.getString("label.names"),
            bundle.getString("label.phone"),
            bundle.getString("label.email"),
            bundle.getString("category.name"),
            bundle.getString("label.favorite")
        });

        lbl_buscar = new JLabel(bundle.getString("label.search"));


        tbl_contactos.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ventana frame = new ventana();
            frame.setVisible(true);
        });
    }
}
