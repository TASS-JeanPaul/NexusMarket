package application.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Maneja la conexion con PostgreSQL usando JDBC.
 * Se usa el patron Singleton para que exista una sola conexion en todo el programa.
 */
public class ConexionBD {

    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    private Connection conexion;

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

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Devuelve la conexion abierta. Si nunca se abrio, o si se cerro o se cayo,
     * la vuelve a abrir. Asi la conexion se mantiene estable.
     */
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, clave);
        }
        return conexion;
    }

    public void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                // si ya estaba cerrada no hay nada que hacer
            }
            conexion = null;
        }
    }
}
