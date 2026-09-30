package smartclinic.project.backend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import smartclinic.project.backend.repositories.AdminRepository;
import smartclinic.project.backend.repositories.DoctorRepository;
import smartclinic.project.backend.repositories.PatientRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    @Value("${jwt.secret:mySecretKeyForSmartClinicProject12345678901234567890}")
    private String jwtSecret;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Отримує секретний ключ для підпису JWT токенів.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерує JWT токен для ідентифікатора користувача (термін дії — 7 днів).
     */
    public String generateToken(String identifier) {
        long expirationTimeMs = 7L * 24 * 60 * 60 * 1000; // 7 днів у мілісекундах

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Перевантажений метод для зручності генерації токена з додатковими параметрами (наприклад, id чи role).
     */
    public String generateToken(Long id, String identifier, String role) {
        long expirationTimeMs = 7L * 24 * 60 * 60 * 1000;

        return Jwts.builder()
                .setSubject(identifier)
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Витягує ідентифікатор (subject: email або username) з JWT токена.
     */
    public String extractIdentifier(String token) {
        try {
            // Очищення токена від префіксу Bearer, якщо він є
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Аліас для розширеної сумісності (витягує email/identifier).
     */
    public String getEmailFromToken(String token) {
        return extractIdentifier(token);
    }

    /**
     * Перевіряє валідність токена для конкретного типу користувача (admin, doctor, patient).
     */
    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null || identifier.isEmpty()) {
                return false;
            }

            if ("admin".equalsIgnoreCase(user)) {
                return adminRepository.findByUsername(identifier) != null;
            } else if ("doctor".equalsIgnoreCase(user)) {
                return doctorRepository.findByEmail(identifier) != null;
            } else if ("patient".equalsIgnoreCase(user)) {
                return patientRepository.findByEmail(identifier) != null;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}