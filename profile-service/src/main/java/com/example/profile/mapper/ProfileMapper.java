package com.example.profile.mapper;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.qualifier.followees.FolloweesCountQualifier;
import com.example.profile.mapper.qualifier.followees.FolloweesForProfile;
import com.example.profile.mapper.qualifier.followers.FollowersCountQualifier;
import com.example.profile.mapper.qualifier.followers.FollowersForProfile;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {FolloweesCountQualifier.class, FollowersCountQualifier.class}
)
public interface ProfileMapper {

    Profile toProfile(CreateProfileRequest createProfileRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "followees", source = "id", qualifiedBy = FolloweesForProfile.class)
    @Mapping(target = "followers", source = "id", qualifiedBy = FollowersForProfile.class)
    ProfileResponse toResponse(Profile profile);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", ignore = true),
            @Mapping(target = "joinDate", ignore = true)
    })
    Profile updateProfileFromUpdateProfileRequest(UpdateProfileRequest updateProfileRequest, @MappingTarget Profile profile);
}
