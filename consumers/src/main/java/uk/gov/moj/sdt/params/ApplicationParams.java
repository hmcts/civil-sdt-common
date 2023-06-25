package uk.gov.moj.sdt.params;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

@Named
@Singleton
public class ApplicationParams {

    @Value("${idam.sdt.username}")
    private String sdtSystemUserId;
    @Value("${idam.sdt.password}")
    private String sdtSystemUserPassword;

    public String getSdtSystemUserId() {
        return sdtSystemUserId;
    }

    public String getSdtSystemUserPassword() {
        return sdtSystemUserPassword;
    }

}
