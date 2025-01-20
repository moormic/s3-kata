package moormic.kata;

import lombok.RequiredArgsConstructor;
import moormic.kata.config.S3Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"moormic.kata"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class S3Application {
    private final S3Configuration s3Configuration;

    public static void main(String[] args) {

    }

}
