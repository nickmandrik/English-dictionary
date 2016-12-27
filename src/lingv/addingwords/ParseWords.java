/*
 *  Package: lingv.addingwords
 *  Classname: ParseWords
 *  Version: 1.0
 *  Developed and protected by Nikolay Mandrik
 */

package lingv.addingwords;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import javax.swing.JOptionPane;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ParseWords {
    private HashMap<Pair<String, String>, Integer> words;
    
    public ParseWords(File file) {
        String text = "";
        try {
            text += new String(Files.readAllBytes(file.toPath()));
            words = new HashMap();
            parse(text);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Need to access the File " + file.getPath());
        }
    }
    
    
    public HashMap<Pair<String, String>, Integer> getWordsAndValues() {
        return words;
    }
    
    
    private void parse(String text) throws FileNotFoundException, IOException {
        // 1
        InputStream iES = new FileInputStream("en-sent.bin");
	SentenceModel modelSent = new SentenceModel(iES);
	SentenceDetectorME sdetector = new SentenceDetectorME(modelSent);
 
	String sentences[] = sdetector.sentDetect(text);
        
        // 2
        InputStream iET = new FileInputStream("en-token.bin");
	TokenizerModel modelWord = new TokenizerModel(iET);
	Tokenizer tokenizer = new TokenizerME(modelWord);
        
        // 3
        POSModel modelTag = new POSModelLoader()	
		.load(new File("en-pos-maxent.bin"));
	POSTaggerME tagger = new POSTaggerME(modelTag);
        
        for(String sent: sentences ) {
            String[] wordsBySent = tokenizer.tokenize(sent);
            String[] tags = tagger.tag(wordsBySent);
            for(int i = 0; i < tags.length; i++) {
                String word = wordsBySent[i];
                String tag = tags[i];
                String upWord = word.toUpperCase();
                if(word.equals(upWord)) {
                    if(words.containsKey(new Pair(word, tag)))
                    {
                        words.replace(new Pair(word, tag), 
                                words.get(new Pair(word, tag)) + 1);
                    }
                    else {
                        words.put(new Pair(word, tag), 1);
                    }
                }
                else {
                    word = word.charAt(0) + word.substring(1, word.length()).toLowerCase();
                    
                    if(words.containsKey(new Pair(upWord, tag)))
                    {
                        words.replace(new Pair(word, tag), 
                                words.get(new Pair(upWord, tag)));
                        words.remove(new Pair(upWord, tag));
                    }
                    
                    char first = word.charAt(0);
                    char upFirst = word.toUpperCase().charAt(0);
                    String tWord = upFirst + word.substring(1, word.length());
                    
                    if(first != upFirst) {
                        if(words.containsKey(new Pair(tWord, tag)))
                        {
                            words.replace(new Pair(word, tag), 
                                words.get(new Pair(tWord, tag)));
                            words.remove(new Pair(tWord, tag));
                        }
                    }
                    
                    if(words.containsKey(new Pair(word, tag)))
                    {
                        words.replace(new Pair(word, tag), 
                                words.get(new Pair(word, tag)) + 1);
                    }
                    else {
                        words.put(new Pair(word, tag), 1);
                    }
                }
            }
        }
    }
}
