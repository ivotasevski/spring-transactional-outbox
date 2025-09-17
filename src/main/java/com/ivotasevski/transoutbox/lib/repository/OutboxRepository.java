package com.ivotasevski.transoutbox.lib.repository;

import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query(value = """
            SELECT * 
            FROM outbox 
            WHERE status <> 'IN_PROGRESS'
            ORDER BY id
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<Outbox> findBatchForProcessing(@Param("batchSize") int batchSize);

    @Modifying
    @Query("UPDATE Outbox o SET o.status = :state WHERE o.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("state") OutboxStatus status);
}
