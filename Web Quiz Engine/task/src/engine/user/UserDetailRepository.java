package engine.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserData, Integer> {
    Optional<UserData> findByEmail(String email);
}
