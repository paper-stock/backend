package pl.maropce.paperstock.position.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.position.PositionDTO;
import pl.maropce.paperstock.position.internal.Position;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PositionMapper {
    PositionDTO toDTO(Position position);
}
