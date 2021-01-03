package bg.chess.api.controller;

import bg.chess.api.config.security.AuthenticationRequest;
import bg.chess.api.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/token")
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody Mono<AuthenticationRequest> authRequest) {
        return authRequest
                .flatMap(login -> authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .map(auth -> tokenProvider.createToken(auth))
                )
                .map(jwt -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                            var tokenBody = Map.of("id_token", jwt);
                            return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                        }
                );
    }

//    @PostMapping("/token")
//    public Mono<ResponseEntity> login(ServerWebExchange exchange) {
//        Mono<MultiValueMap<String, String>> data = exchange.getFormData();
//        return authRequest
//                .flatMap(login -> authenticationManager
//                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
//                        .map(auth -> tokenProvider.createToken(auth))
//                )
//                .map(jwt -> {
//                            HttpHeaders httpHeaders = new HttpHeaders();
//                            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
//                            var tokenBody = Map.of("id_token", jwt);
//                            return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
//                        }
//                );
//
//        return null;
//    }
}
