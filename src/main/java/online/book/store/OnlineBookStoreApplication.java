package online.book.store;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@RequiredArgsConstructor
public class OnlineBookStoreApplication {
    private static final Logger logger = LogManager.getLogger(OnlineBookStoreApplication.class);

    //    private final BookRepository bookRepository;
    //    private final UserRepository userRepository;
    //    private final ShoppingCartRepository shoppingCartRepository;

    public static void main(String[] args) {
        SpringApplication.run(online.book.store.OnlineBookStoreApplication.class, args);
        logger.info("API Documentation Overview: http://localhost:8080/api/swagger-ui/index.html#/");
    }

    @Component
    public class ApplicationRunnerImpl implements ApplicationRunner {
        @Override
        public void run(ApplicationArguments args) throws Exception {

        }
    }
}
