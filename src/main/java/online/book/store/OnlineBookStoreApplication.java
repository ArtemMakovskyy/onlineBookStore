package online.book.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineBookStoreApplication {
    private static final Logger logger = LogManager.getLogger(OnlineBookStoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(online.book.store.OnlineBookStoreApplication.class, args);
        logger.info("API Documentation Overview: http://localhost:8088/api/swagger-ui/index.html#/");
    }
}
