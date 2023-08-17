package online.book.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineBookStoreApplication {
    private static final String SWAGGER_LINK = "http://localhost:8080/swagger-ui/index.html";

    public static void main(String[] args) {
        SpringApplication.run(online.book.store.OnlineBookStoreApplication.class, args);
        System.out.println("API Documentation Overview - " + SWAGGER_LINK);
    }
}
