/*
 *  Package: lingv
 *  Classname: WorkDatabase
 *  Version: 1.0
 *  Developed and protected by Nikolay Mandrik
 */

package lingv;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import lingv.bdworking.DBGlossaryWords;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

public class WorkDatabase extends javax.swing.JFrame {

    DBGlossaryWords dbGlossary;
    Statistic stat;
     
    /**
     * Creates new form WorkDatabase
     */
    public WorkDatabase() {
        try {
            dbGlossary = new DBGlossaryWords();
            initComponents();
            jList1.setBackground(new Color(240,240,240));
            baseform.setVisible(false);
            createTable();
            setTextInFields();
            setResizable(false);
            Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();
            setSize (sSize);
        }
        catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Server work is not correct.\nPlease try again for a few minutes");
            dispose();
        }
    }

    
    private void createTable() {

        TableColumn column = table.getColumnModel().getColumn(0);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column = table.getColumnModel().getColumn(1);
        column.setMinWidth(70);
        column.setMaxWidth(70);
        column.setPreferredWidth(70);
        column = table.getColumnModel().getColumn(2);
        column.setMinWidth(70);
        column.setMaxWidth(70);
        column.setPreferredWidth(70);
        column = table.getColumnModel().getColumn(3);
        column.setMinWidth(300);
        column.setMaxWidth(300);
        column.setPreferredWidth(300);
        column = table.getColumnModel().getColumn(5);
        column.setMinWidth(50);
        column.setMaxWidth(50);
        column.setPreferredWidth(50);
//        column = table.getColumnModel().getColumn(4);
//        column.setMinWidth(100);
//        column.setMaxWidth(100);
//        column.setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        table.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        table.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
        table.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
        
        centerRenderer = (DefaultTableCellRenderer)
            table.getTableHeader().getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(centerRenderer);
       
        table.addMouseListener(new ExtMouseAdapter(this));
        pack();
    }
    
    //
    public class ExtMouseAdapter extends MouseAdapter {
        // Таймаут ожидания второго клика в миллисекундах.
        private int doubleClickDelay = 300;
        // Таймер ожидания второго клика.
        private Timer timer;
        private JFrame frame;

        public ExtMouseAdapter(JFrame frame) {
            this.frame = frame;
            ActionListener actionListener = new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent e) {
                    timer.stop();
                }
            };
            timer = new Timer(doubleClickDelay, actionListener);
            timer.setRepeats(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            ActionEvent ev = new ActionEvent(
                    e.getSource(), e.getID(), e.paramString());
            if (timer.isRunning()) {
                timer.stop();
                fireSingleClick(ev);
                fireDoubleClick(ev);
            } else {
                timer.start();
                fireSingleClick(ev);
            }
        }

        /**
         * Обработчик однократного клика.
         * @param e Событие.
         */
        protected void fireSingleClick(ActionEvent e) {
            if(table.getSelectedColumn() == 5) {
                int numRow = table.getSelectedRow();
                if("X".equals((String) table.getValueAt(numRow, 5))) {
                    Integer id = (Integer) table.getValueAt(numRow, 2);
                    EditWord edWor = new EditWord(frame, dbGlossary, id);
                    edWor.setVisible(true);
                    updateEdit(numRow, id);
                }
            }
            if(table.getSelectedColumn() == 4) {
                int numRow = table.getSelectedRow();
                if(!"".equals((String) table.getValueAt(numRow, 4))) {
                    jList1.removeAll();
                    baseform.setVisible(true);
                    jList1.setVisible(true);
                    String baseForm = (String) table.getValueAt(numRow, 4);
                    baseform.setText(baseForm);
                    ArrayList<String> arr = dbGlossary.findWordByBaseForm(baseForm);
                    
                    for(String str: arr) {
                        jList1.setModel(new javax.swing.AbstractListModel() {
                            ArrayList<String> strings = arr;
                            public int getSize() { return strings.size(); }
                            public Object getElementAt(int i) { return strings.get(i); }
                        });
                    }
                }
            }
        }

        /**
         * Обработчик двойного клика.
         * @param e Событие.
         */
        protected void fireDoubleClick(ActionEvent e) {
            
        }
    }
    
    
    public Integer findIDbyWord(String word) {
        Connection connect = dbGlossary.getConnect();
        Statement statment = null;
        try {
            statment = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            return -1;
        }
        String querySQL = "SELECT * FROM TMP_WORDS WHERE WORD='";
        querySQL += word;
        querySQL += "'";
        try {
            ResultSet result = statment.executeQuery(querySQL);
            result.first();
            return result.getInt("ID");
        } catch (SQLException ex) {
            return -1;
        }
    }
    
    private void setTextInFields() {
        for(int i = 0; i < 100; i++) {
            if(dbGlossary.getIDs()[i] != -1) {
                table.getModel().setValueAt(dbGlossary.getIDs()[i], i, 2);
                table.getModel().setValueAt(dbGlossary.getWords()[i], i, 0);
                table.getModel().setValueAt(dbGlossary.getCounts()[i], i, 1);
                table.getModel().setValueAt(this.convert(dbGlossary.getTags()[i]), i, 3);
                table.getModel().setValueAt("X", i, 5);
                table.getModel().setValueAt(dbGlossary.getBaseForms()[i], i, 4);
            }
            else {
                table.getModel().setValueAt("", i, 2);
                table.getModel().setValueAt("", i, 0);
                table.getModel().setValueAt("", i, 1);
                table.getModel().setValueAt("", i, 5);
                table.getModel().setValueAt("", i, 3);
                table.getModel().setValueAt("", i, 4);
            }
        }
    }
    
    public void updateEdit(Integer numRow, int id) {
        if(dbGlossary.isUpdate) {
            Connection connect = dbGlossary.getConnect();
            Statement statment = null;
            try {
                statment = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException ex) { }
            String querySQL = "SELECT * FROM TMP_WORDS WHERE ID=";
            querySQL += id;
            try {
                ResultSet result = statment.executeQuery(querySQL);
                result.first();
                table.getModel().setValueAt(result.getInt("ID"), numRow, 2);
                table.getModel().setValueAt(result.getString("WORD"), numRow, 0);
                table.getModel().setValueAt(result.getInt("AMOUNT"), numRow, 1);
                table.getModel().setValueAt(this.convert(result.getString("TAG")), numRow, 3);
                table.getModel().setValueAt("X", numRow, 5);
                table.getModel().setValueAt(result.getString("BASEFORM"), numRow, 4);
            } catch (SQLException ex) { }
        }
        else {
            table.getModel().setValueAt("", numRow, 2);
            table.getModel().setValueAt("", numRow, 0);
            table.getModel().setValueAt("", numRow, 1);
            table.getModel().setValueAt("", numRow, 3);
            table.getModel().setValueAt("", numRow, 5);
            table.getModel().setValueAt("", numRow, 4);
        }
    }
    
    public void updateAdd() {
        dbGlossary.select();
        setTextInFields();
    }

    public String convert(String str) {
        Connection connect = dbGlossary.getConnect();
        Statement statment = null;
        try {
            statment = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            return "none!!!";
        }
        String querySQL = "SELECT DESCRIP FROM TAGS WHERE TAG='";
        querySQL += str;
        querySQL += "'";
        try {
            ResultSet result = statment.executeQuery(querySQL);
            result.first();
            return result.getString("DESCRIP");
        } catch (SQLException ex) {
            return "none!!!";
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        baseform = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dictionary");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButton1.setText("First");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Next");
        jButton2.setToolTipText("");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Previous");
        jButton3.setToolTipText("");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Last");
        jButton4.setToolTipText("");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        table.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        table.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Word", "Count", "ID", "Tag", "Base Form", "Edit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setRowHeight(20);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        baseform.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        baseform.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        baseform.setText("efwwf");

        jScrollPane3.setBorder(null);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jScrollPane3.setViewportView(jList1);

        jMenu1.setText("Dictionary");
        jMenu1.setToolTipText("");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem5.setText("Open text");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem6.setText("Add words");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jMenuItem7.setText("Clear dictionary");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Sort");

        jMenu3.setText("Word");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("A -> Z");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem4.setText("Z -> A");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenu2.add(jMenu3);

        jMenu4.setText("Count");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("1 -> 100");
        jMenuItem1.setToolTipText("");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setText("100 -> 1");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem3);

        jMenu2.add(jMenu4);

        jMenu7.setText("Base form");

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("A -> Z");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem9);

        jMenuItem12.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem12.setText("Z -> A");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem12);

        jMenu2.add(jMenu7);

        jMenuBar1.add(jMenu2);

        jMenu5.setText("Annontation");

        jMenuItem8.setText("Add text");
        jMenuItem8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem8MouseClicked(evt);
            }
        });
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem8);

        jMenuBar1.add(jMenu5);

        jMenu6.setText("Statistic");

        jMenuItem10.setText("Show");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem10);

        jMenuItem11.setText("Clear");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem11);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(503, 503, 503)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(531, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(baseform, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(115, 115, 115))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                        .addGap(7, 7, 7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(baseform)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
         try {
            dbGlossary.connect.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Closing database is uncorrect");
        } catch (NullPointerException ex) {
        } 
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        dbGlossary.sortUpWords();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        dbGlossary.sortDownWords();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        dbGlossary.sortUpAmount();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        dbGlossary.sortDownAmount();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        dbGlossary.previousTen();
        setTextInFields();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dbGlossary.nextTen();
        setTextInFields();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dbGlossary.firstTen();
        setTextInFields();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        dbGlossary.lastTen();
        setTextInFields();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        Toolkit.getDefaultToolkit().beep();
        Object[] options = { "Yes", "No!" };
        int n = JOptionPane.showOptionDialog(null, "Do you want to clear the dictionary?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[1]);
        if (n == 0) {
            dbGlossary.deleteWords();
            updateAdd();
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // JFileChooser to choose file to get from it information
        Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
        JFileChooser chooser = new JFileChooser();
        UIManager.put("FileChooser.readOnly", old);  
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory  (new File(WorkDatabase.class.getResource("WorkDatabase.class").getPath()));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files (*txt)", "txt", "text");
        chooser.setFileFilter(filter);
        
        // work with choosen file
        if(chooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();  
            ProcessBuilder pb = new ProcessBuilder("Notepad.exe", file.getPath());
            try {
                pb.start();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // JFileChooser to choose file to get from it information
        Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
        JFileChooser chooser = new JFileChooser();
        UIManager.put("FileChooser.readOnly", old);  
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory  (new File(WorkDatabase.class.getResource("WorkDatabase.class").getPath()));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files (*txt)", "txt", "text");
        chooser.setFileFilter(filter);
        
        // work with choosen file
        if(chooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = chooser.getSelectedFile();
            AddWords fram = new AddWords(this, dbGlossary, file);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            updateAdd();
            
//            DBGlossaryWords dbW = new DBGlossaryWords();
//            dbW.addWords(words);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem8MouseClicked

    }//GEN-LAST:event_jMenuItem8MouseClicked

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        Boolean old = UIManager.getBoolean("FileChooser.readOnly");  
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
        JFileChooser chooser = new JFileChooser();
        UIManager.put("FileChooser.readOnly", old);  
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory  (new File(WorkDatabase.class.getResource("WorkDatabase.class").getPath()));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files (*txt)", "txt", "text");
        chooser.setFileFilter(filter);
        
        if(chooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION) {
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = chooser.getSelectedFile();
            Annontation fram = new Annontation(this, dbGlossary, file);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        Object[] options = { "Yes", "No!" };
        int n = JOptionPane.showOptionDialog(null, "Do you want to clear data of statistic?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            dbGlossary.deleteFromSEQ();
            dbGlossary.deleteFromANN();
            JOptionPane.showMessageDialog(this, "Clear data finished successfull");
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        Statistic st = new Statistic(dbGlossary);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        dbGlossary.sortUpBaseForm();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        dbGlossary.sortDownBaseForm();
        setTextInFields();
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
                        | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(WorkDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WorkDatabase().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel baseform;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
