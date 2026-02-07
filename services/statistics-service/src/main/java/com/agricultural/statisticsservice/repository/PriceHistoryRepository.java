package com.agricultural.statisticsservice.repository;

import com.agricultural.statisticsservice.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByProductIdAndChangeDateBetweenOrderByChangeDateAsc(Long productId, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<PriceHistory> findByChangeDateBetweenOrderByChangeDateDesc(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
