package io.leego.mock.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    private String id;
    private String filename;
    private Long size;
    @JsonIgnore
    private byte[] data;
}
