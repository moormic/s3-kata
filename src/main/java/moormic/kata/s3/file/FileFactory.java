package moormic.kata.s3.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileFactory {
    private static final Random RANDOM = new Random(Instant.now().getEpochSecond());

    public static File getRandom(int size) {
        var uuid = UUID.randomUUID();
        var bytes = new byte[size];
        RANDOM.nextBytes(bytes);

        var file = new File(uuid.toString());
        try (var outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.printf("Unable to write to file %s. Error: %s\n", uuid, e.getMessage());
        }
        return file;
    }
}
