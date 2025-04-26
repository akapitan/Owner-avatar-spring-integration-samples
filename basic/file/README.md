# Spring Integration File Sample

This sample demonstrates the use of Spring Integration's File adapters. It was based on the original XML configuration
sample but fully converted to Java DSL.

## Overview

The sample consists of two main integration flows:

1. **Simple File Copy Flow** - Monitors an input directory and copies files to an output directory
2. **File Processing Flow** - Watches for files, transforms them to bytes, processes the content, and writes the results
   to an output directory with modified filenames

## Running the Sample

You can run this sample directly from your IDE or using Gradle:

```bash
./gradlew :basic:file:bootRun
```

### Directory Structure

The application uses temporary directories by default:

- Input directory: `${java.io.tmpdir}/spring-integration-samples/input`
- Output directory: `${java.io.tmpdir}/spring-integration-samples/output`
- Processing input directory: `${java.io.tmpdir}/spring-integration-samples/processing/input`
- Processing output directory: `${java.io.tmpdir}/spring-integration-samples/processing/output`

You can override these locations by setting the appropriate properties in `application.properties` or passing them as
command-line parameters.

### Testing the File Flows

1. Create the input directory if it doesn't exist
2. Place text files in the input directory
3. Observe the files being copied to the output directory
4. For the processing flow, place files in the processing input directory
5. Observe the processed files appearing in the processing output directory with "processed_" prefix

## Java DSL vs XML Configuration

This sample demonstrates how to use Spring Integration's Java DSL instead of XML configuration. The Java DSL offers
several advantages:

- Type safety and IDE auto-completion
- Better refactoring support
- Easier testing
- More concise and readable configuration
- Ability to use the full power of Java for complex configurations

## Key Classes

- `FileHandler` - Service class for handling different types of file payloads
- `FileCopyDemoConfig` - Basic file copy configuration using Java DSL
- `FileProcessingConfig` - More complex file processing flow with transformers and multiple channels
- `FileApplication` - Main application class