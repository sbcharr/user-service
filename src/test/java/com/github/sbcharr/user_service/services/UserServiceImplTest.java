// language: java
package com.github.sbcharr.user_service.services;

import com.github.sbcharr.user_service.exceptions.InvalidCredentialsException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import com.github.sbcharr.user_service.models.Role;
import com.github.sbcharr.user_service.models.RoleName;
import com.github.sbcharr.user_service.models.Token;
import com.github.sbcharr.user_service.models.User;
import com.github.sbcharr.user_service.repositories.RoleRepository;
import com.github.sbcharr.user_service.repositories.TokenRepository;
import com.github.sbcharr.user_service.repositories.UserRepository;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Use a real SecretKey for HS256/HS512 usage in tests
    private SecretKey secretKey;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        // generate a sufficiently long HMAC key (32+ bytes)
        secretKey = Keys.hmacShaKeyFor("test-secret-key-that-is-long-enough-0123456789".getBytes(StandardCharsets.UTF_8));

        // ensure @Value fields are set for behavior that depends on them
        ReflectionTestUtils.setField(service, "jwtExpiryMinutes", 30);
        ReflectionTestUtils.setField(service, "jwtIssuer", "test-issuer");
        ReflectionTestUtils.setField(service, "jwtBlacklistPrefix", "blk:");

        // inject the real secretKey into the service to avoid UnsupportedKeyException
        ReflectionTestUtils.setField(service, "secretKey", secretKey);
    }

    @Test
    void register_success_persistsUserWithHashedPasswordAndRole() {
        String email = "test@example.com";
        String rawPassword = "password";
        String hashed = "hashed-password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Role buyer = new Role();
        buyer.setName(RoleName.BUYER);
        when(roleRepository.findByName(RoleName.BUYER)).thenReturn(Optional.of(buyer));

        when(bCryptPasswordEncoder.encode(rawPassword)).thenReturn(hashed);

        User savedUser = new User();
        savedUser.setId(42L);
        savedUser.setEmail(email);
        savedUser.setName("Test");
        savedUser.setHashedPassword(hashed);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = service.register("Test", email, rawPassword);

        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        assertThat(captured.getEmail()).isEqualTo(email);
        assertThat(captured.getHashedPassword()).isEqualTo(hashed);
        assertThat(captured.getRoles()).isNotEmpty();
        assertThat(result.getId()).isEqualTo(42L);
    }

    @Test
    void register_duplicateEmail_throws() {
        String email = "dup@example.com";
        User existing = new User();
        existing.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existing));

        assertThrows(UserAlreadyExistsException.class,
                () -> service.register("Name", email, "pw"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success_createsAndSavesToken() {
        String email = "login@example.com";
        String raw = "pw";
        String hashed = "hashed";
        User user = new User();
        user.setId(7L);
        user.setEmail(email);
        user.setHashedPassword(hashed);

        when(userRepository.findByEmailAndDeletedAtIsNull(email)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(raw, hashed)).thenReturn(true);

        // Return the same Token instance passed to save(...) so the test sees the generated JWT value
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Token result = service.login(email, raw);

        verify(tokenRepository).save(any(Token.class));
        // Token value should be a JWT: three dot-separated base64 parts
        assertThat(result.getValue()).matches("[^.]+\\.[^.]+\\.[^.]+");
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getExpiryAt()).isAfter(Instant.now());
    }

    @Test
    void login_badPassword_throwsInvalidCredentials() {
        String email = "login2@example.com";
        User user = new User();
        user.setEmail(email);
        user.setHashedPassword("hashed");

        when(userRepository.findByEmailAndDeletedAtIsNull(email)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> service.login(email, "wrong"));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void login_userNotFound_throwsInvalidCredentials() {
        String email = "nouser@example.com";
        when(userRepository.findByEmailAndDeletedAtIsNull(email)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> service.login(email, "pw"));
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void findById_returnsWhenPresent() {
        User u = new User();
        u.setId(13L);
        when(userRepository.findByIdAndDeletedAtIsNull(13L)).thenReturn(Optional.of(u));

        Optional<User> res = service.findById(13L);

        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(13L);
    }

    @Test
    void findById_emptyWhenNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        Optional<User> res = service.findById(99L);

        assertThat(res).isEmpty();
    }
}