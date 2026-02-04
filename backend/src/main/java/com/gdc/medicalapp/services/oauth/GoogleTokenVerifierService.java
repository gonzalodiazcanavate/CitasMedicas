package com.gdc.medicalapp.services.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    public GoogleTokenVerifierService(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Verifica un token de Google (ID Token o Access Token) y retorna los datos del usuario.
     * Primero intenta verificar como ID Token, si falla, intenta como Access Token.
     *
     * @param token El token recibido de Google Sign-In (puede ser ID Token o Access Token)
     * @return GoogleUserInfo con los datos del usuario si el token es válido
     * @throws InvalidGoogleTokenException si el token es inválido o expirado
     */
    public GoogleUserInfo verifyToken(String token) {
        // Primero intentar verificar como ID Token
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                return extractFromIdToken(idToken);
            }
        } catch (GeneralSecurityException | IOException e) {
            // No es un ID Token válido, intentar como Access Token
        }

        // Intentar como Access Token
        return verifyAccessToken(token);
    }

    /**
     * Extrae información del usuario desde un ID Token verificado
     */
    private GoogleUserInfo extractFromIdToken(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Verificar que el email está verificado
        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new InvalidGoogleTokenException("El email de Google no está verificado");
        }

        return new GoogleUserInfo(
                payload.getSubject(),
                payload.getEmail(),
                (String) payload.get("name"),
                (String) payload.get("picture"),
                (String) payload.get("given_name"),
                (String) payload.get("family_name")
        );
    }

    /**
     * Verifica un Access Token llamando al endpoint userinfo de Google
     */
    private GoogleUserInfo verifyAccessToken(String accessToken) {
        try {
            URL url = new URL(GOOGLE_USERINFO_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new InvalidGoogleTokenException("Token de Google inválido o expirado");
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                String googleId = json.has("sub") ? json.get("sub").getAsString() : null;
                String email = json.has("email") ? json.get("email").getAsString() : null;
                boolean emailVerified = json.has("email_verified") && json.get("email_verified").getAsBoolean();

                if (googleId == null || email == null) {
                    throw new InvalidGoogleTokenException("No se pudo obtener información del usuario de Google");
                }

                if (!emailVerified) {
                    throw new InvalidGoogleTokenException("El email de Google no está verificado");
                }

                return new GoogleUserInfo(
                        googleId,
                        email,
                        json.has("name") ? json.get("name").getAsString() : null,
                        json.has("picture") ? json.get("picture").getAsString() : null,
                        json.has("given_name") ? json.get("given_name").getAsString() : null,
                        json.has("family_name") ? json.get("family_name").getAsString() : null
                );
            }
        } catch (IOException e) {
            throw new InvalidGoogleTokenException("Error verificando token de Google: " + e.getMessage());
        }
    }

    /**
     * Record con los datos del usuario de Google
     */
    public record GoogleUserInfo(
            String googleId,
            String email,
            String name,
            String pictureUrl,
            String givenName,
            String familyName
    ) {}

    /**
     * Excepción para tokens de Google inválidos
     */
    public static class InvalidGoogleTokenException extends RuntimeException {
        public InvalidGoogleTokenException(String message) {
            super(message);
        }
    }
}
