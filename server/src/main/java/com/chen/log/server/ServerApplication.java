package com.chen.log.server;

import com.chen.log.server.receiver.RabbitReceiver;
import com.chen.tools.util.StringUtils;
import com.o2osys.log.client.intrceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.chen.tools.config.exception",
        "com.chen.tools.config.DateConfig",
        "com.chen.tools.config.interceptor",
        "com.chen.tools.config.redis",
        "com.chen.tools.config.swagger",
        "com.chen.tools.cache",
        "com.chen.tools.rabbitmq",
        "com.chen.tools.token",
        "com.chen.tools.web.filter",
        "com.chen.log.server",
        "com.chen.log.client",
})
public class ServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ServerApplication.class, args);
        System.out.println(run.getBean(RabbitReceiver.class));
        System.out.println(
                "  ___  _     _           _   _                                \n" +
                        " / _ \\| |__ (_) ___  ___| |_| |    ___   __ _  __ _  ___ _ __ \n" +
                        "| | | | '_ \\| |/ _ \\/ __| __| |   / _ \\ / _` |/ _` |/ _ \\ '__|\n" +
                        "| |_| | |_) | |  __/ (__| |_| |__| (_) | (_| | (_| |  __/ |   \n" +
                        " \\___/|_.__// |\\___|\\___|\\__|_____\\___/ \\__, |\\__, |\\___|_|   \n" +
                        "          |__/                          |___/ |___/           ");
        System.out.println("operate-log Server application start successfully!");
        System.out.println("Visit the following address for more information:");
        System.out.println("http://127.0.0.1:12300/doc.html");
    }

    //数据库连接url
    @Value("${spring.datasource.url}")
    String datasourceUrl;

    /**
     * 注册日志mybatis拦截器
     */
    @Bean
    public LogInterceptor logInterceptor() {

        //获取连接数据库名
        String tableSchema = StringUtils.substringBetween(datasourceUrl,":3306/","?");
        tableSchema = tableSchema != null ? tableSchema : StringUtils.substringAfter(datasourceUrl,":3306/");

        LogInterceptor interceptor = new LogInterceptor();
        Properties properties = new Properties();
        properties.setProperty("tableSchema",tableSchema.trim());
        properties.setProperty("appName",tableSchema.trim());
        interceptor.setProperties(properties);
        return interceptor;
    }

}
