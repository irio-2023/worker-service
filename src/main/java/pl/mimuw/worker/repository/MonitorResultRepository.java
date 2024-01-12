package pl.mimuw.worker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.mimuw.worker.entity.MonitorResultEntity;

import java.util.Optional;
import java.util.UUID;

public interface MonitorResultRepository extends MongoRepository<MonitorResultEntity, UUID> {

    Optional<MonitorResultEntity> findByJobId(UUID jobId);

    Optional<MonitorResultEntity> findTopByJobIdOrderByTimestampDesc(UUID jobId);
}
