package pl.mimuw.worker.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document("monitor_result")
public class MonitorResultEntity {

    @Id
    private UUID id;

    @Indexed
    private UUID jobId;

    private Date timestamp;

    private MonitorResult result;
}
