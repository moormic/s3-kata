package moormic.kata.s3;

import lombok.RequiredArgsConstructor;
import moormic.kata.s3.file.FileFactory;
import moormic.kata.s3.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
@ComponentScan(basePackages = {"moormic.kata"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class S3Application implements CommandLineRunner {
    private static final int NUM_FILES = 1000;
    private static final int FILE_SIZE = 100000;
    private static final String BUCKET_NAME = "s3-kata";
    private final S3Repository s3Repository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(S3Application.class)
                .run(args);
    }

    public void run(String... args) {
        var files = createFiles();
        uploadFiles(files);
        cleanupFiles();
    }

    private List<File> createFiles() {
        var start = Instant.now();
        var files = IntStream.range(0, NUM_FILES)
                .mapToObj(FileFactory::getRandom)
                .toList();
        var end = Instant.now();
        System.out.printf("Created %d files in %d seconds\n", NUM_FILES, Duration.between(start, end).getSeconds());
        return files;
    }

    private void uploadFiles(List<File> files) {
        var start = Instant.now();
        files.forEach(file -> s3Repository.put(BUCKET_NAME, file));
        var end = Instant.now();
        var timeTaken = Duration.between(start, end).getSeconds();
        var bytesUploaded = NUM_FILES * FILE_SIZE;
        var bytesThroughput = bytesUploaded / timeTaken;
        System.out.printf("With file size %d, upload throughput is %d bytes per second.\n", FILE_SIZE, bytesThroughput);
    }

    private void cleanupFiles() {
        var objects = s3Repository.list(BUCKET_NAME);
        var keys = objects.stream().map(S3Object::key).toList();
        s3Repository.delete(BUCKET_NAME, keys);
        System.out.printf("Deleted %d objects in %s\n", objects.size(), BUCKET_NAME);
    }
}