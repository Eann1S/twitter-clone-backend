package test_util.model;

import com.example.profile.dto.request.CreateProfileRequest;
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
}
