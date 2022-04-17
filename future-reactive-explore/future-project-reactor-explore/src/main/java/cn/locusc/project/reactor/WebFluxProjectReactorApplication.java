package cn.locusc.project.reactor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@EnableJpaRepositories
@RequiredArgsConstructor
@SpringBootApplication
public class WebFluxProjectReactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxProjectReactorApplication.class, args);
    }

}
