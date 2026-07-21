package pl.maropce.paperstock.appUser.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.internal.AppUser;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserMapper {
    AppUserDTO toDTO(AppUser appUser);
}
