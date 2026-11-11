package pl.maropce.paperstock.appUserAccount.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;
import pl.maropce.paperstock.appUserAccount.internal.AppUserAccount;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserAccountMapper {

    AppUserAccountDTO toDTO(AppUserAccount appUserDetails);
}
