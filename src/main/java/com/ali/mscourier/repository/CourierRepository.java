package com.ali.mscourier.repository;

import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByIdAndDeletedFalse(Long id);

    Optional<Courier> findFirstByStatusAndDeletedFalseOrderByIdAsc(CourierStatus status);

    Page<Courier> findAllByDeletedFalse(Pageable pageable);
    Optional<Courier> findFirstByStatusOrderByIdAsc(CourierStatus status);
    boolean existsByFinCode(String finCode);
    boolean existsByFinCodeAndIdNot(String finCode, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Courier c where c.id = :id and c.deleted = false")
    Optional<Courier> findLockedById(@Param("id") Long id);
}
