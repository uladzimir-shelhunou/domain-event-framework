package net.vvsh.domainevent.core.repository;

import net.vvsh.domainevent.core.entity.OutboxEntity;
import java.util.List;
import java.util.UUID;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    @Query("SELECT o FROM OutboxEntity o WHERE o.fulfillmentDate is null")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxEntity> findUnprocessedEntities(Pageable pageable);

}
