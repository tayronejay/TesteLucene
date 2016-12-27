package correcaoortografica;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BrazilianPortuguese;
import org.languagetool.rules.RuleMatch;

public class Corretor {

    public List<String> getErrors(String text) throws IOException {
        String[] suggestions;
        ArrayList<String> finalSuggestions = new ArrayList();
        JLanguageTool langTool = new JLanguageTool(new BrazilianPortuguese());
        List<RuleMatch> matches = langTool.check(text);        
        for(RuleMatch match : matches) {
            if(match.getRule().getId().equals("HUNSPELL_NO_SUGGEST_RULE")){
                try {
                    suggestions = getSuggests(text.substring(match.getFromPos(), match.getToPos()));
                    finalSuggestions.addAll(Arrays.asList(suggestions));
                } catch (Exception ex) {
                    Logger.getLogger(Corretor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return finalSuggestions;
    }
    
    private String[] getSuggests(String wordForSuggestions) throws Exception {
        String path = pathDaClasse(Corretor.class);
        File dir = new File(path);
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellChecker = new SpellChecker(directory);
        
        spellChecker.indexDictionary(new PlainTextDictionary(new File(System.getProperty("user.dir") + "/pt-BR.dic").toPath()),
                new IndexWriterConfig(new StandardAnalyzer()), false);
        
        int suggestionsNumber = 2;
        String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
        return suggestions;
    }
    
    private String pathDaClasse(Class klass) throws Exception {
        String className = "/" + klass.getName().replace('.', '/') + ".class";
        URL classURL = klass.getResource(className);
        String path = URLDecoder.decode(classURL.toString(), "UTF-8");
        if (path.startsWith("jar:file:/")) {
            int pos = path.indexOf(".jar!/");
            if (pos != -1) {
                if (File.separator.equals("\\"))
                    path = path.substring("jar:file:/".length(), pos + ".jar".length());
                else
                    path = path.substring("jar:file:".length(), pos + ".jar".length());
                path = path.replaceAll("%20", " ");
            } else
                path = "?";
        } else if (path.startsWith("file:/")) {
            if (File.separator.equals ("\\"))
                path = path.substring("file:/".length());
            else
                path = path.substring("file:".length());
            path = path.substring(0, path.lastIndexOf(className)).replaceAll("%20", " ");
        } else
            path = "?";
        return path + "/" + klass.getPackage().getName();
    }
}