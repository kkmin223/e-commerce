package kr.hhplus.be.server.infrastructure.user;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT u from User u WHERE u.id = :id")
    Optional<User> findByIdWithOptimisticLock(@Param("id") Long userId);
}
