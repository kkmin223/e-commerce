package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> getUser(Long userId) {
        return null;
    }
}
