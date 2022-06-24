package com.github.kbuntrock.sample.rest;

import fr.github.kbuntrock.sample.enpoint.FileUploadController;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Buntrock
 */
@RestController
public class FileUploadControllerImpl implements FileUploadController {

    @Override
    public List<String> uploadFiles(long myId, MultipartFile[] multipartFile) {
        List<String> filenames = new ArrayList<>();
        long i = myId;
        for (MultipartFile file : multipartFile) {
            filenames.add(i + " : " + file.getOriginalFilename());
            i++;
        }
        return filenames;
    }

    @Override
    public String uploadSingleFile(long myId, MultipartFile file) {
        return myId + " : " + file.getOriginalFilename();
    }

    @Override
    public List<String> uploadNonRequiredFiles(long myId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return List.of(myId + " : " + "no file");
        }
        return uploadFiles(myId, files);
    }
}
