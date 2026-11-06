package application.dialog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI nexusMarketOpenApi() {
        Info info = new Info()
                .title("NexusMarket API")
                .version("0.0.1")
                .description("Marketplace construido como sistema de soporte a la decision. "
                        + "Las secciones estan numeradas en el orden del flujo de negocio: "
                        + "se registran los participantes, se publica el catalogo, se cargan existencias, "
                        + "se arma el carrito, se hace el checkout, se paga, se despacha y, "
                        + "si hace falta, se devuelve y se reembolsa.");

        return new OpenAPI().info(info);
    }
}
