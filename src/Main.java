import java.io.*;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static Map<String, Integer> map = new LinkedHashMap<>();

    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println("Usage: java -jar archivo.jar [ruta_archivo] [palabra_buscar] [palabra_reemplazar]");
            System.exit(1);
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce la ruta del archivo: ");
        File file;
        if (args.length >= 1) {
            file = new File(args[0]);
        } else {
            file = new File(sc.nextLine());
        }
        contarPalabrasRepetidas(file);
        System.out.print("Introduce la palabra a buscar: ");
        String palabra;
        if (args.length >= 2) {
            palabra = args[1];
        } else {
            palabra = sc.nextLine();
        }
        int ocurrencias = contarPalabras(palabra);
        System.out.println("La palabra '" + palabra + "' aparece " + ocurrencias + " veces.");

        System.out.print("Introduce una palabra que sustituirá a la otra: ");
        String palabraSustituir;
        if (args.length == 3) {
            palabraSustituir = args[2];
        } else {
            palabraSustituir = sc.nextLine();
        }

        if (ocurrencias > 0) {
            sustituirPalabra(file, palabra, palabraSustituir);
        }
    }

    public static void sustituirPalabra(File file, String palabra, String palabraSustituir) {
        StringBuilder sb = new StringBuilder();
        File nuevoArchivo = new File(file.getParent() + "\\NUEVO_" + file.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String linea = br.readLine();
                String[] palabras = linea.split("\s");
                for (String s : palabras) {
                    String p = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "");
                    if (p.equalsIgnoreCase(palabra)) {
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

    public static int contarPalabras(String palabra) {
        if (!map.isEmpty()) {
            return map.get(palabra);
        } else {
            return 0;
        }
    }

    public static void contarPalabrasRepetidas(File file) {
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] palabras = linea.split("\s");
                for (String s : palabras) {
                    String p = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "");
                    if (map.containsKey(p)) {
                        map.put(p, map.get(p) + 1);
                    } else {
                        map.put(p, 1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Stream<Map.Entry<String, Integer>> ordenadosPorValor = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Integer::intValue)));
        map = ordenadosPorValor.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        map.forEach((k, v) -> System.out.println("'" + k + "'" + " aparece " + v + " veces."));
    }
}