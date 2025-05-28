import java.net.URL;
import java.nio.file.Paths;

public class ResourceChecker {

    public static String getResourceAbsolutePath(String resourcePath) {
        // resourcePath ejemplo: "/fonts/kenvector_future.ttf"
        URL resourceUrl = ResourceChecker.class.getResource(resourcePath);
        if (resourceUrl == null) {
            System.out.println("Recurso NO encontrado: " + resourcePath);
            return null;
        }
        try {
            // Convertir URL a Path absoluto
            return Paths.get(resourceUrl.toURI()).toAbsolutePath().toString();
        } catch (Exception e) {
            System.out.println("Error al obtener ruta absoluta: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String ruta = getResourceAbsolutePath("/fonts/kenvector_future.ttf");
        if (ruta != null) {
            System.out.println("Recurso encontrado en: " + ruta);
        } else {
            System.out.println("No se encontró el recurso.");
        }
    }
}
