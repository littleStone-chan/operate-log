package com.o2osys.log.server;

import com.o2osys.log.client.intrceptor.LogInterceptor;
import com.o2osys.log.server.receiver.RabbitReceiver;
import com.o2osys.tools.commons.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.o2osys.tools.config.exception",
        "com.o2osys.tools.config.DateConfig",
        "com.o2osys.tools.config.interceptor",
        "com.o2osys.tools.config.redis",
        "com.o2osys.tools.config.swagger",
        "com.o2osys.tools.cache",
        "com.o2osys.tools.rabbitmq",
        "com.o2osys.tools.token",
        "com.o2osys.tools.web.filter",
        "com.o2osys.log.server",
        "com.o2osys.log.client",
})
public class ObjectLoggerServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ObjectLoggerServerApplication.class, args);
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
