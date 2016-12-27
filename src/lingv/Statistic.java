/*
 *  Package: lingv
 *  Classname: Statistic
 *  Version: 1.0
 *  Developed and protected by Nikolay Mandrik
 */

package lingv;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map.Entry;
import javafx.util.Pair;
import lingv.bdworking.DBGlossaryWords;


public class Statistic extends javax.swing.JFrame {

    private final DBGlossaryWords dbGlossary;
    private final Object[][] model;
    /**
     * Creates new form Statistic
     * @param dbGlossary
     */
    public Statistic(DBGlossaryWords dbGlossary) {
        this.dbGlossary = dbGlossary;
        model = dbGlossary.getRowsNN();
        
        initComponents();
        
        fillTableWords();
        createTableWords();
        fillTableTags();
        createTableTags();
        fillTableSeq();
        createTableSeq();
        
        setResizable(false);
        Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        setSize (sSize);
        setVisible(true);
    }
    
    private void fillTableWords() {
        Object[][] model1 = new Object[model.length][4]; 
        for(int i = 0; i < model.length; i++) {
            model1[i][0] = model[i][0];
            model1[i][1] = this.convert((String) model[i][1]);
            model1[i][2] = model[i][2];
            model1[i][3] = model[i][3];
        }
        tableWords.setAutoCreateRowSorter(true);
        tableWords.setModel(new javax.swing.table.DefaultTableModel(model1, new String [] {
                "Word", "Tag disription", "Count", "Base Form"})
        {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, Integer.class, 
                java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableWords.setRowHeight(20);
        tableWords.setShowVerticalLines(false);
        tableWords.getTableHeader().setReorderingAllowed(false);
    
    }
    
    
    private void fillTableTags() {
        Object[][] model2 = new Object[36][3]; 
        ArrayList<String> tags = dbGlossary.getAllTags();
        Integer[] counts = new Integer[36];
        for(int i = 0; i < 36; i++) {
            model2[i][0] = tags.get(i);
            model2[i][1] = this.convert(tags.get(i));
            counts[i] = 0;
        }
        
        for(int i = 0; i < model.length; i++) {
            counts[EditWord.getNum((String) model[i][1])] += (Integer) model[i][2];
        }
        for(int i = 0; i < 36; i++) {
            model2[i][2] = counts[i];
        }
        tableTags.setAutoCreateRowSorter(true);
        tableTags.setModel(new javax.swing.table.DefaultTableModel(model2, new String [] {
                "Tag", "Disription", "Count"})
        {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, 
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableTags.setRowHeight(20);
        tableTags.setShowVerticalLines(false);
        tableTags.getTableHeader().setReorderingAllowed(false);
    }
    
    private void fillTableSeq() {
        
        Object[][] model3 = dbGlossary.getRowsSEQ();
        
        tableSeq.setAutoCreateRowSorter(true);
        tableSeq.setModel(new javax.swing.table.DefaultTableModel(model3, new String [] {
                "First tag", "Second tag", "Count"})
        {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, 
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableSeq.setRowHeight(20);
        tableSeq.setShowVerticalLines(false);
        tableSeq.getTableHeader().setReorderingAllowed(false);
    }
    
    private void createTableWords() {
        
        TableColumn column = tableWords.getColumnModel().getColumn(0);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column = tableWords.getColumnModel().getColumn(2);
        column.setMinWidth(100);
        column.setMaxWidth(100);
        column.setPreferredWidth(100);
        column = tableWords.getColumnModel().getColumn(1);
        column.setMinWidth(300);
        column.setMaxWidth(300);
        column.setPreferredWidth(300);
        

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        tableWords.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        tableWords.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        tableWords.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
        
        centerRenderer = (DefaultTableCellRenderer)
            tableWords.getTableHeader().getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTableHeader header = tableWords.getTableHeader();
        header.setDefaultRenderer(centerRenderer);

        pack();
    }
    
    private void createTableTags() {
        
        TableColumn column = tableTags.getColumnModel().getColumn(0);
        column.setMinWidth(70);
        column.setMaxWidth(70);
        column.setPreferredWidth(70);
        column = tableTags.getColumnModel().getColumn(1);
        column.setMinWidth(270);
        column.setMaxWidth(270);
        column.setPreferredWidth(270);
        

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        tableTags.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        tableTags.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        
        centerRenderer = (DefaultTableCellRenderer)
            tableTags.getTableHeader().getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTableHeader header = tableTags.getTableHeader();
        header.setDefaultRenderer(centerRenderer);

        pack();
    }
    
    private void createTableSeq() {
        
        TableColumn column = tableSeq.getColumnModel().getColumn(0);
        column.setMinWidth(70);
        column.setMaxWidth(70);
        column.setPreferredWidth(70);
        column = tableSeq.getColumnModel().getColumn(1);
        column.setMinWidth(70);
        column.setMaxWidth(70);
        column.setPreferredWidth(70);
        

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        tableSeq.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        
        centerRenderer = (DefaultTableCellRenderer)
            tableSeq.getTableHeader().getDefaultRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTableHeader header = tableSeq.getTableHeader();
        header.setDefaultRenderer(centerRenderer);

        pack();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tableTags = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableWords = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableSeq = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Statistic");

        tableTags.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag", "Discription", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableTags.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(tableTags);

        tableWords.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Word", "Tag", "Amount", "Base form"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableWords.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tableWords);

        tableSeq.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "First Tag", "Second Tag", "Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tableSeq);

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(514, 514, 514))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tableSeq;
    private javax.swing.JTable tableTags;
    private javax.swing.JTable tableWords;
    // End of variables declaration//GEN-END:variables
}
