package uk.gov.moj.sdt.dao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("uk.gov.moj.sdt.dao.repository")
@EntityScan("uk.gov.moj.sdt.domain")
public class DaoTestConfig {
}
