package se.uu.ebc.bemanning.config;

import java.io.IOException;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Redirects Vaadin route URLs that carry a trailing slash to their canonical
 * (no-slash) form.
 *
 * <p>Vaadin Flow emits a relative {@code <base href=".">} into its bootstrap
 * page. When a route is opened with a trailing slash (e.g. {@code /courses/}),
 * the browser resolves that relative base to {@code /courses/}, so the client
 * requests its framework endpoints (UIDL, heartbeat, dev-tools/push) under
 * {@code /courses/VAADIN/...} instead of {@code /VAADIN/...}. The server then
 * treats it as a different context and keeps spinning up fresh UIs, which the
 * browser surfaces as "Connection lost, trying to reconnect".
 * See <a href="https://github.com/vaadin/flow/issues/17018">vaadin/flow#17018</a>.
 *
 * <p>The fix is to canonicalize the URL: a GET to {@code /courses/} is
 * redirected (308, preserving method/semantics) to {@code /courses}. Only
 * top-level route paths are touched; framework/static/API paths are left alone
 * so this cannot interfere with {@code /VAADIN/**}, {@code /api/**},
 * {@code /rest/**} or the root {@code /}.
 */
@Configuration
@Slf4j
public class TrailingSlashRedirectConfig {

    @Bean
    public FilterRegistrationBean<Filter> vaadinTrailingSlashRedirectFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TrailingSlashRedirectFilter());
        registration.addUrlPatterns("/*");
        // Run early, before the Vaadin forwarding happens.
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("vaadinTrailingSlashRedirectFilter");
        return registration;
    }

    static class TrailingSlashRedirectFilter implements Filter {

        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            String uri = request.getRequestURI();
            String context = request.getContextPath();
            // Path within the app, without the context path.
            String path = uri.substring(context.length());

            if (shouldRedirect(request.getMethod(), path)) {
                String target = context + path.substring(0, path.length() - 1);
                String query = request.getQueryString();
                if (query != null && !query.isEmpty()) {
                    target = target + "?" + query;
                }
                log.debug("Redirecting trailing-slash route {} -> {}", uri, target);
                // 308 keeps the HTTP method and body semantics intact.
                response.setStatus(HttpServletResponse.SC_PERMANENT_REDIRECT);
                response.setHeader("Location", target);
                return;
            }

            chain.doFilter(req, res);
        }

        private boolean shouldRedirect(String method, String path) {
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                return false;
            }
            // Only a single trailing slash on a non-root path.
            if (path == null || path.length() < 2 || !path.endsWith("/")) {
                return false;
            }
            // Leave framework, static-resource and API paths untouched.
            if (path.startsWith("/VAADIN/")
                    || path.startsWith("/api/")
                    || path.startsWith("/rest/")
                    || path.startsWith("/view/")
                    || path.startsWith("/files/")
                    || path.startsWith("/actuator/")) {
                return false;
            }
            // Don't touch requests that look like a static file (contain a dot in
            // the last segment), e.g. /something/app.css/ is unusual but a plain
            // file path should be left as-is.
            return true;
        }
    }
}
