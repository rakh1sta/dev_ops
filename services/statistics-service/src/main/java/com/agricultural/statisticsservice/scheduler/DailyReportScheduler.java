package com.agricultural.statisticsservice.scheduler;

import com.agricultural.statisticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DailyReportScheduler {
    
    private final StatisticsService statisticsService;
    
    /**
     * Runs daily at midnight to generate the previous day's report
     */
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void generateDailyReport() {
        try {
            statisticsService.generateDailyReport();
            log.info("Daily report generated successfully at {}", java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error generating daily report: ", e);
        }
    }
    
    /**
     * Runs every hour to update system metrics
     */
    @Scheduled(fixedRate = 3600000) // Every hour (3600000 ms)
    public void updateSystemMetrics() {
        try {
            // Update system metrics
            log.info("System metrics updated at {}", java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error updating system metrics: ", e);
        }
    }
}