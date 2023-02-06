package io.leego.mock.repository;

import io.leego.mock.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

/**
 * @author Leego Yih
 */
public interface UserRepository extends CrudRepository<User, Long> {

    @Nullable
    User findByUsername(String username);

    boolean existsByUsername(String username);

}
