package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> getUser(Long userId);
    User save(User user);
}
