import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("¡Hola! Ingresa tu nombre: ");
        String nombre = scanner.nextLine();

        try{
            FileWriter archivo = new FileWriter("nombre.txt");
            PrintWriter texto = new PrintWriter(archivo);

            texto.println(nombre);
            texto.close();

            System.out.println("El archivo ha sido generado y guardado :)");
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
        }
        scanner.close();
        }
    }
