package se.uu.ebc.bemanning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import se.uu.ebc.bemanning.dto.PhDPositionDTO;
import se.uu.ebc.bemanning.entity.PhDPosition;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PhDPositionMapper {

    PhDPositionMapper INSTANCE = Mappers.getMapper (PhDPositionMapper.class);

    @Mapping(target = "personId", source = "person.id")
    @Mapping(target = "predictedFinishDate", expression = "java(phdPosition.predictedFinishDate().toLocalDate())")
    @Mapping(target = "predictedHalfTime", expression = "java(phdPosition.predictedHalfTime().toLocalDate())")
    @Mapping(target = "predicted80Percent", expression = "java(phdPosition.predicted80Percent().toLocalDate())")
    @Mapping(target = "currentRemainingProjectTime", expression = "java(phdPosition.currentRemainingProjectTime())")
    @Mapping(target = "program", ignore = true)
    PhDPositionDTO entityToDTO(PhDPosition phdPosition);

   // @Mapping(target = "person.id", source = "personId")
 	@Mapping(target="person", ignore = true)
 	@Mapping(target="progresses", ignore = true)
    PhDPosition dtoToEntity(PhDPositionDTO phdPositionDTO);

  	@Mapping(target="person", ignore = true)
 	@Mapping(target="progresses", ignore = true)
   void updateEntityFromDTO(PhDPositionDTO phdPositionDTO, @MappingTarget PhDPosition phDPosition);
} 