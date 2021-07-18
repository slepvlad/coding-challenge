package org.decta.bpmnparser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BpmnParserApplication implements CommandLineRunner {

    @Value("${path.input}")
    private String path;

    @Autowired
    private BpmnParser parser;


    public static void main(String[] args) {
        SpringApplication.run(BpmnParserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        parser.drawSignalsRelations(path);
    }
}
