package uk.gov.moj.sdt.idam;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.moj.sdt.params.ApplicationParams;

@Component
public class IdamRepository {

    private final IdamClient idamClient;
    private ApplicationParams applicationParams;

    @Autowired
    public IdamRepository(IdamClient idamClient,
                          ApplicationParams applicationParams) {
        this.idamClient = idamClient;
        this.applicationParams = applicationParams;
    }

    @Cacheable("userInfoCache")
    public UserInfo getUserInfo(String bearerToken) {
        return idamClient.getUserInfo(bearerToken);
    }

    @Cacheable("sdtAccessTokenCache")
    public String getSdtSystemUserAccessToken() {
        return idamClient.getAccessToken(applicationParams.getSdtSystemUserId(), applicationParams.getSdtSystemUserPassword());
    }

    public UserDetails getUserByUserId(String userId, String bearerToken) {
        return idamClient.getUserByUserId(bearerToken, userId);
    }
}
