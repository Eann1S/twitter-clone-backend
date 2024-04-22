package com.example.profile.repository;

import com.example.profile.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    void deleteByFollowerProfile_IdAndFolloweeProfile_Id(String followerId, String followeeId);

    boolean existsByFollowerProfile_IdAndFolloweeProfile_Id(String followerId, String followeeId);

    Page<Follow> findAllByFollowerProfile_Id(String followerId, Pageable pageable);

    Page<Follow> findAllByFolloweeProfile_Id(String followeeId, Pageable pageable);

    Integer countAllByFollowerProfile_Id(String followerId);

    Integer countAllByFolloweeProfile_Id(String followeeId);
}
