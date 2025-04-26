package com.example.basic.file.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * A command line interface to help users test the file integration flows.
 */
@Component
public class FileIntegrationCliRunner implements CommandLineRunner {

    private final Environment env;

    public FileIntegrationCliRunner(Environment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) {
        // Get directory paths from environment
        String inputDir = env.getProperty("file.input.directory");
        String outputDir = env.getProperty("file.output.directory");
        String processingInputDir = env.getProperty("file.processing.input");
        String processingOutputDir = env.getProperty("file.processing.output");

        // Ensure directories exist
        createDirectoryIfNotExists(inputDir);
        createDirectoryIfNotExists(outputDir);
        createDirectoryIfNotExists(processingInputDir);
        createDirectoryIfNotExists(processingOutputDir);

        // Show help if requested
        if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h"))) {
            showHelp(inputDir, outputDir, processingInputDir, processingOutputDir);
            return;
        }

        // Show information about the configured directories
        System.out.println("\nDirectories:");
        System.out.println("  Input: " + inputDir);
        System.out.println("  Output: " + outputDir);
        System.out.println("  Processing Input: " + processingInputDir);
        System.out.println("  Processing Output: " + processingOutputDir);

        // Offer interactive mode if requested
        if (args.length > 0 && args[0].equals("--interactive")) {
            runInteractiveMode(inputDir, processingInputDir);
        }
    }

    private void createDirectoryIfNotExists(String dirPath) {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Created directory: " + dirPath);
            } catch (Exception e) {
                System.err.println("Failed to create directory: " + dirPath);
                e.printStackTrace();
            }
        }
    }

    private void showHelp(String inputDir, String outputDir, String processingInputDir, String processingOutputDir) {
        System.out.println("\nSpring Integration File Sample Help");
        System.out.println("=================================");
        System.out.println("\nCommands:");
        System.out.println("  --help, -h          Show this help message");
        System.out.println("  --interactive       Start in interactive mode");
        System.out.println("\nDirectory Configuration:");
        System.out.println("  Input Directory:           " + inputDir);
        System.out.println("  Output Directory:          " + outputDir);
        System.out.println("  Processing Input Directory: " + processingInputDir);
        System.out.println("  Processing Output Directory:" + processingOutputDir);
        System.out.println("\nUsage:");
        System.out.println("  1. Place files in the input directory to see them copied to the output directory");
        System.out.println("  2. Place files in the processing input directory to see them processed and placed in the processing output directory");
        System.out.println("\n");
    }

    private void runInteractiveMode(String inputDir, String processingInputDir) {
        System.out.println("\nInteractive Mode");
        System.out.println("================");
        System.out.println("Enter file content to create test files (or 'exit' to quit):");

        Scanner scanner = new Scanner(System.in);
        int fileCount = 1;

        while (true) {
            System.out.print("\nEnter content (exit to quit): ");
            String content = scanner.nextLine();

            if ("exit".equalsIgnoreCase(content.trim())) {
                break;
            }

            System.out.print("Choose destination (1 = simple, 2 = processing): ");
            String choice = scanner.nextLine();

            try {
                String fileName = "test-file-" + fileCount + ".txt";
                String filePath;

                if ("2".equals(choice)) {
                    filePath = processingInputDir + File.separator + fileName;
                } else {
                    filePath = inputDir + File.separator + fileName;
                }

                Files.write(Paths.get(filePath), content.getBytes());
                System.out.println("Created file: " + filePath);
                fileCount++;
            } catch (Exception e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }

        System.out.println("Exiting interactive mode...");
    }
}