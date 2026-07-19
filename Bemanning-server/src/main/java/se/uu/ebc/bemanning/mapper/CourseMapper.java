package se.uu.ebc.bemanning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import se.uu.ebc.bemanning.dto.CourseDTO;
import se.uu.ebc.bemanning.entity.course.Course;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CourseMapper {

    PhDPositionMapper INSTANCE = Mappers.getMapper (PhDPositionMapper.class);

    CourseDTO entityToDTO(Course entity);

   // @Mapping(target = "person.id", source = "personId")
 	@Mapping(target="courseInstances", ignore = true)
    Course dtoToEntity(CourseDTO dto);

 	@Mapping(target="courseInstances", ignore = true)
	void updateEntityFromDTO(CourseDTO dto, @MappingTarget Course entity);
} 