/*
 *  Package: lingv.dbworking
 *  Classname: DBGlossaryWords
 *  Version: 1.0
 *  Developed and protected by Nikolay Mandrik
 */

package lingv.bdworking;

import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;
import javax.swing.JOptionPane;
import lingv.EditWord;


public final class DBGlossaryWords {
    public Connection connect;
    private Statement statment;
    private Integer[] ids = new Integer[numN];
    private Integer[] counts = new Integer[numN];
    private String[] words = new String[numN];
    private String[] tags = new String[numN];
    private String[] baseForms = new String[numN];
    private String querySQL;
    
    private static int numN = 100;
    
    private ResultSet result;
    
    public DBGlossaryWords() {
        System.setProperty("wordnet.database.dir", "WordNet-3.0/dict/");
        WordNetDatabase database = WordNetDatabase.getFileInstance();

        morphology = Morphology.getInstance();
        try {
            connect = DriverManager.getConnection("jdbc:derby://localhost:1527/Glossary", "Nick", "1235");
            statment = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            first();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
 
    public int getLastID() {
        querySQL = "SELECT * FROM TMP_WORDS";
        int countID = -1;
        try {
            result = statment.executeQuery(querySQL);
            result.last();
            countID = result.getRow();
            if(countID != 0) {
                countID = result.getInt("ID");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return countID;
    }
    
    public void deleteWords() {
        querySQL = "DELETE FROM TMP_WORDS";
        try {
            statment.executeUpdate(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void nextTen() {
        try {
            if(result.next()) {
                changeFields(0);
                for(int i = 1; i < numN; i++) {
                    if(!result.next()) {
                        for(int j = i; j < numN; j++) {
                            changeDefaultFields(j);
                        }
                        break;
                    }
                    else {
                        changeFields(i);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void previousTen() {
        try {
            int temp = 0;
            for(int i = 0; i < numN; i++) {
                if(result.previous()) {
                    temp ++;
                }
            }
            int max = temp;
            
            temp = 0;
            for(int i = 0; i < numN; i++) {
                if(result.previous()) {
                    temp ++;
                }
            }
            int countID = temp;
            
            if(countID == numN) {
                for(int i = 0; i < numN; i++) {
                    if(result.next()) {
                        changeFields(i);
                    }
                }
            } else if(countID != 0) {
                for(int i = 1; i < numN - countID; i++) {
                    ids[countID + i] = ids[i];
                    counts[countID + i] = counts[i];
                    words[countID + i] = words[i];
                    tags[countID + i] = tags[i];
                    baseForms[countID + i] = baseForms[i];
                }
                for(int i = 0; i <= countID; i++) {
                    if(result.next()) {
                        changeFields(i);
                    }
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void lastTen() {
        try {
            if(!result.last()) {
                for(int j = 0; j < numN; j++) {
                    changeDefaultFields(j);
                }
            }
            else {
                int amount = 0;
                for(int i = 0; i < numN - 1; i++) {
                    if(result.previous()) {
                       amount++; 
                    }
                    else {
                        break;
                    }
                }
                
                for(int i = amount + 1; i < numN; i++) {
                    changeDefaultFields(i);
                }
                result.last();
                changeFields(amount);
                amount--;
                for(int temp = amount; temp >= 0; temp--) {
                    result.previous();
                    changeFields(temp);
                }
                for(int temp = 0; temp <= amount; temp++) {
                    result.next();
                }
                
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void firstTen() {
        try {
            if(!result.first()) {
                for(int j = 0; j < numN; j++) {
                    changeDefaultFields(j);
                }
                JOptionPane.showMessageDialog(null, "Dictionary is free");
            }
            else {
                changeFields(0);
                for(int i = 1; i < numN; i++) {
                    if(!result.next()) {
                        for(int j = i; j < numN; j++) {
                            changeDefaultFields(j);
                        }
                        break;
                    }
                    else {
                        changeFields(i);
                    }
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void first() {
        try {
            querySQL = "SELECT * FROM TMP_WORDS";
            result = statment.executeQuery(querySQL);
            
            if(!result.first()) {
                for(int j = 0; j < numN; j++) {
                    changeDefaultFields(j);
                }
                JOptionPane.showMessageDialog(null, "Dictionary is free");
            }
            else {
                changeFields(0);
                for(int i = 1; i < numN; i++) {
                    if(!result.next()) {
                        for(int j = i; j < numN; j++) {
                            changeDefaultFields(j);
                        }
                        break;
                    }
                    else {
                        changeFields(i);
                    }
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void sortUpWords() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY LOWER(WORD)";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    
    public void sortDownWords() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY LOWER(WORD) DESC";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    
    public void sortUpAmount() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY AMOUNT";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    
    public void sortDownAmount() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY AMOUNT DESC";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
        
    public void sortUpBaseForm() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY BASEFORM";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    
    public void sortDownBaseForm() {
        querySQL = "SELECT * FROM TMP_WORDS ORDER BY BASEFORM DESC";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    
    public void select() {
        querySQL = "SELECT * FROM TMP_WORDS";
        try {
            result = statment.executeQuery(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        firstTen();
    }
    public boolean isUpdate = true;
    
    public void update(int ID, String word, int count, String tag, String baseForm) {
        try {
            String sql = "UPDATE TMP_WORDS SET AMOUNT = " + (count) + ", WORD = '" +
                    word + "', TAG = '" + tag + "', BASEFORM = '" + baseForm + "' WHERE ID = " + ID;
            statment.executeUpdate(sql);
            isUpdate = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void delete(int id) {
        String sql = "DELETE FROM TMP_WORDS WHERE ID=" + id;
        try {
            statment.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Deleting successfull");
            isUpdate = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Somthing wrong...\nOperation delete is not success.");
        }
    }
    
    private void changeFields(int index) throws SQLException {
        ids[index] = result.getInt("ID");
        counts[index] = result.getInt("AMOUNT");
        words[index] = result.getString("WORD");
        tags[index] = result.getString("TAG");
        baseForms[index] = result.getString("BASEFORM");
    }
    
    private void changeDefaultFields(int index) throws SQLException {
        ids[index] = -1;
        counts[index] = -1;
        words[index] = "";
        tags[index] = "";
        baseForms[index] = "";
    }

    public int insert(String key, Integer value, String tag, int count) {
        try {
            querySQL = "SELECT * FROM TMP_WORDS WHERE WORD='" + key + "' AND TAG='" + tag + "'";
            ResultSet res = statment.executeQuery(querySQL);
            res.next();
            int id = res.getInt("ID");
            int countWords = res.getInt("AMOUNT");
            String sql = "UPDATE TMP_WORDS SET AMOUNT = " + (countWords+value)
                    + " WHERE ID = " + id;
            statment.executeUpdate(sql);
        } catch (SQLException ex) {
            add(count + 1, key, tag, value);
            return count + 1;
        }
        return count;
    }
    
    private Morphology morphology;
    
    public void add(Integer index, String word, String tag, Integer amount) {
            if(index > Math.pow(10, 10) || index < 0) {
                return;
            }
            
            String[] candidate = null;
            if(EditWord.getNum(tag) >= 26 && EditWord.getNum(tag) <= 31) {
            String[] arr = morphology.getBaseFormCandidates(word, SynsetType.VERB);
                candidate = new String[arr.length];
                System.arraycopy(arr, 0, candidate, 0, arr.length);
            }
            if(EditWord.getNum(tag) > 5 && EditWord.getNum(tag) < 9) {
                String[] arr = morphology.getBaseFormCandidates(word, SynsetType.ADJECTIVE);
                candidate = new String[arr.length];
                System.arraycopy(arr, 0, candidate, 0, arr.length);
            }
            if(EditWord.getNum(tag) > 18 && EditWord.getNum(tag) < 22) {
                String[] arr = morphology.getBaseFormCandidates(word, SynsetType.ADVERB);
                candidate = new String[arr.length];
                System.arraycopy(arr, 0, candidate, 0, arr.length);
            }
            if(EditWord.getNum(tag) >= 11 && EditWord.getNum(tag) <= 14) {
                String[] arr = morphology.getBaseFormCandidates(word, SynsetType.NOUN);
                candidate = new String[arr.length];
                System.arraycopy(arr, 0, candidate, 0, arr.length);
            }
            if(EditWord.getNum(tag) == 1 || EditWord.getNum(tag) == 23 
                    || EditWord.getNum(tag) == 15) {
                candidate = new String[1];
                candidate[0] = word;
            }

            String cand = "";
            if(candidate != null) {
                if(candidate.length == 1) {
                    cand = candidate[0];
                }
            }
            System.out.println(index + " " + word + " " + amount + " " + tag + " " + cand);
            querySQL = "INSERT INTO TMP_WORDS VALUES(";
            querySQL += index;
            querySQL += ", '";
            querySQL += word;
            querySQL += "', ";
            querySQL += amount;
            querySQL += ", '";
            querySQL += tag;
            querySQL += "', '";
            querySQL += cand;
            //!!!
            querySQL += "'";
            querySQL += ")";
            try {
                statment.executeUpdate(querySQL);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                System.out.println(ex.getMessage());
            }
        }
    
    public HashMap<String, ArrayList<String>> getWordTags(ArrayList<String> words) {
        HashMap<String, ArrayList<String>> wordTags = new HashMap();
        for(String word: words) {
            try {
                int count  = 0;

                String querySQL = "SELECT * FROM TMP_WORDS WHERE WORD='" + word + "'";
                ResultSet res = statment.executeQuery(querySQL);
                wordTags.put(word, new ArrayList());
                
                boolean flag = false;
                while(res.next()) {
                    flag = true;
                    String tag = res.getString("TAG");
                    wordTags.get(word).add(tag);
                    count++;
                }
                
                if(!flag && word.charAt(0) == word.toUpperCase().charAt(0)) {
                    char first = word.toLowerCase().charAt(0);
                    String tWord = first + word.substring(1, word.length());
                
                    String query = "SELECT * FROM TMP_WORDS WHERE WORD='" + tWord + "'";
                    res = statment.executeQuery(query);
                    while(res.next()) {
                       
                        String tag = res.getString("TAG");
                        wordTags.get(word).add(tag);
                        count++;
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        return wordTags;
    }
    
    public ArrayList<String> getAllTags() {
        ArrayList arr = new ArrayList();
        try {
            String query = "SELECT * FROM TAGS";
            ResultSet res = statment.executeQuery(query);
            while(res.next()) {
                String tag = res.getString("TAG");
                arr.add(tag);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return arr;
    }
    
    public ArrayList<String> findWordByBaseForm(String baseForm) {
        ArrayList<String> arr = new ArrayList();
        try {
            String query = "SELECT * FROM TMP_WORDS WHERE BASEFORM='" + baseForm + "'";
            ResultSet res = statment.executeQuery(query);
            while(res.next()) {
                if(!arr.contains(res.getString("WORD"))) {
                    arr.add(res.getString("WORD"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return arr;
    }
    
    public String getDesByTag(String tag) {
        try {
            String query = "SELECT * FROM TAGS WHERE TAG='" + tag + "'";
            ResultSet res = statment.executeQuery(query);
            if(res.next()) {
                return res.getString("DESCRIP");
            }
            else { 
                return "none";
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return "none";
    }
    
    public void insertInANN(ArrayList<Pair<String, String>> wordTags) {
        try {
            querySQL = "SELECT * FROM ANN_WORDS";
            int countID = 1;
            try {
                result = statment.executeQuery(querySQL);
                result.last();
                countID = result.getRow();
                if(countID != 0) {
                    countID = result.getInt("ID");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            for(Pair<String, String> wordTag: wordTags) {
                String word = wordTag.getKey();
                String tag = wordTag.getValue();
                querySQL = "SELECT * FROM ANN_WORDS WHERE WORD='" + word + "' AND TAG='" + tag + "'";
                ResultSet res = statment.executeQuery(querySQL);
                if(res.next()) {
                    int id = res.getInt("ID");
                    int countWords = res.getInt("AMOUNT");
                    String sql = "UPDATE ANN_WORDS SET AMOUNT = " + (countWords+1)
                        + " WHERE ID = " + id;
                    statment.executeUpdate(sql);
                    String baseForm = "";
                    
                }
                else {
                    querySQL = "SELECT * FROM TMP_WORDS WHERE WORD='" + word + "' AND TAG='" + tag + "'";
                    try (ResultSet tmpRes = statment.executeQuery(querySQL)) {
                        String baseForm = "";
                        if(tmpRes.next()) {
                            baseForm = tmpRes.getString("BASEFORM");
                        }
                        querySQL = "INSERT INTO ANN_WORDS VALUES(";
                        querySQL += countID;
                        querySQL += ", '";
                        querySQL += word;
                        querySQL += "', ";
                        querySQL += 1;
                        querySQL += ", '";
                        querySQL += tag;
                        querySQL += "', '";
                        querySQL += baseForm;
                        querySQL += "')";
                        countID++;
                        statment.executeUpdate(querySQL);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }
    
    public void deleteFromANN() {
        querySQL = "DELETE FROM ANN_WORDS";
        try {
            statment.executeUpdate(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public int getCountRowsANN() {
        querySQL = "SELECT * FROM ANN_WORDS";
        try {
            ResultSet res = statment.executeQuery(querySQL);
            res.last();
            return res.getRow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return -1;
    }
    
    public Object[][] getRowsNN() {
        Object[][] model = new Object[getCountRowsANN()][4];
        querySQL = "SELECT * FROM ANN_WORDS";
        try {
            ResultSet res = statment.executeQuery(querySQL);
            for(int i = 0; res.next(); i++) {
                model[i][0] = res.getString("WORD");
                model[i][1] = res.getString("TAG");
                model[i][2] = res.getInt("AMOUNT");
                model[i][3] = res.getString("BASEFORM");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return model;
    }
    
    
    public void addSeq(HashMap<Pair<String, String>, Integer> seq) {
        try {
            querySQL = "SELECT * FROM SEQTAG";
            int countID = 1;
            try {
                result = statment.executeQuery(querySQL);
                result.last();
                countID = result.getRow();
                if(countID != 0) {
                    countID = result.getInt("ID");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            for(HashMap.Entry entry : seq.entrySet()) {
            
                String tag1 = (((Pair<String, String>) entry.getKey()).getKey());
                String tag2 = ((Pair<String, String>) entry.getKey()).getValue();
                Integer val = (Integer) entry.getValue();
                
                querySQL = "SELECT * FROM SEQTAG WHERE FIR='" + tag1 + "' AND SEC='" + tag2 + "'";
                ResultSet res = statment.executeQuery(querySQL);
                if(res.next()) {
                    int id = res.getInt("ID");
                    int countWords = res.getInt("COUNT");
                    String sql = "UPDATE SEQTAG SET COUNT = " + (countWords+1)
                        + " WHERE ID = " + id;
                    statment.executeUpdate(sql);
                    String baseForm = "";
                    
                }
                else {
                    querySQL = "INSERT INTO SEQTAG VALUES(";
                    querySQL += countID;
                    querySQL += ", '";
                    querySQL += tag1;
                    querySQL += "', '";
                    querySQL += tag2;
                    querySQL += "', ";
                    querySQL += val;
                    querySQL += ")";
                    countID++;
                    statment.executeUpdate(querySQL);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }
    
    
    public void deleteFromSEQ() {
        querySQL = "DELETE FROM SEQTAG";
        try {
            statment.executeUpdate(querySQL);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public int getCountRowsSEQ() {
        querySQL = "SELECT * FROM SEQTAG";
        try {
            ResultSet res = statment.executeQuery(querySQL);
            res.last();
            return res.getRow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return -1;
    }
    
    public Object[][] getRowsSEQ() {
        Object[][] model = new Object[getCountRowsSEQ()][3];
        querySQL = "SELECT * FROM SEQTAG";
        try {
            ResultSet res = statment.executeQuery(querySQL);
            for(int i = 0; res.next(); i++) {
                model[i][0] = res.getString("FIR");
                model[i][1] = res.getString("SEC");
                model[i][2] = res.getInt("COUNT");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return model;
    }
    
    
    /**
     * @return the statment
     */
    public Statement getLastStatment() {
        return statment;
    }

    /**
     * @return numN id
     */
    public Integer[] getIDs() {
        return ids;
    }
    
    /**
     * @return numN id
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @return numN counts
     */
    public Integer[] getCounts() {
        return counts;
    }

    /**
     * @return numN words
     */
    public String[] getWords() {
        return words;
    }

    /**
     * @return the querySQL
     */
    public String getLastQuerySQL() {
        return querySQL;
    }
    
    /**
     * @return the connect
     */
    public Connection getConnect() {
        return connect;
    }
    
    /**
     * @return the base forms of words
     */
    public String[] getBaseForms() {
        return baseForms;
    }
}