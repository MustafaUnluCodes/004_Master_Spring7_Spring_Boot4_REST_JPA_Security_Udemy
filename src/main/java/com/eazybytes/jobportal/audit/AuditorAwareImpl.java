package com.eazybytes.jobportal.audit;

import com.eazybytes.jobportal.util.ApplicationUtility;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Returns the current auditor of the application.
     *
     * @return the current auditor.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
//        return Optional.of("AnonymousUser");
        return Optional.of(ApplicationUtility.getLoggedInUser());
    }
}
