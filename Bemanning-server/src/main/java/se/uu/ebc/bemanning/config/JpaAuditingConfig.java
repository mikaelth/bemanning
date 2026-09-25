package se.uu.ebc.bemanning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import se.uu.ebc.bemanning.security.AuditorAwareImpl;

/**
 * Enables Spring Data JPA auditing so that the {@code @CreatedDate},
 * {@code @CreatedBy}, {@code @LastModifiedDate} and {@code @LastModifiedBy}
 * fields on {@link se.uu.ebc.bemanning.entity.Auditable} are populated on
 * persist/update.
 *
 * <p>{@code Auditable} is already annotated with
 * {@code @EntityListeners(AuditingEntityListener.class)}, but that listener only
 * fills the audit fields when the auditing infrastructure is active. Two pieces
 * were missing, which is why {@code created_by}, {@code creation_date},
 * {@code modified_by} and {@code modification_date} were left null:
 * <ol>
 *   <li>{@code @EnableJpaAuditing} was not present anywhere (it was commented
 *       out on the application class), so the listener was never wired up.</li>
 *   <li>{@link AuditorAwareImpl} was never registered as a bean, so there was no
 *       {@code auditorProvider} for {@code @CreatedBy}/{@code @LastModifiedBy}
 *       to resolve the current user.</li>
 * </ol>
 *
 * The {@code auditorAwareRef} below matches the {@link #auditorProvider()} bean
 * name.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
