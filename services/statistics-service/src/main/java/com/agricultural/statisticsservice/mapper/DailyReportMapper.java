package com.agricultural.statisticsservice.mapper;

import com.agricultural.statisticsservice.dto.DailyReportDTO;
import com.agricultural.statisticsservice.entity.DailyReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyReportMapper {
    
    DailyReportMapper INSTANCE = Mappers.getMapper(DailyReportMapper.class);
    
    DailyReportDTO toDto(DailyReport dailyReport);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DailyReport toEntity(DailyReportDTO dailyReportDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(DailyReportDTO dailyReportDTO, @MappingTarget DailyReport dailyReport);
}