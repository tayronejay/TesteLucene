package testelucene;

import java.io.File;
import java.io.IOException;
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
        File dir = new File(System.getProperty("user.dir"));
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellChecker = new SpellChecker(directory);
        
        spellChecker.indexDictionary(new PlainTextDictionary(new File(System.getProperty("user.dir") + "/pt-BR.dic").toPath()),
                new IndexWriterConfig(new StandardAnalyzer()), false);
        
        int suggestionsNumber = 2;
        String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
        return suggestions;
    }
}