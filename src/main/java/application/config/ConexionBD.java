package application.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Administra el acceso a la base de datos con JDBC.
 *
 * Se usa el patron Singleton para leer la configuracion una sola vez, pero
 * entrega una conexion nueva cada vez que se la piden. De esa forma, si varias
 * personas usan la aplicacion al mismo tiempo, cada operacion trabaja con su
 * propia conexion y no se interfieren entre ellas.
 */
public class ConexionBD {

    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    // el constructor es privado para que nadie pueda hacer new ConexionBD()
    private ConexionBD() {
        Properties propiedades = new Properties();

        try (InputStream archivo = ConexionBD.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (archivo == null) {
                throw new IllegalStateException("No se encontro el archivo application.properties");
            }
            propiedades.load(archivo);

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la configuracion de la base de datos", e);
        }

        this.url = propiedades.getProperty("db.url");
        this.usuario = propiedades.getProperty("db.usuario");
        this.clave = propiedades.getProperty("db.clave");
    }

    // synchronized para que dos peticiones al tiempo no creen dos instancias
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Entrega una conexion nueva, abierta y lista para usar.
     * Quien la pide es responsable de cerrarla; lo mas seguro es pedirla
     * dentro de un try-with-resources para que se cierre sola.
     */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }
}
