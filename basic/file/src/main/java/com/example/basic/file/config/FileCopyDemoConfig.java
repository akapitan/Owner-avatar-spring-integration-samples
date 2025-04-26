package com.example.basic.file.config;

import com.example.basic.file.service.FileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;

import java.io.File;

/**
 * Java DSL configuration for the file copy demo.
 * This replaces the XML configuration from the original sample.
 */
@Configuration
public class FileCopyDemoConfig {

    private final FileHandler fileHandler;

    @Value("${file.input.directory:${java.io.tmpdir}/spring-integration-samples/input}")
    private String inputDirectory;

    @Value("${file.output.directory:${java.io.tmpdir}/spring-integration-samples/output}")
    private String outputDirectory;

    @Value("${file.pattern:*.txt}")
    private String filePattern;

    public FileCopyDemoConfig(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Bean
    public MessageChannel filesChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public IntegrationFlow fileInboundFlow() {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(inputDirectory))
                                .filter(new SimplePatternFileListFilter(filePattern))
                                .preventDuplicates(true),
                        e -> e.poller(Pollers.fixedRate(1000)))
                .channel(filesChannel())
                .get();
    }

    @Bean
    public IntegrationFlow fileOutboundFlow() {
        return IntegrationFlow
                .from(filesChannel())
                .handle(fileHandler, "handleFile")
                .handle(Files.outboundAdapter(new File(outputDirectory))
                        .autoCreateDirectory(true))
                .get();
    }
}