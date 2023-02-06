package io.leego.mock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldNameConstants
@Entity
@Table(name = "test_file")
public class File {
    @Id
    private String id;
    @Column(nullable = true, updatable = false)
    private String filename;
    @Column(nullable = true, updatable = false)
    private Long size;
    @Column(nullable = false, updatable = false)
    private byte[] data;
}
