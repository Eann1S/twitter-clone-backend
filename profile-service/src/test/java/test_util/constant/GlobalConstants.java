package test_util.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum GlobalConstants {

    PROFILE_EMAIL("new_profile@gmail.com"),
    USERNAME("dummy username"),
    ID("dummy id");

    private final String constant;
}
