package com.ivotasevski.transoutbox.lib.repository;

import com.ivotasevski.transoutbox.lib.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query(value = """
            SELECT * 
            FROM outbox 
            WHERE status NOT IN ('RUNNING', 'COMPLETED')
                AND (next_run_not_before IS NULL OR next_run_not_before <= NOW())
            ORDER BY created_at ASC
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<Outbox> findBatchForProcessing(@Param("batchSize") int batchSize);
}
