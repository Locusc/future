package cn.locusc.spring.shell.demo;

import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;

@ShellComponent // @ShellComponent 扫描命令的类集
@ShellCommandGroup(value = "SpringShell Commands") // 不添加则默认为@ShellCommandGroup(value="驼峰类名以空格分隔")
public class ShellCommand {

    /**
     * ShellMethod 扫描命令,
     * Value值英文一般首字母大写,
     * 以"."结尾. 如使用中文方法注解,
     * 注意变更Linux环境的字符集,value为方法描述,key为命令行方法名称
     */
    @ShellMethod(value = "Add two nums together.", key = "sum")
    public int add(int a, int b){
        return a+b;
    }

    /**
     * 1.命令操作
     * 命令行参数可以按位置或者按照名称匹配;
     * 其中按位置如: echo 1 2 3 ;
     * 按名称默认为 --arg
     * 如: echo --a --b --c,亦可混合使用;
     *
     * shell:>echo a b paramC
     * You said a=a, b=b, c=paramC
     * shell:>echo --a a --b b --c paramC
     * You said a=a, b=b, c=paramC
     * shell:>echo a --b b paramC
     * You said a=a, b=b, c=paramC
     */
    @ShellMethod(value = "Display stuff.")
    public String echo(String a, String b, String c){
        return String.format("Param is a=%s ,b=%s, c=%s", a, b, c);
    }

    /**
     * 2.自定义参数命名键
     * 更改整个方法的默认前缀, use the prefix() attribute of the @ShellMethod annotation
     * 以每个参数的方式覆盖整个key , annotate the parameter with the @ShellOption annotation.
     *
     *  ShellMethod prefix 设置整个方法的默认前缀
     *  ShellOption 设置具体某个参数的名称导入方式.如: -c 或者 --third 名称匹配参数c;
     *
     * shell:>echo a b -c paramC
     * You said a=a, b=b, c=paramC
     * shell:>echo -a a -b b --third paramC
     * You said a=a, b=b, c=paramC
     */
    @ShellMethod(value = "Display stuff.", prefix = "-", key = "echo")
    public String echoCustomizing(String a, String b, @ShellOption(value = {"-c", "--third"}) String c) {
        return String.format("You said a=%s, b=%s, c=%s", a, b, c);
    }

    /**
     * 3.默认值
     * ShellOption对参数进行设置,defaultValue 默认值, help设置help提示(如 help query)
     * shell:>query
     * defaultAppName
     * shell:>help query
     *
     *
     * NAME
     *     query - query appName.
     *
     * SYNOPSYS
     *     query [[--app-name] string]
     *
     * OPTIONS
     *     --app-name  string
     *         应用名称
     *         [Optional, default = defaultAppName]
     */
    @ShellMethod("query appName.")
    public String query(@ShellOption(defaultValue = "defaultAppName", help = "应用名称") String appName){
        return appName;
    }

    /**
     * 4.参数相关性arity
     * ShellOption arity 参数相关性,参数类型为数组或集合,并指定预期的值;
     *
     * shell:>add-array 1 2 3
     * 6
     */
    @ShellMethod(value = "add Array nums.", key = {"add-array", "addArray"})
    public int addArray(@ShellOption(arity = 3) int[] nums) {
        return nums[0] + nums[1] + nums[2];
    }

    /**
     * 7.布尔类型参数的处理
     * boolean 默认为@ShellOption arity = 0,即默认为false, --arg 则为true;
     *
     * shell:>shutdown
     * The system terminate false
     * shell:>shutdown --force
     * The system terminate true
     */
    @ShellMethod(value = "Terminate the system.")
    public String shutdown(boolean force) {
        return "The system terminate " + force;
    }

    /**
     * boolean 亦可设置为@ShellOption arity = 1,defaultValu为false;
     * shell:>shutdownSystem
     * The system terminate false
     * shell:>shutdownSystem true
     * The system terminate true
     */
    @ShellMethod(value = "Terminate the system.", key = {"shutdown-system", "shutdownSystem"})
    public String shutdownSystem(@ShellOption(arity = 1, defaultValue = "false") boolean force) {
        return "The system terminate " + force;
    }

    /**
     * 默认使用空格分隔参数,包含特殊字符的参数,可使用单引号('')或者双引号("")来确定参数,转义使用反斜杠(\);
     * shell:>query hello
     * hello
     * shell:>query 'hello world'
     * hello world
     * shell:>query "hello world"
     * hello world
     * shell:>query 'I\'m here!'
     * I'm here!
     * shell:>query "He said \"Hi!\""
     * He said "Hi!"
     *
     * springshell支持TAB快捷操作,参数太长,可使用反斜杠("\")换行输入;
     */
    @ShellMethod("select appName.")
    public String select(@ShellOption(defaultValue = "defaultAppName", help = "应用名称") String appName){
        return appName;
    }

    /**
     * shell:>checklength a
     * The following constraints were not met:
     *     --username string : 个数必须在2和10之间 (You passed 'a')
     * shell:>checklength yaoming
     * the username is yaoming
     * shell:>checklength yaoming123456789
     * The following constraints were not met:
     *     --username string : 个数必须在2和10之间 (You passed 'yaoming123456789')
     */
    @ShellMethod("checkLength.") // javax.validation.constraints.Size
    public String checkLength(@Size(min = 2, max = 10) String username){
        return "the username is " + username;
    }

}
