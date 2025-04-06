package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    public User getUser(Long userId);
}
