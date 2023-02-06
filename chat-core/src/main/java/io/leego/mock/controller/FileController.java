package io.leego.mock.controller;

import io.leego.mock.service.FileService;
import io.leego.mock.vo.FileVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Uploads files.
     *
     * @param files {@link MultipartFile}
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileVO[] uploadFiles(@RequestParam MultipartFile[] files) {
        try {
            FileVO[] res = new FileVO[files.length];
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                res[i] = fileService.createFile(file.getOriginalFilename(), file.getSize(), file.getBytes());
            }
            return res;
        } catch (IOException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Downloads the file with the given ID.
     *
     * @param id must not be {@literal null}.
     */
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void downloadFile(@PathVariable String id, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            FileVO file = fileService.getFile(id);
            String cd = ContentDisposition.attachment().filename(file.getFilename(), StandardCharsets.UTF_8).build().toString();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, cd);
            os.write(file.getData());
        } catch (IOException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
