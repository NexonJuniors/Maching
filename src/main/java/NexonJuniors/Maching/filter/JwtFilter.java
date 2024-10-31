package NexonJuniors.Maching.filter;

import NexonJuniors.Maching.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
//@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String VALID_AUTH_HEADER_START = "Bearer ";
    private static final String NOT_VALID_TOKEN_MESSAGE = "유효하지 않은 토큰";
    private final static String REFRESH_TOKEN_ISSUER = "REF_MACHING";
    private static final Integer ACCESS_TOKEN_TIME = 1800;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isExistAuthHeader(authHeader)) {
            String accessToken = extractAccessToken(authHeader);
            String refreshToken = extractRefreshToken(request.getCookies());

            if (!isValidateToken(accessToken) || !isValidateToken(refreshToken) && isExpiredToken(refreshToken) && isValidIssuer(refreshToken)) {
                log.error(NOT_VALID_TOKEN_MESSAGE);
                logout(response);
            }
            else {
                if (isExpiredToken(accessToken)) {
                    // 토큰 재발급
                    setNewAccessToken(response, reIssueAccessToken(accessToken));
                }

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                SecurityContextHolder.setContext(securityContext);
            }
        }

        filterChain.doFilter(request, response);
    }


    private boolean isExistAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith(VALID_AUTH_HEADER_START);
    }

    private String extractAccessToken(String authHeader) {
        return authHeader.split(" ")[1];
    }

    private String extractRefreshToken(Cookie[] cookies) {
        String refreshToken = null;

        if(cookies == null) return refreshToken;

        for (Cookie cookie : cookies) {
            if (cookie.isHttpOnly() && cookie.getName().equals(REFRESH_TOKEN)) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        return refreshToken;
    }

    private boolean isValidateToken(String token) {
        return jwtUtil.isValidate(token);
    }

    private boolean isExpiredToken(String token) {
        return jwtUtil.isExpired(token);
    }

    private boolean isValidIssuer(String token){
        Claims claims = jwtUtil.parseClaims(token);

        return claims.getIssuer().equals(REFRESH_TOKEN_ISSUER);
    }

    private void logout(HttpServletResponse response) {
        response.addCookie(deleteToken(ACCESS_TOKEN));
        response.addCookie(deleteToken(REFRESH_TOKEN));
    }

    private Cookie deleteToken(String token) {
        Cookie cookie = new Cookie(token, null);
        cookie.setMaxAge(0);

        return cookie;
    }

    private String reIssueAccessToken(String token){
        Claims claims = jwtUtil.parseClaims(token);

        return jwtUtil.generateToken(Long.parseLong(claims.getId()), ACCESS_TOKEN_TIME);
    }

    private void setNewAccessToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        response.addCookie(cookie);
    }
}


