import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce la ruta del archivo: ");
        File file = new File(sc.nextLine());
        System.out.print("Introduce la palabra a buscar: ");
        String palabra = sc.nextLine();

        System.out.println("La palabra " + palabra + " aparece " + contarPalabras(file, palabra) + " veces.");

        System.out.print("Introduce una palabra que sustituirá a la otra: ");
        String palabraSustituir = sc.nextLine();
        sustituirPalabra(file, palabra, palabraSustituir);
    }

    public static void sustituirPalabra(File file, String palabra, String palabraSustituir) {
        StringBuilder sb = new StringBuilder();
        File nuevoArchivo = new File(file.getParent() + "\\NUEVO_" + file.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String linea = br.readLine();
                String[] palabras = linea.split("\s");
                for (String s : palabras) {
                    if (s.equalsIgnoreCase(palabra)) {
                        sb.append(palabraSustituir);
                    } else {
                        sb.append(s);
                    }
                    sb.append("\s");
                }
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nuevoArchivo))) {
            if (nuevoArchivo.delete() && nuevoArchivo.createNewFile()) System.gc();
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int contarPalabras(File file, String palabra) {
        int contador = 0;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] palabras = linea.split("\s");
                for (String p : palabras) {
                    if (p.equalsIgnoreCase(palabra)) {
                        contador++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return contador;
    }
}