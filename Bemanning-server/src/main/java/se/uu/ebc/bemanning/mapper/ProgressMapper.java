package se.uu.ebc.bemanning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import se.uu.ebc.bemanning.dto.ProgressDTO;
import se.uu.ebc.bemanning.entity.Progress;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProgressMapper {

    ProgressMapper INSTANCE = Mappers.getMapper (ProgressMapper.class);

    @Mapping(target = "phdPositionId", source = "phdPosition.id")
    ProgressDTO entityToDTO(Progress progress);

    @Mapping(target = "phdPosition", ignore = true)
    @Mapping(target = "monthsAtStart", ignore = true)
    Progress dtoToEntity(ProgressDTO progressDTO);

    @Mapping(target = "phdPosition", ignore = true)
    @Mapping(target = "monthsAtStart", ignore = true)
    void updateEntityFromDTO(ProgressDTO progressDTO, @MappingTarget Progress progress);
} 