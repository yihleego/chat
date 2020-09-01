package io.leego.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Yihleego
 */
@SpringBootApplication
public class ChatClientApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatClientApplication.class);
        application.setApplicationContextClass(AnnotationConfigApplicationContext.class);
        application.run(args);
    }

}
