package com.example.profile.mapper;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ProfileMapper.class}
)
public interface PageMapper {

    PageResponse<ProfileResponse> mapToPageResponse(Page<Profile> page);
}
