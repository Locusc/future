package cn.locusc.spring.shell.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Jay
 * 自定义内置命令提示符(ResultHandlers)
 * 2022/10/18
 */
@Slf4j
@Component
public class CustomPromptProvider implements PromptProvider {

    @SneakyThrows
    @Override
    public AttributedString getPrompt() {
        // 获取主机名称
        String hostName = getHostName();
        // 设置命令提示符文字
        String prompt = "SpringShell@" + hostName + "> ";
        // 设置命令提示符字体样式
        AttributedStyle promptStyle = AttributedStyle.BOLD.foreground(AttributedStyle.GREEN);
        // 返回命令提示符
        return new AttributedString(prompt, promptStyle);
    }

    private String getHostName() throws UnknownHostException {
        String hostName = "";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            log.info("获取主机名称 UnknownHostException: {}", e.getMessage());
            throw e;
        }
        return hostName;
    }

}
