/*
 *  Package: lingv
 *  Classname: Annontation
 *  Version: 1.0
 *  Developed and protected by Nikolay Mandrik
 */

package lingv;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import lingv.addingwords.ParseWords;
import lingv.bdworking.DBGlossaryWords;


public class Annontation extends javax.swing.JDialog {
   

    private File file;
    private ArrayList<JLabel> labels;
    private ArrayList<JComboBox> boxs;
    private HashMap<String, ArrayList<String>> wordTags;
    DBGlossaryWords dbGlossary;
    
    /**
     * Creates new form Annontation
     * @param owner
     * @param dbGlossary
     * @param file
     */
    public Annontation(JFrame owner, DBGlossaryWords dbGlossary, File file) {
        super(owner, "Annontation text", true);
        this.dbGlossary = dbGlossary;
        this.file = file;
        
        
        
        String text = "";
        try {
            text += new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Need to access the File " + file.getPath());
        }
        
        getWordsAndCandTags(text);

        initComponents();
        
        String regex = "[a-zA-Z]+";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
       
        
        labels = new ArrayList();
        boxs = new ArrayList();
        content.setLayout(null);
        
        int index = 0;
        int numRows = 0;
        int countInRow = 0;
        StringBuilder stb = new StringBuilder();
        boolean flag = true;
        for(int i = 0; i < text.length(); i++) {
            char symb = text.charAt(i);
            if( (65 <= symb && symb <=90) || (97 <= symb && symb <=122) ) {
                if(!flag) {
                    String word = stb.toString();
                    JLabel label = new JLabel(word);
                    label.setFont(new java.awt.Font("Times New Roman", 0, 14));
                    
                    label.setSize(label.getMinimumSize().width + 2, 17);
                    if(countInRow + label.getMinimumSize().width + 2 > content.getWidth()) {
                        numRows++;
                        label.setLocation(0,numRows*17);
                        countInRow = label.getMinimumSize().width + 2;
                    }
                    else {
                        label.setLocation(countInRow,numRows*17);
                        countInRow += label.getMinimumSize().width + 2; 
                    }
//                    label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                    content.add(label);
                    stb = new StringBuilder();
                }
                stb.append(symb);
                flag = true;
            }
            else {
                if(flag) {
                    String word = stb.toString();
                    JLabel label = new JLabel(word);
                    
                    label.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            label.setForeground(new Color(38,209,54));
                        }
                    });
                    JComboBox box = new JComboBox();
                    boolean addBox = false;
                    if(wordTags.containsKey(word)) {
                        if(wordTags.get(word).size() == 1) {
                            label.setToolTipText(wordTags.get(word).get(0) + " " +
                                    dbGlossary.getDesByTag(wordTags.get(word).get(0)));
                            label.setForeground(new Color(38,209,54));
                        }
                        else {
                            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            addBox = true;
                            // выбор
                            if(!wordTags.get(word).isEmpty()) {
                                for (String get : wordTags.get(word)) {
                                    box.addItem(get);
                                }
                                
                                box.setRenderer(new BasicComboBoxRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(JList list, Object value,
                                        int index, boolean isSelected, boolean cellHasFocus) {
                                        if (isSelected) {
                                            setBackground(Color.blue);
                                            setForeground(Color.WHITE);
                                            if (-1 < index) {
                                                list.setToolTipText(dbGlossary.getDesByTag(
                                                        wordTags.get(word).get(index)));
                                            }
                                        } else {
                                            setBackground(Color.WHITE);
                                            setForeground(Color.BLACK);
                                        }
                                        setFont(list.getFont());
                                        setText((value == null) ? "" : value.toString());
                                        return this;
                                    }
                                });
                                label.setForeground(Color.BLUE);
                            }
                            else {
                                for (String get : dbGlossary.getAllTags()) {
                                    box.addItem(get);
                                }
                                box.setRenderer(new BasicComboBoxRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(JList list, Object value,
                                        int index, boolean isSelected, boolean cellHasFocus) {
                                        if (isSelected) {
                                            setBackground(Color.blue);
                                            setForeground(Color.WHITE);
                                            if (-1 < index) {
                                                list.setToolTipText(dbGlossary.getDesByTag(
                                                        dbGlossary.getAllTags().get(index)));
                                            }
                                        } else {
                                            setBackground(Color.WHITE);
                                            setForeground(Color.BLACK);
                                        }
                                        setFont(list.getFont());
                                        setText((value == null) ? "" : value.toString());
                                        return this;
                                    }
                                });
                                label.setForeground(Color.RED);
                            }
                            content.add(box);
                        }
                    }
                    else if(wordTags.containsKey(word.toLowerCase().charAt(0) +
                            word.substring(1, word.length()))) {
                            
                        if(wordTags.get(word.toLowerCase().charAt(0) +
                            word.substring(1, word.length())).size() == 1) {
                            label.setToolTipText(wordTags.get(word.toLowerCase().charAt(0) +
                                word.substring(1, word.length())) + " " +
                                    dbGlossary.getDesByTag(wordTags.get(word.toLowerCase().charAt(0) +
                                        word.substring(1, word.length())).get(0)));
                            label.setForeground(new Color(38,209,54));
                        }
                        else {
                            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            addBox = true;
                            if(!wordTags.get(word).isEmpty()) {
                                
                                for (String get : wordTags.get(word)) {
                                    box.addItem(get);
                                }
                                
                                box.setRenderer(new BasicComboBoxRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(JList list, Object value,
                                        int index, boolean isSelected, boolean cellHasFocus) {
                                        if (isSelected) {
                                            setBackground(Color.blue);
                                            setForeground(Color.WHITE);
                                            if (-1 < index) {
                                                list.setToolTipText(dbGlossary.getDesByTag(
                                                        wordTags.get(word).get(index)));
                                            }
                                        } else {
                                            setBackground(Color.WHITE);
                                            setForeground(Color.BLACK);
                                        }
                                        setFont(list.getFont());
                                        setText((value == null) ? "" : value.toString());
                                        return this;
                                    }
                                });
                                
                                label.setToolTipText(box.getSelectedItem() + " " +
                                        dbGlossary.getDesByTag((String) box.getSelectedItem()));
                                label.setForeground(Color.BLUE);
                            }
                            else {
                                for (String get : dbGlossary.getAllTags()) {
                                    box.addItem(get);
                                }
                                box.setRenderer(new BasicComboBoxRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(JList list, Object value,
                                        int index, boolean isSelected, boolean cellHasFocus) {
                                        if (isSelected) {
                                            setBackground(Color.blue);
                                            setForeground(Color.WHITE);
                                            if (-1 < index) {
                                                list.setToolTipText(dbGlossary.getDesByTag(
                                                        dbGlossary.getAllTags().get(index)));
                                            }
                                        } else {
                                            setBackground(Color.WHITE);
                                            setForeground(Color.BLACK);
                                        }
                                        setFont(list.getFont());
                                        setText((value == null) ? "" : value.toString());
                                        return this;
                                    }
                                });
                                label.setToolTipText(box.getSelectedItem() + " " +
                                        dbGlossary.getDesByTag((String) box.getSelectedItem()));
                                label.setForeground(Color.RED);
                            }
                        }
                    }
                    
                    label.setFont(new java.awt.Font("Times New Roman", 0, 14));
                    
                    label.setSize(label.getPreferredSize().width + 2, 17);
                    if(countInRow + label.getPreferredSize().width + 2 > content.getWidth()) {
                        numRows++;
                        label.setLocation(0,numRows*17);
                        countInRow = label.getPreferredSize().width + 2;
                    }
                    else {
                        label.setLocation(countInRow,numRows*17);
                        countInRow += label.getPreferredSize().width + 2; 
                    }
                    
                    labels.add(label);
                    content.add(label);
                    
                    if(addBox) {
                        box.setSize(box.getPreferredSize().width, 20);
                        if(countInRow + box.getPreferredSize().width > content.getWidth()) {
                            numRows++;
                            box.setLocation(0,numRows*17 - 2);
                            countInRow = box.getPreferredSize().width;
                        }
                        else {
                            box.setLocation(countInRow,numRows*17 - 2);
                            countInRow += box.getPreferredSize().width; 
                        }
                        boxs.add(box);
                        content.add(box);
                    }
                    index++;
                    stb = new StringBuilder();
                }
                stb.append(symb);
                flag = false;
            }
        }
        
        pack();
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
       
        setResizable(false);
        
        setVisible(true);
    }

    
    private void getWordsAndCandTags(String text) {
        ParseWords pw = new ParseWords(file);
        HashMap<Pair<String, String>, Integer> map = pw.getWordsAndValues();
        ArrayList<String> words = new ArrayList();
        for (HashMap.Entry pair : map.entrySet()) {
            String word = ((Pair<String, String>) pair.getKey()).getKey();
            if(!words.contains(word)) {
                words.add(word);
            }
        }
        
        wordTags =  dbGlossary.getWordTags(words);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        content = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Send text");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        content.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jScrollPane1.setViewportView(content);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(260, 260, 260)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        
        Object[] options = { "Yes", "No" };
        int n = JOptionPane.showOptionDialog(null, "Do you want to annotate this text?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
            ArrayList<Pair<String, String>> arr = new ArrayList();
            int coun = 0;
            for(JLabel label: labels) {
                String word = label.getText();
                if(wordTags.containsKey(word)) {
                    if(wordTags.get(word).size() == 1) {
                        arr.add(new Pair(word, wordTags.get(word).get(0)));
                    }
                    else {
                        arr.add(new Pair(label.getText(), boxs.get(coun).getSelectedItem()));
                        coun++;
                    }
                }
                else if(wordTags.containsKey(word.toLowerCase().charAt(0) +
                                word.substring(1, word.length()))) {
                    if(wordTags.get(word.toLowerCase().charAt(0) +
                                word.substring(1, word.length())).size() == 1) {
                        arr.add(new Pair(word, wordTags.get(word.toLowerCase().charAt(0) +
                                word.substring(1, word.length())).get(0)));
                    }
                    else {
                        arr.add(new Pair(label.getText(), boxs.get(coun).getSelectedItem()));
                        coun++;
                    }
                }
                else {
                    arr.add(new Pair(label.getText(), boxs.get(coun).getSelectedItem()));
                    coun++;
                }
            }
            
            HashMap<Pair<String, String>, Integer> seq = new HashMap();
            for(int i = 0; i < arr.size() - 1; i++) {
                if(seq.containsKey(new Pair(arr.get(i).getValue(), arr.get(i+1).getValue()))) {
                    seq.replace(new Pair(arr.get(i).getValue(), arr.get(i+1).getValue()), 
                            seq.get(new Pair(arr.get(i).getValue(), arr.get(i+1).getValue())) + 1);
                }
                else {
                    seq.put(new Pair(arr.get(i).getValue(), arr.get(i+1).getValue()), 1);
                }
            }
            
            
            dbGlossary.insertInANN(arr);
            dbGlossary.addSeq(seq);
            
            HashMap<Pair<String, String>, Integer> map = new HashMap();
            for(Pair<String, String> stlk: arr) {
                String upWord = stlk.getKey().toUpperCase();
                String tag = stlk.getValue();
                if(map.equals(upWord)) {
                    if(map.containsKey(new Pair(stlk.getKey(), stlk.getValue())))
                    {
                        map.replace(new Pair(stlk.getKey(), tag), 
                                map.get(new Pair(stlk.getKey(), tag)) + 1);
                    }
                    else {
                        map.put(new Pair(stlk.getKey(), tag), 1);
                    }
                }
                else {
                    String maps = stlk.getKey().charAt(0) + stlk.getKey().substring(1, stlk.getKey().length())
                            .toLowerCase();
                    
                    if(map.containsKey(new Pair(upWord, tag)))
                    {
                        map.replace(new Pair(maps, tag), 
                                map.get(new Pair(upWord, tag)));
                        map.remove(new Pair(upWord, tag));
                    }
                    
                    char first = maps.charAt(0);
                    char upFirst = maps.toUpperCase().charAt(0);
                    String tWord = upFirst + maps.substring(1, maps.length());
                    
                    if(first != upFirst) {
                        if(map.containsKey(new Pair(tWord, tag)))
                        {
                            map.replace(new Pair(maps, tag), 
                                map.get(new Pair(tWord, tag)));
                            map.remove(new Pair(tWord, tag));
                        }
                    }
                    
                    if(map.containsKey(new Pair(maps, tag)))
                    {
                        map.replace(new Pair(maps, tag), 
                                map.get(new Pair(maps, tag)) + 1);
                    }
                    else {
                        map.put(new Pair(maps, tag), 1);
                    }
                }
            }
            int count;
            Iterator it = map.entrySet().iterator();;
            count = dbGlossary.getLastID();
            if(count > -1) {
                while (it.hasNext()) {
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
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Annotation of the text\nfinished successfull");
            
            this.dispose();
        }
    }//GEN-LAST:event_jButton1MouseClicked

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList content;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
