package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.AccountRequest;
import com.bobcarrental.dto.response.AccountResponse;
import com.bobcarrental.model.Account;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Account entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recd", source = "amount")
    @Mapping(target = "bill", expression = "java(java.math.BigDecimal.ZERO)")
    Account toEntity(AccountRequest request);
    
    AccountResponse toResponse(Account entity);
    
    List<AccountResponse> toResponseList(List<Account> entities);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recd", source = "amount")
    void updateEntityFromRequest(AccountRequest request, @MappingTarget Account entity);
}

// Made with Bob
