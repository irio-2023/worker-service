package pl.mimuw.worker.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document("monitor_result")
public class MonitorResultEntity {

    @Id
    private ObjectId id;

    @Indexed
    @Field(targetType = FieldType.STRING)
    private UUID jobId;

    private Long timestamp;

    private MonitorResult result;
}
