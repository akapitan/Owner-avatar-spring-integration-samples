package com.example.basic.file.config;

import com.example.basic.file.service.FileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.transformer.FileToByteArrayTransformer;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.Objects;

/**
 * Java DSL configuration for file processing operations.
 */
@Configuration
public class FileProcessingConfig {

    private final FileHandler fileHandler;

    @Value("${file.processing.input:${java.io.tmpdir}/spring-integration-samples/processing/input}")
    private String inputDirectory;

    @Value("${file.processing.output:${java.io.tmpdir}/spring-integration-samples/processing/output}")
    private String outputDirectory;

    public FileProcessingConfig(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Bean
    public MessageChannel fileInputChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel fileProcessingChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public IntegrationFlow fileReadingFlow() {
        CompositeFileListFilter<File> filter = new CompositeFileListFilter<>();
        filter.addFilter(new SimplePatternFileListFilter("*.txt"));
        filter.addFilter(new AcceptOnceFileListFilter<>());

        return IntegrationFlow
                .from(Files.inboundAdapter(new File(inputDirectory))
                                .filter(filter)
                                .useWatchService(true)
                                .watchEvents(
                                        FileReadingMessageSource.WatchEventType.CREATE,
                                        FileReadingMessageSource.WatchEventType.MODIFY),
                        e -> e.poller(Pollers.fixedDelay(5000)))
                .enrichHeaders(h -> h.header(FileHeaders.ORIGINAL_FILE, FileHeaders.ORIGINAL_FILE))
                .channel(fileInputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow fileProcessingFlow() {
        return IntegrationFlow
                .from(fileInputChannel())
                .transform(new FileToByteArrayTransformer())
                .handle(fileHandler, "handleBytes")
                .channel(fileProcessingChannel())
                .get();
    }

    @Bean
    public IntegrationFlow fileWritingFlow() {
        return IntegrationFlow
                .from(fileProcessingChannel())
                .handle(Files.outboundAdapter(new File(outputDirectory))
                        .autoCreateDirectory(true)
                        .fileNameGenerator(message ->
                                "processed_" + (new File((String) Objects.requireNonNull(message.getHeaders()
                                        .get(FileHeaders.FILENAME)))).getName()))
                .get();
    }
}