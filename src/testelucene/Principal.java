package testelucene;

import java.io.IOException;
import java.util.Scanner;

public class Principal {
    
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in, "latin1");
        System.out.println("Digite uma frase: ");
        String frase = sc.nextLine();
        Corretor corretor = new Corretor();
        corretor.getErrors(frase);
    }
}