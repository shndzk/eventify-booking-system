package app.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "telegram.bot.token=TELEGRAM_BOT_TOKEN",
        "telegram.bot.name=@TELEGRAM_BOT_NAME",
        "spring.datasource.url=jdbc:postgresql://localhost:5432/eventify_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=postgres",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.liquibase.enabled=false"
})
@ActiveProfiles("default")
class EventApplicationTests {

    @Test
    void contextLoads() {
    }
}
