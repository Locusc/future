package cn.locusc.spring.shell.demo;

import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

/**
 * 自定义实现内置命令
 */
@ShellComponent
public class ExitCommand implements Quit.Command {

    public ExitCommand() {
    }

    @ShellMethod(
            value = "Exit the shell.",
            key = {"quit", "exit"}, group = "SpringShellDemo Commands"
    )
    public void quit() {
        throw new ExitRequest();
    }

    public interface Command { }

}
