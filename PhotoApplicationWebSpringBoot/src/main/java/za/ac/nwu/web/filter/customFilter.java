package za.ac.nwu.web.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class customFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthorFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException
    {
        if (req.getServletPath().equals("/v1/c1/login"))
        {
            filterChain.doFilter(req, res);
        }
        else
        {
            String authorHeader = req.getHeader(AUTHORIZATION);
            if (authorHeader != null && authorHeader.startsWith("Carrier "))
            {
                try
                {
                    authorizingHttpRequestViaAccessKey(req, res, filterChain, authorHeader);
                }
                catch (Exception e)
                {
                    LOGGER.error("An error occurred during the authorization process: {}", e.getMessage());
                    res.setHeader("error", e.getMessage());
                    res.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    res.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(res.getOutputStream(), error);
                }
            }
            else
            {
                filterChain.doFilter(req, res);
            }
        }
    }

    private void authorizingHttpRequestViaAccessKey(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain, String authorHeader) throws IOException, ServletException
    {
        String coin = authorHeader.substring("Carrier ".length());
        Algorithm algorithm = Algorithm.HMAC256("E)H@MbQeThWmZq4t7w!z%C*F-JaNdRfUjXn2r5u8x/A?D(G+KbPeShVkYp3s6v9y".getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(coin);
        String email = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> auths = new ArrayList<>();
        stream(roles).forEach(role ->
        {
            auths.add(new SimpleGrantedAuthority(role));
        });
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, auths);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(req, res);
    }
}
