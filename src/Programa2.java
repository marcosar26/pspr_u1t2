import java.awt.*;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class Programa2 {
    static Scanner sc = new Scanner(System.in);
    static Set<File> archivos = new HashSet<>();

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("MENU");
            System.out.println("1. Listar archivos a explorar");
            System.out.println("2. Añadir archivo");
            System.out.println("3. Eliminar archivo");
            System.out.println("4. Buscar número de apariciones de una cadena");
            System.out.println("5. Reemplazar cadena");
            System.out.println("6. Salir");
            System.out.print("Introduce una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> listarArchivos();
                case 2 -> addArchivo();
                case 3 -> eliminarArchivo();
                case 4 -> buscarCadena();
                case 5 -> reemplazarCadena();
                case 6 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción incorrecta");
            }
        } while (opcion != 6);
    }

    private static void reemplazarCadena() {
        Map<File, Integer> map = new HashMap<>();
        final File archivoLog = new File("." + "\\log_" + System.currentTimeMillis() + ".txt");
        System.out.print("Introduzca la cadena a buscar: ");
        String cadena = sc.next();
        System.out.print("Introduzca la cadena a reemplazar: ");
        String reemplazo = sc.next();
        for (File archivo : archivos) {
            int sustituciones = sustituirPalabra(archivo, cadena, reemplazo);
            map.putIfAbsent(archivo, sustituciones);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoLog))) {
            for (Map.Entry<File, Integer> entry : map.entrySet()) {
                bw.write("En el archivo " + entry.getKey().getName() + " se han sustituido " + entry.getValue() + " cadenas.");
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de log");

        }
        System.out.print("¿Desea abrir el archivo de log? (S/N): ");
        String respuesta = sc.next();
        if (respuesta.equalsIgnoreCase("S")) {
            try {
                Desktop.getDesktop().open(archivoLog);
            } catch (IOException e) {
                System.out.println("Error al abrir el archivo de log");
            }
        }
    }

    private static void buscarCadena() {
        Map<File, Integer> apariciones = new HashMap<>();
        System.out.print("Introduce la cadena a buscar: ");
        String cadena = sc.next();
        for (File archivo : archivos) {
            int ocurrencias = contarPalabras(archivo, cadena);
            apariciones.putIfAbsent(archivo, ocurrencias);
        }
        for (File archivo : archivos) {
            System.out.println("La cadena '" + cadena + "' aparece " + apariciones.get(archivo) + " veces en el archivo " + archivo.getName());
        }
    }

    private static void eliminarArchivo() {
        System.out.print("Introduce el nombre del archivo a eliminar: ");
        String nombre = sc.next();
        File archivo = new File(nombre);
        if (archivos.contains(archivo)) {
            archivos.remove(archivo);
            System.out.println("Archivo eliminado");
        } else {
            System.out.println("El archivo no existe");
        }
    }

    private static void addArchivo() {
        System.out.print("Introduce la ruta del archivo: ");
        File file = new File(sc.next());
        if (file.exists() && file.isFile()) {
            archivos.add(file);
        } else {
            System.out.println("El archivo no existe o no es un archivo editable");
        }
    }

    private static void listarArchivos() {
        if (archivos.size() == 0) {
            System.out.println("No hay archivos");
            return;
        }
        int numArchivo = 1;
        for (File archivo : archivos) {
            System.out.println(numArchivo + ". " + archivo.getName());
            numArchivo++;
        }
        System.out.print("¿Desea visualizar el contenido de alguno de los archivos? (S/N): ");
        String respuesta = sc.next();
        if (respuesta.equalsIgnoreCase("S")) {
            System.out.print("Introduce el número del archivo: ");
            int num = sc.nextInt();
            if (num > 0 && num <= archivos.size()) {
                File archivo = (File) archivos.toArray()[num - 1];
                try {
                    Scanner sc = new Scanner(archivo);
                    while (sc.hasNextLine()) {
                        System.out.println(sc.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("El archivo no existe");
                }
            } else {
                System.out.println("El número introducido no es válido");
            }
        }
    }

    public static int contarPalabras(File file, String palabra) {
        int apariciones = 0;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] palabras = linea.split("\s");
                for (String s : palabras) {
                    String p = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "");
                    if (p.isEmpty() || p.isBlank()) continue;
                    if (palabra.equalsIgnoreCase(p)) {
                        apariciones++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe");
        }
        return apariciones;
    }

    public static int sustituirPalabra(File file, String palabra, String palabraSustituir) {
        int sustituciones = 0;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String linea = br.readLine();
                String[] palabras = linea.split("\s");
                for (String s : palabras) {
                    String p = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^a-zA-Z]", "");
                    if (p.isEmpty() || p.isBlank()) continue;
                    if (p.equalsIgnoreCase(palabra)) {
                        sb.append(palabraSustituir);
                        sustituciones++;
                    } else {
                        sb.append(s);
                    }
                    sb.append("\s");
                }
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("El archivo no existe");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            file.delete();
            file.createNewFile();
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo");
        }
        return sustituciones;
    }
}
