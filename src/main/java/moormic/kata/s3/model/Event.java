package moormic.kata.s3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private String source;
    private Long offset;
    private String processingTimestamp;

    @Override
    public String toString() {
        return String.format("%s|%s|%d|%s", source, id, offset, processingTimestamp);
    }
}
