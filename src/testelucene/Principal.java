package testelucene;

import java.io.IOException;
import java.util.Scanner;

public class Principal {
    
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in, "latin1");
        String frase;
        System.out.println("Digite uma frase: ");
        frase = sc.nextLine();
        //System.out.println(frase);
        Corretor corretor = new Corretor();
        corretor.getErrors(frase);
    }
}