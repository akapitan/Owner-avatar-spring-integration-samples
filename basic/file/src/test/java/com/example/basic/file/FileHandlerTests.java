package com.example.basic.file;

import com.example.basic.file.service.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the FileHandler class.
 */
class FileHandlerTests {

    private FileHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FileHandler();
    }

    @Test
    void testHandleString() {
        String input = "hello world";
        String result = handler.handleString(input);
        assertEquals("HELLO WORLD", result);
    }

    @Test
    void testHandleBytes() {
        byte[] input = "test data".getBytes();
        byte[] output = handler.handleBytes(input);
        assertEquals("TEST DATA", new String(output));
    }

    @Test
    void testHandleFile(@TempDir Path tempDir) throws Exception {
        // Create a test file
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "test content".getBytes());

        // Test the handler
        File result = handler.handleFile(testFile.toFile());
        assertNotNull(result);
        assertEquals(testFile.toFile().getAbsolutePath(), result.getAbsolutePath());
    }
}