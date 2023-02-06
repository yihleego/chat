package io.leego.mock.service.impl;

import io.leego.mock.entity.File;
import io.leego.mock.repository.FileRepository;
import io.leego.mock.service.FileService;
import io.leego.mock.vo.FileVO;
import io.leego.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Leego Yih
 */
@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public FileVO createFile(String filename, long size, byte[] bytes) {
        File file = new File(SecurityUtils.randomToken(), filename, size, bytes);
        fileRepository.save(file);
        file.setData(null);
        return toVO(file);
    }

    @Override
    public FileVO getFile(String id) {
        return fileRepository.findById(id)
                .map(this::toVO)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private FileVO toVO(File o) {
        return new FileVO(o.getId(), o.getFilename(), o.getSize(), o.getData());
    }
}
