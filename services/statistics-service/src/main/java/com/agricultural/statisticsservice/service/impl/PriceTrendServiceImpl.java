//package com.agricultural.statisticsservice.service.impl;
//
//import com.agricultural.statisticsservice.client.ProductServiceClient;
//import com.agricultural.statisticsservice.dto.PriceTrendDTO;
//import com.agricultural.statisticsservice.dto.PriceTrendDTO.PricePoint;
//import com.agricultural.statisticsservice.entity.PriceHistory;
//import com.agricultural.statisticsservice.repository.PriceHistoryRepository;
//import com.agricultural.statisticsservice.service.PriceTrendService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PriceTrendServiceImpl implements PriceTrendService {
//
//    private final PriceHistoryRepository priceHistoryRepository;
//    private final ProductServiceClient productServiceClient;
//
//    @Override
//    public List<PriceTrendDTO> getHistoricalPriceTrends(Long productId, LocalDate startDate, LocalDate endDate) {
//        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductIdAndChangeDateBetweenOrderByChangeDateAsc(productId, startDate.atStartOfDay(), endDate.atStartOfDay());
//
//        return priceHistories.stream()
//                .map(this::convertToPriceTrendDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PriceTrendDTO> getRecentPriceChanges(int days) {
//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusDays(days);
//
//        List<PriceHistory> priceHistories = priceHistoryRepository.findByChangeDateBetweenOrderByChangeDateDesc(
//                startDate.atStartOfDay(), endDate.atStartOfDay());
//
//        return priceHistories.stream()
//                .map(this::convertToPriceTrendDTO)
//                .distinct()
//                .limit(50) // Limit to prevent too much data
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PriceTrendDTO> getTopPriceChangers(int limit, String direction) {
//        LocalDate weekAgo = LocalDate.now().minusWeeks(1);
//
//        List<PriceHistory> recentChanges = priceHistoryRepository.findByChangeDateBetweenOrderByChangeDateDesc(
//                weekAgo.atStartOfDay(), LocalDate.now().atStartOfDay());
//
//        // Group by product and calculate percentage change
//        return recentChanges.stream()
//                .collect(Collectors.groupingBy(
//                        ph -> ph.getProduct().getId(),
//                        Collectors.toList()
//                ))
//                .entrySet().stream()
//                .map(entry -> {
//                    List<PriceHistory> changes = entry.getValue();
//                    if (changes.size() < 2) return null;
//
//                    PriceHistory first = changes.get(changes.size() - 1); // Earliest
//                    PriceHistory last = changes.get(0); // Latest
//
//                    BigDecimal oldPrice = first.getOldPrice();
//                    BigDecimal newPrice = last.getNewPrice();
//
//                    if (oldPrice.compareTo(BigDecimal.ZERO) == 0) return null;
//
//                    BigDecimal changePercent = newPrice.subtract(oldPrice)
//                            .divide(oldPrice, 4, RoundingMode.HALF_UP)
//                            .multiply(BigDecimal.valueOf(100));
//
//                    PriceTrendDTO trend = new PriceTrendDTO();
//                    trend.setProductId(first.getProduct().getId());
//                    trend.setProductName(first.getProduct().getName());
//                    trend.setCategory(first.getProduct().getCategory() != null ? first.getProduct().getCategory().getName() : "Unknown");
//                    trend.setCurrentPrice(newPrice);
//                    trend.setPriceChangePercentage(changePercent);
//                    trend.setAnalysisDate(LocalDate.now());
//
//                    // Determine trend direction
//                    if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
//                        trend.setTrendDirection("UP");
//                    } else if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
//                        trend.setTrendDirection("DOWN");
//                    } else {
//                        trend.setTrendDirection("STABLE");
//                    }
//
//                    // Add price points for the trend
//                    List<PricePoint> pricePoints = new ArrayList<>();
//                    for (int i = changes.size() - 1; i >= 0; i--) {
//                        PriceHistory change = changes.get(i);
//                        pricePoints.add(new PricePoint(change.getChangeDate().toLocalDate(), change.getNewPrice()));
//                    }
//                    trend.setPricePoints(pricePoints);
//
//                    return trend;
//                })
//                .filter(trend -> trend != null)
//                .filter(trend ->
//                    "BOTH".equalsIgnoreCase(direction) ||
//                    trend.getTrendDirection().equalsIgnoreCase(direction)
//                )
//                .sorted(Comparator.comparing(PriceTrendDTO::getPriceChangePercentage).reversed())
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public PriceTrendDTO getPriceTrendForProduct(Long productId) {
//        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductIdOrderByChangeDateDesc(productId);
//
//        if (priceHistories.isEmpty()) {
//            return null;
//        }
//
//        // Get the product details
////        var product = productServiceClient.getProductById(productId);
//
//        PriceTrendDTO trend = new PriceTrendDTO();
//        trend.setProductId(productId);
////        trend.setProductName(product.getName());
////        trend.setCategory(product.getCategoryName());
//
//        // Calculate trend based on recent changes
//        if (priceHistories.size() >= 2) {
//            PriceHistory latest = priceHistories.get(0);
//            PriceHistory earliest = priceHistories.get(priceHistories.size() - 1);
//
//            BigDecimal oldPrice = earliest.getOldPrice();
//            BigDecimal newPrice = latest.getNewPrice();
//
//            if (oldPrice.compareTo(BigDecimal.ZERO) != 0) {
//                BigDecimal changePercent = newPrice.subtract(oldPrice)
//                        .divide(oldPrice, 4, RoundingMode.HALF_UP)
//                        .multiply(BigDecimal.valueOf(100));
//
//                trend.setPriceChangePercentage(changePercent);
//
//                if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
//                    trend.setTrendDirection("UP");
//                } else if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
//                    trend.setTrendDirection("DOWN");
//                } else {
//                    trend.setTrendDirection("STABLE");
//                }
//            }
//        } else {
//            trend.setPriceChangePercentage(BigDecimal.ZERO);
//            trend.setTrendDirection("STABLE");
//        }
//
//        trend.setCurrentPrice(priceHistories.get(0).getNewPrice());
//        trend.setAnalysisDate(LocalDate.now());
//
//        // Add all price points
//        List<PricePoint> pricePoints = priceHistories.stream()
//                .map(ph -> new PricePoint(ph.getChangeDate().toLocalDate(), ph.getNewPrice()))
//                .collect(Collectors.toList());
//        trend.setPricePoints(pricePoints);
//
//        return trend;
//    }
//
//    @Override
//    public List<PriceTrendDTO> getSeasonalPriceTrends(Long productId, String season) {
//        // This would typically analyze historical data for seasonal patterns
//        // For now, we'll return trends for the specified product in the given season timeframe
//        LocalDate now = LocalDate.now();
//        LocalDate startOfSeason, endOfSeason;
//
//        switch (season.toLowerCase()) {
//            case "spring":
//                startOfSeason = LocalDate.of(now.getYear(), 3, 1);
//                endOfSeason = LocalDate.of(now.getYear(), 5, 31);
//                break;
//            case "summer":
//                startOfSeason = LocalDate.of(now.getYear(), 6, 1);
//                endOfSeason = LocalDate.of(now.getYear(), 8, 31);
//                break;
//            case "autumn":
//            case "fall":
//                startOfSeason = LocalDate.of(now.getYear(), 9, 1);
//                endOfSeason = LocalDate.of(now.getYear(), 11, 30);
//                break;
//            case "winter":
//                startOfSeason = LocalDate.of(now.getYear(), 12, 1);
//                endOfSeason = LocalDate.of(now.getYear() + 1, 2, 28);
//                break;
//            default:
//                startOfSeason = now.minusMonths(3);
//                endOfSeason = now;
//        }
//
//        List<PriceHistory> seasonalChanges = priceHistoryRepository.findByProductIdAndChangeDateBetweenOrderByChangeDateAsc(
//                productId, startOfSeason.atStartOfDay(), endOfSeason.atStartOfDay());
//
//        if (seasonalChanges.isEmpty()) {
//            return List.of();
//        }
//
//        PriceTrendDTO trend = new PriceTrendDTO();
//        trend.setProductId(productId);
//        trend.setSeason(season);
//        trend.setStartDate(startOfSeason);
//        trend.setEndDate(endOfSeason);
//
//        // Calculate seasonal trend
//        PriceHistory first = seasonalChanges.get(seasonalChanges.size() - 1);
//        PriceHistory last = seasonalChanges.get(0);
//
//        BigDecimal oldPrice = first.getOldPrice();
//        BigDecimal newPrice = last.getNewPrice();
//
//        if (oldPrice.compareTo(BigDecimal.ZERO) != 0) {
//            BigDecimal changePercent = newPrice.subtract(oldPrice)
//                    .divide(oldPrice, 4, RoundingMode.HALF_UP)
//                    .multiply(BigDecimal.valueOf(100));
//
//            trend.setPriceChangePercentage(changePercent);
//
//            if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
//                trend.setTrendDirection("UP");
//            } else if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
//                trend.setTrendDirection("DOWN");
//            } else {
//                trend.setTrendDirection("STABLE");
//            }
//        }
//
//        trend.setCurrentPrice(last.getNewPrice());
//        trend.setAnalysisDate(LocalDate.now());
//
//        // Add seasonal price points
//        List<PricePoint> pricePoints = seasonalChanges.stream()
//                .map(ph -> new PricePoint(ph.getChangeDate().toLocalDate(), ph.getNewPrice()))
//                .collect(Collectors.toList());
//        trend.setPricePoints(pricePoints);
//
//        return List.of(trend);
//    }
//
//    private PriceTrendDTO convertToPriceTrendDTO(PriceHistory priceHistory) {
//        PriceTrendDTO trend = new PriceTrendDTO();
//        trend.setProductId(priceHistory.getProduct().getId());
//        trend.setProductName(priceHistory.getProduct().getName());
//        trend.setCategory(priceHistory.getProduct().getCategory() != null ? priceHistory.getProduct().getCategory().getName() : "Unknown");
//        trend.setCurrentPrice(priceHistory.getNewPrice());
//        trend.setAnalysisDate(priceHistory.getChangeDate().toLocalDate());
//
//        // Calculate change percentage
//        if (priceHistory.getOldPrice().compareTo(BigDecimal.ZERO) != 0) {
//            BigDecimal changePercent = priceHistory.getNewPrice().subtract(priceHistory.getOldPrice())
//                    .divide(priceHistory.getOldPrice(), 4, RoundingMode.HALF_UP)
//                    .multiply(BigDecimal.valueOf(100));
//            trend.setPriceChangePercentage(changePercent);
//
//            if (changePercent.compareTo(BigDecimal.ZERO) > 0) {
//                trend.setTrendDirection("UP");
//            } else if (changePercent.compareTo(BigDecimal.ZERO) < 0) {
//                trend.setTrendDirection("DOWN");
//            } else {
//                trend.setTrendDirection("STABLE");
//            }
//        }
//
//        // Add single price point for this history entry
//        trend.setPricePoints(List.of(new PricePoint(
//                priceHistory.getChangeDate().toLocalDate(),
//                priceHistory.getNewPrice()
//        )));
//
//        return trend;
//    }
//}