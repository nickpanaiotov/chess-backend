package bg.chess.api.controller;

import bg.chess.api.config.security.AuthenticationRequest;
import bg.chess.api.config.security.JwtTokenProvider;
import bg.chess.api.model.User;
import bg.chess.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/token")
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody Mono<AuthenticationRequest> authRequest) {
        return authRequest
                .flatMap(login -> authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .map(tokenProvider::createToken)
                )
                .map(jwt -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                            var tokenBody = Map.of("id_token", jwt);
                            return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                        }
                );
    }

    @ApiIgnore
    @GetMapping("/token")
    public Mono<ResponseEntity<Map<String, String>>> token(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return Mono.error(new ResponseStatusException(UNAUTHORIZED));
        }

        return this.userService.getUser(user.getAttribute("login"))
                .map(tokenProvider::createToken).map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("id_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                }
        );
    }

    @ApiIgnore
    @GetMapping("/info")
    public Mono<User> userInfo(@AuthenticationPrincipal UserDetails user) {
        return this.userService.getUser(user.getUsername());
    }
}
