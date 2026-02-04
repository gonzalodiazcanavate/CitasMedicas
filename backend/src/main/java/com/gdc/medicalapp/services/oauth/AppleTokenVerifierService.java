package com.gdc.medicalapp.services.oauth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AppleTokenVerifierService {

    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final String clientId;

    // Cache de las claves públicas de Apple
    private final Map<String, PublicKey> publicKeyCache = new ConcurrentHashMap<>();
    private long lastKeyFetch = 0;
    private static final long KEY_CACHE_TTL = 24 * 60 * 60 * 1000; // 24 horas

    public AppleTokenVerifierService(@Value("${apple.client-id:}") String clientId) {
        this.clientId = clientId;
    }

    /**
     * Verifica un ID Token de Apple y retorna los datos del usuario.
     *
     * @param idToken El token JWT recibido de Sign in with Apple
     * @return AppleUserInfo con los datos del usuario si el token es válido
     * @throws InvalidAppleTokenException si el token es inválido o expirado
     */
    public AppleUserInfo verifyToken(String idToken) {
        return verifyToken(idToken, null, null);
    }

    /**
     * Verifica un ID Token de Apple con nombre opcional (solo disponible en primer login).
     *
     * @param idToken   El token JWT recibido de Sign in with Apple
     * @param firstName Nombre (solo en primer login, puede ser null)
     * @param lastName  Apellido (solo en primer login, puede ser null)
     * @return AppleUserInfo con los datos del usuario si el token es válido
     * @throws InvalidAppleTokenException si el token es inválido o expirado
     */
    public AppleUserInfo verifyToken(String idToken, String firstName, String lastName) {
        if (clientId == null || clientId.isBlank()) {
            throw new InvalidAppleTokenException("Apple Sign In no está configurado. Configure apple.client-id en application.properties");
        }

        try {
            // 1. Decodificar header para obtener el kid (Key ID)
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new InvalidAppleTokenException("Formato de token inválido");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonObject header = JsonParser.parseString(headerJson).getAsJsonObject();
            String kid = header.get("kid").getAsString();

            // 2. Obtener la clave pública de Apple
            PublicKey publicKey = getApplePublicKey(kid);

            // 3. Verificar y decodificar el token (compatible con jjwt 0.11.x)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(APPLE_ISSUER)
                    .requireAudience(clientId)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

            // 4. Extraer información del usuario
            String appleUserId = claims.getSubject();
            String email = claims.get("email", String.class);
            Boolean emailVerified = claims.get("email_verified", Boolean.class);
            Boolean isPrivateEmail = claims.get("is_private_email", Boolean.class);

            if (appleUserId == null) {
                throw new InvalidAppleTokenException("No se pudo obtener el ID de usuario de Apple");
            }

            // Construir nombre completo si está disponible
            String fullName = null;
            if (firstName != null || lastName != null) {
                fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
                if (fullName.isEmpty()) {
                    fullName = null;
                }
            }

            return new AppleUserInfo(
                    appleUserId,
                    email,
                    fullName,
                    firstName,
                    lastName,
                    emailVerified != null && emailVerified,
                    isPrivateEmail != null && isPrivateEmail
            );

        } catch (InvalidAppleTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidAppleTokenException("Error verificando token de Apple: " + e.getMessage());
        }
    }

    /**
     * Obtiene la clave pública de Apple para verificar tokens
     */
    private PublicKey getApplePublicKey(String kid) {
        // Verificar cache
        if (publicKeyCache.containsKey(kid) && (System.currentTimeMillis() - lastKeyFetch) < KEY_CACHE_TTL) {
            return publicKeyCache.get(kid);
        }

        // Obtener claves de Apple
        refreshApplePublicKeys();

        PublicKey key = publicKeyCache.get(kid);
        if (key == null) {
            throw new InvalidAppleTokenException("No se encontró la clave pública de Apple para kid: " + kid);
        }

        return key;
    }

    /**
     * Descarga y cachea las claves públicas de Apple
     */
    private synchronized void refreshApplePublicKeys() {
        try {
            URI uri = URI.create(APPLE_KEYS_URL);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                throw new InvalidAppleTokenException("No se pudieron obtener las claves públicas de Apple");
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray keys = response.getAsJsonArray("keys");

                publicKeyCache.clear();

                for (int i = 0; i < keys.size(); i++) {
                    JsonObject keyData = keys.get(i).getAsJsonObject();
                    String kid = keyData.get("kid").getAsString();
                    String n = keyData.get("n").getAsString();
                    String e = keyData.get("e").getAsString();

                    PublicKey publicKey = createRSAPublicKey(n, e);
                    publicKeyCache.put(kid, publicKey);
                }

                lastKeyFetch = System.currentTimeMillis();
            }
        } catch (Exception e) {
            throw new InvalidAppleTokenException("Error obteniendo claves públicas de Apple: " + e.getMessage());
        }
    }

    /**
     * Crea una clave pública RSA a partir de los componentes n y e
     */
    private PublicKey createRSAPublicKey(String modulusBase64, String exponentBase64) throws Exception {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulusBase64);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponentBase64);

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePublic(spec);
    }

    /**
     * Record con los datos del usuario de Apple
     */
    public record AppleUserInfo(
            String appleId,
            String email,
            String name,
            String firstName,
            String lastName,
            boolean emailVerified,
            boolean isPrivateEmail
    ) {}

    /**
     * Excepción para tokens de Apple inválidos
     */
    public static class InvalidAppleTokenException extends RuntimeException {
        public InvalidAppleTokenException(String message) {
            super(message);
        }
    }
}
