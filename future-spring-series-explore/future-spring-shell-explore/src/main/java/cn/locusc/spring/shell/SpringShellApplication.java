package cn.locusc.spring.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

@SpringBootApplication
public class SpringShellApplication {

    public static void main(String[] args) {
        String[] disabledCommands = {
                "--spring.shell.command.exit.enabled=false",
                "--spring.shell.command.quit.enabled=false"
        };
        String[] fullArgs = StringUtils.concatenateStringArrays(args, disabledCommands);
        SpringApplication.run(SpringShellApplication.class, fullArgs);
    }

}
