package cn.locusc.spring.shell.demo;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

/**
 * springshell允许判断命令是否可用,并优雅提示;
 * ShellMethodAvailability(value  = "") 扫描动作命令;
 *
 * shell:>download
 * Command 'download' exists but is not currently available because you are not connected
 * Details of the error have been omitted. You can use the stacktrace command to print the full stacktrace.
 * shell:>disconnect
 * No command found for 'disconnect'
 * Details of the error have been omitted. You can use the stacktrace command to print the full stacktrace.
 * shell:>connect admin admin
 * shell:>download
 */
@ShellComponent
public class DynamicCommand {

    private boolean connected;

    @ShellMethod("Connect to the server.")
    public void connect(String user, String password) {
        // ..
        connected = true;
    }

    @ShellMethod("Download the nuclear codes.")
    public void download() {
        // ..
    }

    @ShellMethodAvailability({"download", "disconnect"})
    public Availability downloadAvailability() {
        return connected
                ? Availability.available()
                : Availability.unavailable("you are not connected");
    }

}
