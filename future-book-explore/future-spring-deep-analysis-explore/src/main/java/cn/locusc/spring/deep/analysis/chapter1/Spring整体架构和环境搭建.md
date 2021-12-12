#### 1.1 spring的整体架构
pojo Plain Old Java Object 普通的java类型 只包含一些属性和getter和setter方法没有业务逻辑
ejb Enterprise Java Beans 可复用的组件
#### 1.2 环境搭建

#### 1.3 cglib obienesis 的编译错误解决
没有遇到文中缺少cglib obienesis包的问题,
查看spring-core/spring-core.gradle
文件发现这个两个包已经在构建时被导入

#### 1.4 Aspect j编译问题解决
##### 1.4.1 问题发现
备注: gradle正常导入和构建，没有遇到文中的编译错误
暂时不知道文中的编译指的是gradle初始化构建还是其他地方执行的编译

aspect关键字java语法违背

##### 1.4.2 原因
1. 脱离了spring, aspectj的使用方法
```aspectj
public aspect TxAspect {

    void around():call(void sayHello()) {
        System.out.println("Transaction Begin");
        proceed();
        System.out.println("Transaction End");
    }

}
```
```java
package org.springframework;

public class HelloWorld {

	public void sayHello () {
		System.out.println("Hello AspectJ !");
	}

	public static void main(String[] args) {
		HelloWorld h = new HelloWorld();
		h.sayHello();
	}

}
```
2. aspect关键字不是java的关键字, 而是只有aspectj才认识的关键字

3. void around中的内容同样不是方法, 只是指定程序执行HellWorld对象的sayHello
方法时, 执行这个代码块, 其中proceed表示调用原来的sayHello方法

4. Java无法识别TxAspect.java文件中的内容,所以我们需要使用ajc.exe来执行编译：
ajc HelloWorld.java TxAspect.java(这里没有做命令编译测试, 并且发现idea2020.1版本
不需要切换编译模式也可以直接进行编译, 应该是项目有默认的编译方式)

5. 我们可以把ajc命令理解为javac命令, 两者都用于编译Java程序, 区别是ajc命令可以识
AspectJ的语法, 从这个角度看, 我们可以将ajc命令当成增强版的javac命令(实际测试中将TxAspect
.aj文件改为.java文件确实可以编译成功, 应该是项目默认的编译方式就是ajc-在spring-aspects.gradle
中发现了ajc("org.aspectj:aspectjtools:1.9.2")依赖)

##### 1.4.3 问题解决
1. 下载AspectJ的最新稳定版本
2. AspectJ安装
3. IDEA对Ajc支持官方文档(使用AspectJ编译器)
idea ultimate版本中原生支持该功能

默认情况idea使用javac编译, 项目级别指定的ajc设置可以在各个模块进行微调,
Ajc与Javac结合使用可以优化编译性能, idea可把二者组合起来, 而无须在IDE中切换编译器,
该选择Ajc为项目编译器,同时使用Javac,打开idea compiler设置中的"Delegate to Javac"选项,
没有aspect的模块将被编译为Javac(通常更快), 并且, 包含aspect的模块将用Ajc编译如果
此选项off,Ajc用于项目中的所有模块

对于只包含@Aspect注解的Java类(在.java文件中)的形式的模块, 您可以指定Ajc仅应用于后
编译的编织(weaving), 如果这样做, 则Javac将用于编译所有源文件, 然后Ajc其应用于编译的类文件进行编织
整个过程(编译＋编织)(compilation + weaving)将花费更少的时间


如果打开了"Javac代理选项Delegate to Javac", 则通过在与模块关联的AspectJ Facets 
中打开相应的选项来启用Ajc编译后编织模式, 不应该为包含代码样式aspect的模块(在.aj文件中定义的aspect)
启用此选项

4. 为spring-aspect工程添加Facets属性
5. 更改编译器

