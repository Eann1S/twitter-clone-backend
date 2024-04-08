package com.example.profile.config.service;

import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.CacheService;
import com.example.profile.service.ProfileService;
import com.example.profile.service.impl.ProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProfileServiceConfig {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Bean
    public ProfileService profileService(@Qualifier("profiles") CacheService cacheService) {
        return new ProfileServiceImpl(profileRepository, profileMapper, cacheService);
    }
}
