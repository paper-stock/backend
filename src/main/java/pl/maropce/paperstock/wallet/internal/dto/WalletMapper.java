package pl.maropce.paperstock.wallet.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.wallet.internal.Wallet;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {

    WalletDTO toDTO(Wallet wallet);
}
