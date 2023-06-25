package uk.gov.moj.sdt.idam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Component
public class S2SRepository {

    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public S2SRepository(final AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    public String getS2SToken() {
        return authTokenGenerator.generate();
    }
}
