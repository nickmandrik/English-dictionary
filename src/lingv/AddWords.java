
package lingv;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import javafx.util.Pair;
import javax.swing.*;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import javax.swing.SwingWorker;
import lingv.addingwords.ParseWords;
import lingv.bdworking.DBGlossaryWords;


public class AddWords extends JDialog
                             implements ActionListener, 
                                        PropertyChangeListener {

    private Task task;
    private final HashMap<Pair<String, String>, Integer> map;

    @Override
    public void actionPerformed(ActionEvent e) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task(dbGlossary, map);
        progressBar.setStringPainted(true);
        task.addPropertyChangeListener(this);
        task.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            
            progressBar.setString(progress + "%");
        }
    }
 
    class Task extends SwingWorker<Void, Void> {
        DBGlossaryWords dbGlossary;
        HashMap<Pair<String, String>, Integer> map;
    
        ResultSet result;
        
        Task(DBGlossaryWords dbGlossary, HashMap<Pair<String, String>, Integer> map) {
            this.dbGlossary = dbGlossary;
            this.map = map;
            it = map.entrySet().iterator();
        }
        
        private int count;
        Iterator it;
        public void addWords(HashMap<Pair<String, String>, Integer> map) {           
            
            count = dbGlossary.getLastID();
            if(count > -1) {
                int N = map.size();
                double C = 0;
                int K = 0;
                               
                while (it.hasNext() && !isCancelled()) {
                    C+=1;
                    if((C/N*100) >= K) {
                        setProgress(Math.min(K, 100));
                        K ++;
                    }
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    String word = ((Pair<String, String>) pair.getKey()).getKey();
                    String tag = ((Pair<String, String>) pair.getKey()).getValue();
                    String[] partsWord = word.split("'");
                    if(partsWord.length != 1) {
                        word = "";
                        for(String part: partsWord) {
                            if(!"".equals(part)) {
                                word += part + "''";
                            }
                        }
                    }
                    
                    partsWord = word.split("-");
                    if(partsWord.length > 1) {
                        word = "";
                        for(String part: partsWord) {
                            if(!"".equals(part)) {
                                word += part;
                            }
                        }
                    }
                    partsWord = word.split("=");
                    if(partsWord.length > 1) {
                        word = "";
                        for(String part: partsWord) {
                            if(!"".equals(part)) {
                                word += part;
                            }
                        }
                    }
                    partsWord = word.split("\"");
                    if(partsWord.length > 1) {
                        word = "";
                        for(String part: partsWord) {
                            if(!"".equals(part)) {
                                word += part;
                            }
                        }
                    }
                    partsWord = word.split(" ");
                    if(partsWord.length > 1) {
                        word = "";
                        for(String part: partsWord) {
                            if(!"".equals(part)) {
                                word += part;
                            }
                        }
                    }
                    if(word.length() > 0) {
                    while(word.length() > 0 && word.charAt(word.length()-1) == '\'' ) {
                        word = word.substring(0, word.length()-1);
                    }
                    }
                    
                    if(!(tag.equals(".") || word.equals("") || tag.equals(",") || tag.equals(":"))) {
                        count = dbGlossary.insert(word, (Integer) pair.getValue(), tag, count);
                    }
                    
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }

        @Override
        public Void doInBackground() {
            setProgress(0);
            addWords(map);
            return null;
        }

        
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            Object[] options = { "OK" } ;
            dispose();
            StringBuilder stk = new StringBuilder();
            if(this.isCancelled()) 
                stk.append(count).append(" words from text were added");
            else {
                stk.append("All words from text were added");
            }
            int n = JOptionPane.showOptionDialog(null, stk.toString(), 
                    "Finish", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        }
    }
    
    private final DBGlossaryWords dbGlossary;
    public AddWords (JFrame owner, DBGlossaryWords dbGlossary, File file) {
        super(owner, "Add words to dict", true);
        this.dbGlossary = dbGlossary;

        ParseWords psw = new ParseWords(file);
        map = psw.getWordsAndValues();
            
        initComponents();
        startButton.addActionListener(this);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
        setResizable(false);
        
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Add words to dictionary");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        progressBar.setBackground(new java.awt.Color(255, 0, 51));
        progressBar.setFont(new java.awt.Font("Times New Roman", 3, 12)); // NOI18N
        progressBar.setForeground(new java.awt.Color(0, 0, 0));
        progressBar.setToolTipText("");
        progressBar.setValue(1);

        startButton.setText("Start");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Toolkit.getDefaultToolkit().beep();
        Object[] options = { "Yes", "No!" };
        int n = JOptionPane
                .showOptionDialog(evt.getWindow(), "Are you sure to stop the proccess?",
                        "Confiramtion", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) { 
            if(task != null)
                task.cancel(true);
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
