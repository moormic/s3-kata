package moormic.kata;

import lombok.RequiredArgsConstructor;
import moormic.kata.file.FileFactory;
import moormic.kata.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

@SpringBootApplication
@ComponentScan(basePackages = {"moormic.kata"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class S3Application implements CommandLineRunner {
    private static final int RUN_NUMBER = 1000;
    private static final int FILE_SIZE = 10_000;
    private static final String BUCKET_NAME = "s3-kata";
    private final S3Repository s3Repository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(S3Application.class)
                .run(args);
    }

    public void run(String... args) {
        uploadFiles();
        cleanupFiles();
    }

    private void uploadFiles() {
        var start = Instant.now();
        IntStream.range(0, RUN_NUMBER).forEach(i -> {
            var file = FileFactory.getRandom(FILE_SIZE);
            s3Repository.put(BUCKET_NAME, file);
        });
        var end = Instant.now();
        var timeTaken = Duration.between(start, end).getSeconds();
        var bytesUploaded = RUN_NUMBER * FILE_SIZE;
        var bytesThroughput = bytesUploaded / timeTaken;
        System.out.printf("With file size %d, throughput is %d bytes per second.\n", FILE_SIZE, bytesThroughput);
    }

    private void cleanupFiles() {
        var objects = s3Repository.list(BUCKET_NAME);
        var keys = objects.stream().map(S3Object::key).toList();
        s3Repository.delete(BUCKET_NAME, keys);
        System.out.printf("Deleted %d objects in %s\n", objects.size(), BUCKET_NAME);
    }

}