package com.example.basic.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringIntegrationTest
@TestPropertySource(properties = {
        "file.input.directory=${java.io.tmpdir}/si-test-input",
        "file.output.directory=${java.io.tmpdir}/si-test-output"
})
class FileIntegrationTests {

    @Autowired
    private MessageChannel filesChannel;

    @Test
    void testDirectFileHandling(@TempDir Path tempDir) throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("test-direct.txt");
        Files.write(testFile, "test content".getBytes());

        // Send file directly to channel
        filesChannel.send(new GenericMessage<>(testFile.toFile()));

        // Verify that output directory contains the file
        String outputDir = System.getProperty("java.io.tmpdir") + "/si-test-output";
        File outputDirFile = new File(outputDir);

        await().atMost(5, TimeUnit.SECONDS).until(() ->
                Files.list(outputDirFile.toPath()).count() > 0);

        assertTrue(Files.list(outputDirFile.toPath()).count() > 0);
    }

    @Test
    void testFilePolling(@TempDir Path tempDir) throws IOException {
        // Get the paths from system properties 
        String inputDir = System.getProperty("java.io.tmpdir") + "/si-test-input";
        String outputDir = System.getProperty("java.io.tmpdir") + "/si-test-output";

        // Create directories if they don't exist
        new File(inputDir).mkdirs();
        new File(outputDir).mkdirs();

        // Create a test file
        Path testFile = tempDir.resolve("test-polling.txt");
        Files.write(testFile, "test polling content".getBytes());

        // Copy file to input directory
        Files.copy(testFile,
                new File(inputDir + "/test-polling.txt").toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        // Verify that output directory eventually contains the file
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                Files.list(new File(outputDir).toPath())
                        .anyMatch(p -> p.getFileName().toString().equals("test-polling.txt")));
    }
}