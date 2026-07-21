package pl.maropce.paperstock.auth.internal;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.auth.internal.dto.RegisterUserRequest;


@Component
@AllArgsConstructor
public class DataInitializer {

    private final AuthService authService;

    @PostConstruct
    public void initialize() {
       authService.register(new RegisterUserRequest(
               "admin@gmail.com",
               "password",
               "Admin"
       ));

        authService.register(new RegisterUserRequest(
                "user@gmail.com",
                "password",
                "User"
        ));
    }
}
