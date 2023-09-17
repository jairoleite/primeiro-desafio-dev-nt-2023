import io.gatling.core.Predef.*;
import io.gatling.http.Predef.*;
import scala.concurrent.duration.*;
import scala.util.Random;

public class GatlingSimulation extends Simulation {

    private final String baseUrl = "http://localhost:9999";
    private final String userAgent = "Agente do Caos";

    private HttpProtocolBuilder httpProtocol = http()
        .baseUrl(baseUrl)
        .userAgentHeader(userAgent);

    private ScenarioBuilder criacaoEConsultaPessoas = scenario("Criação E Talvez Consulta de Pessoas")
        .feed(tsv("./fonte-dados/pessoas-payloads.tsv").circular())
        .exec(http("criação")
            .post("/pessoas").body(StringBody("${payload}"))
            .header("content-type", "application/json")
            // 201 pros casos de sucesso :)
            // 422 pra requests inválidos :|
            // 400 pra requests bosta tipo data errada, tipos errados, etc. :(
            .check(status.in(201, 422, 400))
            // Se a criacao foi na api1 e esse location request atingir api2, a api2 tem que encontrar o registro.
            // Pode ser que o request atinja a mesma instancia, mas estatisticamente, pelo menos um request vai atingir a outra.
            // Isso garante o teste de consistencia de dados
            .check(status().saveAs("httpStatus"))
            .checkIf(session -> session("httpStatus").as("String").equals("201")) {
                header("Location").saveAs("location");
            })
        .pause(Duration.ofMillis(1), Duration.ofMillis(30))
        .doIf(session -> session.contains("location")) {
            exec(http("consulta")
                .get("${location}"));
        };

    private ScenarioBuilder buscaPessoas = scenario("Busca Válida de Pessoas")
        .feed(tsv("./fonte-dados/termos-busca.tsv").circular())
        .exec(http("busca válida")
            .get("/pessoas?t=${t}")
        );

    private ScenarioBuilder buscaInvalidaPessoas = scenario("Busca Inválida de Pessoas")
        .exec(http("busca inválida")
            .get("/pessoas")
            .check(status().is(400))
        );

    public GatlingSimulation() {
        setUp(
            criacaoEConsultaPessoas.inject(
                constantUsersPerSec(2).during(Duration.ofSeconds(10)), // warm up
                constantUsersPerSec(5).during(Duration.ofSeconds(15)).randomized(), // are you ready?
                rampUsersPerSec(6).to(600).during(Duration.ofMinutes(3)) // lezzz go!!!
            ),
            buscaPessoas.inject(
                constantUsersPerSec(2).during(Duration.ofSeconds(25)), // warm up
                rampUsersPerSec(6).to(50).during(Duration.ofMinutes(3)) // lezzz go!!!
            ),
            buscaInvalidaPessoas.inject(
                constantUsersPerSec(2).during(Duration.ofSeconds(25)), // warm up
                rampUsersPerSec(6).to(20).during(Duration.ofMinutes(3)) // lezzz go!!!
            )
        ).protocols(httpProtocol);
    }
}
