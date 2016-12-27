package lingv;

import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lingv.bdworking.DBGlossaryWords;


public class EditWord extends JDialog {

    private final DBGlossaryWords dbGlossary;
    private Integer ID;
    private Integer count;
    private String word;
    private String tag;
    private String baseForm;
    private Statement statment;
    
    private boolean getWord(Integer id) {
        Connection connect = dbGlossary.getConnect();
        try {
            statment = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            return false;
        }
        String querySQL = "SELECT * FROM TMP_WORDS WHERE ID=";
        querySQL += id;
        try {
            ResultSet result = statment.executeQuery(querySQL);
            result.first();
            ID = result.getInt("ID");
            count = result.getInt("AMOUNT");
            word = result.getString("WORD");
            tag = result.getString("TAG");
            baseForm = result.getString("BASEFORM");
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    /**
     * Creates new form EditWords
     * @param owner
     * @param dbGlossary
     * @param ID
     */
    public EditWord(JFrame owner, DBGlossaryWords dbGlossary, Integer ID) {
        super(owner, "Edit", true);
        this.dbGlossary = dbGlossary;
        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        getWord(ID);
        jTextField1.setText(word);
        String stk = count.toString();
        jTextField2.setText(stk);
        
        
        System.setProperty("wordnet.database.dir", "WordNet-3.0/dict/");
        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Morphology morphology = Morphology.getInstance();
        String[] candidate = null;
        if(EditWord.getNum(tag) >= 26 && EditWord.getNum(tag) <= 31) {
            String[] arr = morphology.getBaseFormCandidates(word, SynsetType.VERB);
            candidate = new String[arr.length];
            System.arraycopy(arr, 0, candidate, 0, arr.length);
        }
        else if(EditWord.getNum(tag) > 5 && EditWord.getNum(tag) < 9) {
            String[] arr = morphology.getBaseFormCandidates(word, SynsetType.ADJECTIVE);
            candidate = new String[arr.length];
            System.arraycopy(arr, 0, candidate, 0, arr.length);
        }
        else if(EditWord.getNum(tag) > 18 && EditWord.getNum(tag) < 22) {
            String[] arr = morphology.getBaseFormCandidates(word, SynsetType.ADVERB);
            candidate = new String[arr.length];
            System.arraycopy(arr, 0, candidate, 0, arr.length);
        }
        else if(EditWord.getNum(tag) >= 11 && EditWord.getNum(tag) <= 14) {
            String[] arr = morphology.getBaseFormCandidates(word, SynsetType.NOUN);
            candidate = new String[arr.length];
            System.arraycopy(arr, 0, candidate, 0, arr.length);
        }
        else if(EditWord.getNum(tag) == 1 || EditWord.getNum(tag) == 23 || EditWord.getNum(tag) == 15) {
            candidate = new String[1];
            candidate[0] = word;
        }
  
        if(!"".equals(baseForm)) {
            jComboBox2.addItem(baseForm);
            jComboBox2.setSelectedItem(baseForm);
        }
        if(candidate != null) {
            for (String cand : candidate) {
                if(!baseForm.equals(cand)) {
                    jComboBox2.addItem(cand);
                }
            }
            if(candidate.length == 0) {
                jComboBox2.setEditable(true);
            }
        }
        jComboBox2.setEditable(true);
        
        System.out.println(tag + " " + getNum(tag));
        jComboBox1.setSelectedIndex(getNum(tag));
    }

    public static int getNum(String var) {
        if(null != var)
            switch (var) {
            case "CC":
                return 0;
            case "CD":
                return 1;
            case "DT":
                return 2;
            case "EX":
                return 3;
            case "FW":
                return 4;
            case "IN":
                return 5;
            case "JJ":
                return 6;
            case "JJR":
                return 7;
            case "JJS":
                return 8;
            case "LS":
                return 9;
            case "MD":
                return 10;
            case "NN":
                return 11;
            case "NNS":
                return 12;
            case "NNP":
                return 13;
            case "NNPS":
                return 14;
            case "PDT":
                return 15;
            case "POS":
                return 16;
            case "PRP":
                return 17;
            case "PRP$":
                return 18;
            case "RB":
                return 19;
            case "RBR":
                return 20;
            case "RBS":
                return 21;
            case "RP":
                return 22;
            case "SYM":
                return 23;
            case "TO":
                return 24;
            case "UH":
                return 25;
            case "VB":
                return 26;
            case "VBD":
                return 27;
            case "VBG":
                return 28;
            case "VBN":
                return 29;
            case "VBP":
                return 30;
            case "VBZ":
                return 31;
            case "WDT":
                return 32;
            case "WP":
                return 33;
            case "WP$":
                return 34;
            case "WRB":
                return 35;
        }
        return 0;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Word:");

        jLabel2.setText("Amount:");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Tag:");

        jComboBox1.setMaximumRowCount(35);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CC Coordinating conjunction", "CD Cardinal number", "DT Determiner", "EX Existential there", "FW Foreign word", "IN Preposition or subordinating conjunction", "JJ Adjective", "JJR Adjective comparative", "JJS Adjective superlative", "LS List item marker", "MD Modal", "NN Noun singular or mass", "NNS Noun plural", "NNP Proper noun singular", "NNPS Proper noun plural", "PDT Predeterminer", "POS Possessive ending", "PRP Personal pronoun", "PRP$ Possessive pronoun", "RB Adverb", "RBR Adverb comparative", "RBS Adverb superlative", "RP Particle", "SYM Symbol", "TO to", "UH Interjection", "VB Verb base form", "VBD Verb past tense", "VBG Verb gerund or present participle", "VBN Verb past participle", "VBP Verb non3rd person singular present", "VBZ Verb 3rd person singular present", "WDT Whdeterminer", "WP Whpronoun", "WP$ Possessive whpronoun", "WRB Whadverb" }));

        jLabel4.setText("Base form:");

        jButton1.setText("Delete word");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jButton2.setText("Confirm changes");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jComboBox2.setMaximumRowCount(35);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2))
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jLabel3)
                        .addGap(100, 102, Short.MAX_VALUE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(11, 11, 11)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        try {
            int am = Integer.parseInt(jTextField2.getText());
            String select = (String) jComboBox2.getSelectedItem();
            if(jComboBox2.getSelectedItem() == null) {
                select = "";
            }
            Object[] options = { "Yes", "No!" };
            int n = JOptionPane
                    .showOptionDialog(this, "Are you sure to change word '" + word + 
                            "' with tag " + tag + "?",
                            "Confiramtion", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);
            if (n == 0) { 
                String bTag = ((String) jComboBox1.getSelectedItem()).split(" ")[0];
                dbGlossary.update(ID, jTextField1.getText(), am, 
                    bTag, select);
                this.dispose();
            }
        } catch (NumberFormatException ex) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "In field amount need to be a number");
        }
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        
        Toolkit.getDefaultToolkit().beep();
        Object[] options = { "Yes", "No!" };
        int n = JOptionPane
                .showOptionDialog(this, "Are you sure to delete word '" + word + 
                        "' with tag " + tag + "?",
                        "Confiramtion", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) { 
            dbGlossary.delete(ID);
            this.dispose();
        }
    }//GEN-LAST:event_jButton1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
