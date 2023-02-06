package io.leego.chat;

import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Leego Yih
 */
@SpringBootApplication
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatServerApplication.class);
        application.setApplicationContextFactory(ApplicationContextFactory.ofContextClass(AnnotationConfigApplicationContext.class));
        application.run(args);
    }

}
