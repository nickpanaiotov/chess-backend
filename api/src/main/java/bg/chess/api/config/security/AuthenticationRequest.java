package bg.chess.api.config.security;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
    private String grant_type;
}
