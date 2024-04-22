package com.example.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Document(collection = "profiles")
public class Profile implements BaseEntity<String> {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String username;

    private LocalDate joinDate;

    private String bio;

    private String location;

    private String website;

    private LocalDate birthDate;

}
