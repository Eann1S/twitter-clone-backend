package com.example.profile.mapper;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class PageMapperTest {

    @Mock
    private ProfileMapper profileMapper;
    private PageMapper pageMapper;

    @BeforeEach
    void setUp() {
        pageMapper = new PageMapperImpl(profileMapper);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldMapPageToPageResponse(Profile profile, ProfileResponse profileResponse) {
        when(profileMapper.toResponse(profile))
                .thenReturn(profileResponse);
        Page<Profile> page = new PageImpl<>(List.of(profile));

        PageResponse<ProfileResponse> actualResponse = pageMapper.mapProfilesToPageResponse(page);

        assertThat(actualResponse.getContent()).containsExactly(profileResponse);
    }
}