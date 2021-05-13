package idatt2105.erlinssl.ikhovind.fullstackbooking.util.security;

import idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service.SecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

@Slf4j
@Aspect
@Component
public class UserAspect {
    @Autowired
    private SecurityService securityService;

    @Around("@annotation(userTokenRequired)")
    public void userTokenRequiredWithAnnotation(ProceedingJoinPoint pjp, UserTokenRequired userTokenRequired) throws Throwable {
        JSONObject json = new JSONObject();
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String token = request.getHeader("token");
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Empty token");
            }
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter
                    .parseBase64Binary(securityService.getSecretKey()))
                    .parseClaimsJws(token).getBody();
            if (claims == null || claims.getSubject() == null) {
                throw new IllegalArgumentException("Claim is null");
            }
            String subject = claims.getSubject();
            if (subject.split("=").length != 2) {
                throw new IllegalArgumentException("Invalid token");
            }
        } catch (IllegalArgumentException e) {
            json.put("error", "invalid token");
            log.error("An illegal argument was passed", e);
        } catch (ExpiredJwtException e) {
            json.put("error", "expired token");
            log.error("An expired token was passed");
        } catch (Exception e) {
            json.put("error", "unexpected error");
            log.error("An unexpected error occurred", e);
        }

        pjp.proceed();
    }
}
