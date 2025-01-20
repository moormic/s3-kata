package moormic.kata;

import lombok.RequiredArgsConstructor;
import moormic.kata.file.FileFactory;
import moormic.kata.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

@SpringBootApplication
@ComponentScan(basePackages = {"moormic.kata"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class S3Application implements CommandLineRunner {
    private static final int RUN_NUMBER = 100;          // publish 100 files
    private static final String BUCKET_NAME = "s3-kata";
    private final S3Repository s3Repository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(S3Application.class)
                .run(args);
    }

    public void run(String... args) {
        IntStream.range(0, RUN_NUMBER).forEach(i -> {
            var file = FileFactory.get();
            var start = Instant.now();
            s3Repository.put(BUCKET_NAME, file);
            var end = Instant.now();
            System.out.printf("Published file # %d. Took %d seconds\n", i, Duration.between(start, end).getSeconds());
        });
    }

}
