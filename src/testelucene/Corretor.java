package testelucene;

import java.io.File;
import java.io.IOException;
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

    public void getErrors(String text) throws IOException {        
        JLanguageTool langTool = new JLanguageTool(new BrazilianPortuguese());
        List<RuleMatch> matches = langTool.check(text);        
        for(RuleMatch match : matches) {
            System.out.println(match.getMessage() + ": \"" +
                    text.substring(match.getFromPos(), match.getToPos()) + "\"");
            System.out.println("Sugestões de correção(ões):");
            try {
                String[] suggestions = getSuggests(text.substring(match.getFromPos(), match.getToPos()));
                for(String suggestion: suggestions)
                    System.out.println(suggestion);
            } catch (Exception ex) {
                Logger.getLogger(Corretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String[] getSuggests(String wordForSuggestions) throws Exception {
        File dir = new File("C:/Users/ATENDIMENTO 13/Desktop/Sugestões Correção/Arquivos Lucene");
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellChecker = new SpellChecker(directory);
        
        spellChecker.indexDictionary(new PlainTextDictionary(new File("C:/Users/ATENDIMENTO 13/Desktop/Sugestões Correção/Dicionário/pt-BR.dic").toPath()),
                new IndexWriterConfig(new StandardAnalyzer()), false);
        
        int suggestionsNumber = 5;
        String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
        return suggestions;
    }
}