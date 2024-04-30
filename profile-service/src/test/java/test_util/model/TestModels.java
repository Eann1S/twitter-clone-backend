package test_util.model;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;
import static test_util.constant.GlobalConstants.TEST_EMAIL;
import static test_util.constant.GlobalConstants.TEST_USERNAME;

public class TestModels {

    public static Model<CreateProfileRequest> VALID_CREATE_REQUEST_MODEL = Instancio.of(CreateProfileRequest.class)
            .generate(field(CreateProfileRequest::email), gen -> gen.oneOf(TEST_EMAIL))
            .generate(field(CreateProfileRequest::username), gen -> gen.oneOf(TEST_USERNAME))
            .generate(field(CreateProfileRequest::joinDate), gen -> gen.temporal().localDate())
            .toModel();

    public static Model<UpdateProfileRequest> VALID_UPDATE_REQUEST_MODEL = Instancio.of(UpdateProfileRequest.class)
            .generate(field(UpdateProfileRequest::username), gen -> gen.oneOf(TEST_USERNAME))
            .generate(field(UpdateProfileRequest::bio), gen -> gen.string().maxLength(160))
            .generate(field(UpdateProfileRequest::location), gen -> gen.string().maxLength(30))
            .generate(field(UpdateProfileRequest::website), gen -> gen.string().maxLength(100))
            .generate(field(UpdateProfileRequest::birthDate), gen -> gen.temporal().localDate().past())
            .toModel();
}
