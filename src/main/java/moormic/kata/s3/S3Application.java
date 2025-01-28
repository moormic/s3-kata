package moormic.kata.s3;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moormic.kata.s3.model.Event;
import moormic.kata.s3.repository.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"moormic.kata"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class S3Application implements CommandLineRunner {
    private static final int MAX_NUM_EVENTS = 1000;
    private static final int MAX_NUM_EVENTS_PER_FILE = 10;
    private static final String BUCKET_NAME = "s3-kata";
    private final S3Repository s3Repository;

    public static void main(String[] args) {
        new SpringApplicationBuilder(S3Application.class)
                .run(args);
    }

    public void run(String... args) {
        var start = Instant.now();
        var events = generateRandomEvents();
        var files = partitionEventsToFiles(events);
        uploadFiles(files);
        files.forEach(File::delete);
        var end = Instant.now();
        log.info("Uploaded {} files to S3 in {} seconds", files.size(), Duration.between(start, end).getSeconds());
        //cleanupFiles();
    }

    private List<Event> generateRandomEvents() {
        return LongStream.range(0, MAX_NUM_EVENTS)
                .mapToObj(l -> new Event(UUID.randomUUID().toString(), "source-event", l+1, Instant.now().toString()))
                .toList();
    }

    private List<File> partitionEventsToFiles(List<Event> events) {
        var fileNum = new AtomicInteger(0);
        return StreamSupport.stream(Iterables.partition(events, MAX_NUM_EVENTS_PER_FILE).spliterator(), false)
                .map(partition -> {
                    var file = new File("page" + fileNum.incrementAndGet() + ".txt");
                    try {
                        var serializedEvents = partition.stream().map(Event::toString).toList();
                        var text = String.join("\n", serializedEvents);
                        var writer = new BufferedWriter(new FileWriter(file));
                        writer.write(text);
                        writer.flush();
                        writer.close();
                        return file;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private void uploadFiles(List<File> files) {
        files.forEach(file -> s3Repository.put(BUCKET_NAME, file));
    }

    private void cleanupFiles() {
        var objects = s3Repository.list(BUCKET_NAME);
        var keys = objects.stream().map(S3Object::key).toList();
        s3Repository.delete(BUCKET_NAME, keys);
        System.out.printf("Deleted %d objects in %s\n", objects.size(), BUCKET_NAME);
    }
}