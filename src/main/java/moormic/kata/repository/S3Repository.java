package moormic.kata.repository;

import moormic.kata.config.S3Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.HashMap;
import java.util.List;


@Repository
public class S3Repository {
    private final S3Client s3Client;

    @Autowired
    public S3Repository(S3Configuration s3Configuration) {
        var credentials = AwsBasicCredentials.create(s3Configuration.getAccessKey(), s3Configuration.getSecretKey());
        this.s3Client = S3Client
                .builder()
                .region(Region.of(s3Configuration.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public List<Bucket> bucketNames() {
        return s3Client.listBuckets().buckets();
    }

    public void put(String bucketName, File file) {
        var metadata = new HashMap<String, String>();
        metadata.put("key", file.getName());

        s3Client.putObject(request ->
                request.bucket(bucketName)
                        .key(file.getName())
                        .metadata(metadata)
                        .ifNoneMatch("*"),
                file.toPath());
    }

    public List<S3Object> list(String bucketName) {
        return s3Client.listObjectsV2(request -> request.bucket(bucketName).build()).contents();
    }

    public void delete(String bucketName, List<String> keys) {
        var objectsToDelete = keys.stream().map(key -> ObjectIdentifier.builder().key(key).build()).toList();
        s3Client.deleteObjects(request -> request.bucket(bucketName).delete(delete -> delete.objects(objectsToDelete)));
    }
}
