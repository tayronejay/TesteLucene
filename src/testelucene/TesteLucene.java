package testelucene;

import java.io.File;
import java.util.Scanner;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TesteLucene {
    
    public static void main(String[] args) throws Exception {
        
        File dir = new File("C:/Users/ATENDIMENTO 13/Desktop/Sugestões Correção/Arquivos Lucene");
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellChecker = new SpellChecker(directory);
        
        spellChecker.indexDictionary(new PlainTextDictionary(new File("C:/Users/ATENDIMENTO 13/Desktop/Sugestões Correção/Dicionário/pt-BR.dic").toPath()),
                new IndexWriterConfig(new StandardAnalyzer()), false);
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite uma palavra: ");
        String wordForSuggestions = sc.nextLine();
        do{
            int suggestionsNumber = 5;
            String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);
            if(suggestions != null && suggestions.length > 0)
                for(String word : suggestions)
                    System.out.println("Você quis dizer: " + word);
            else
                System.out.println("Nenhuma sugestão encontrada para a palavra: " + wordForSuggestions);
            System.out.print("Digite outra palavra: ");
            wordForSuggestions = sc.nextLine();
        }while(!wordForSuggestions.equals("-1"));
    }
}