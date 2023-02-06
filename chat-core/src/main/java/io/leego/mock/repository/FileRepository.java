package io.leego.mock.repository;

import io.leego.mock.entity.File;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Leego Yih
 */
public interface FileRepository extends CrudRepository<File, String> {
}
