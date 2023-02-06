package io.leego.mock.service;

import io.leego.mock.vo.FileVO;

/**
 * @author Leego Yih
 */
public interface FileService {

    FileVO createFile(String filename, long size, byte[] bytes);

    FileVO getFile(String id);

}
