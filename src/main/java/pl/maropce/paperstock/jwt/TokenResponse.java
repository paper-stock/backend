package pl.maropce.paperstock.jwt;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
