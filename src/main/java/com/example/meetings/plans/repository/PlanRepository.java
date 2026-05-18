package com.example.meetings.plans.repository;

import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.domain.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findFirstByOwnerUserIdAndTypeAndStatus(Long ownerUserId, PlanType type, PlanStatus status);

    @Query("""
            SELECT p FROM PLANS p
            WHERE p.owner.userId = :ownerUserId
              AND (
                   p.type <> com.example.meetings.plans.model.domain.PlanType.LIVE
                   OR (p.type = com.example.meetings.plans.model.domain.PlanType.LIVE
                       AND p.status = com.example.meetings.plans.model.domain.PlanStatus.ACTIVE
                       AND p.expiresAt > :now)
              )
            ORDER BY p.createdAt DESC
            """)
    List<Plan> findMyPlansExcludingStaleLive(@Param("ownerUserId") Long ownerUserId, @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT *
            FROM PLANS P
            WHERE P.status = 'ACTIVE'
              AND P.expires_at > CAST(:now AS TIMESTAMP)
              AND ((CAST(:excludeOwn AS BOOLEAN) IS FALSE) OR P.owner_user_id <> CAST(:authUserId AS BIGINT))
              AND (CAST(:typeFilter AS VARCHAR) IS NULL OR P.type = CAST(:typeFilter AS VARCHAR))
              AND (
                  CAST(:fromTime AS TIMESTAMP) IS NULL
                  OR P.type = 'LIVE'
                  OR P.start_at >= CAST(:fromTime AS TIMESTAMP)
              )
              AND (
                  CAST(:toTime AS TIMESTAMP) IS NULL
                  OR P.type = 'LIVE'
                  OR P.start_at <= CAST(:toTime AS TIMESTAMP)
              )
              AND P.lat IS NOT NULL
              AND P.lon IS NOT NULL
              AND (
                   6371000 * acos(
                       cos(radians(CAST(:lat AS DOUBLE PRECISION))) * cos(radians(P.lat))
                           * cos(radians(P.lon) - radians(CAST(:lon AS DOUBLE PRECISION)))
                       + sin(radians(CAST(:lat AS DOUBLE PRECISION))) * sin(radians(P.lat))
                   )
              ) <= CAST(:radiusM AS INTEGER)
            ORDER BY P.created_at DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM PLANS P
            WHERE P.status = 'ACTIVE'
              AND P.expires_at > CAST(:now AS TIMESTAMP)
              AND ((CAST(:excludeOwn AS BOOLEAN) IS FALSE) OR P.owner_user_id <> CAST(:authUserId AS BIGINT))
              AND (CAST(:typeFilter AS VARCHAR) IS NULL OR P.type = CAST(:typeFilter AS VARCHAR))
              AND (
                  CAST(:fromTime AS TIMESTAMP) IS NULL
                  OR P.type = 'LIVE'
                  OR P.start_at >= CAST(:fromTime AS TIMESTAMP)
              )
              AND (
                  CAST(:toTime AS TIMESTAMP) IS NULL
                  OR P.type = 'LIVE'
                  OR P.start_at <= CAST(:toTime AS TIMESTAMP)
              )
              AND P.lat IS NOT NULL
              AND P.lon IS NOT NULL
              AND (
                   6371000 * acos(
                       cos(radians(CAST(:lat AS DOUBLE PRECISION))) * cos(radians(P.lat))
                           * cos(radians(P.lon) - radians(CAST(:lon AS DOUBLE PRECISION)))
                       + sin(radians(CAST(:lat AS DOUBLE PRECISION))) * sin(radians(P.lat))
                   )
              ) <= CAST(:radiusM AS INTEGER)
            """,
            nativeQuery = true)
    Page<Plan> findNearby(@Param("lat") Double lat,
                          @Param("lon") Double lon,
                          @Param("radiusM") Integer radiusM,
                          @Param("typeFilter") String typeFilter,
                          @Param("fromTime") LocalDateTime fromTime,
                          @Param("toTime") LocalDateTime toTime,
                          @Param("excludeOwn") boolean excludeOwn,
                          @Param("authUserId") Long authUserId,
                          @Param("now") LocalDateTime now,
                          Pageable pageable);

    @Modifying
    @Query(value = """
            UPDATE PLANS
            SET status = 'EXPIRED', updated_at = :now, closed_at = :now
            WHERE status = 'ACTIVE' AND expires_at <= :now
            """, nativeQuery = true)
    int expireActivePlans(@Param("now") LocalDateTime now);
}

