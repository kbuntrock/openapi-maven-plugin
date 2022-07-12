package com.github.kbuntrock.resources.endpoint.file;

import com.github.kbuntrock.resources.Constants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Api handling any type of file upload.
 * From basic to complicated stuff.
 *
 * @author kbuntrock
 */
@RequestMapping(path = Constants.BASE_API + "/file-upload")
public interface FileUploadController {

    /**
     * Upload des fichiers et renvoi leur noms
     *
     * @param myId  id sous lequel sauvegarder ces fichiers
     * @param files fichiers à sauvegarder
     * @return liste des noms de fichier
     */
    @PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<String> uploadFiles(@RequestParam long myId, @RequestParam(name = "files") MultipartFile[] files);

    /**
     * Upload d'un fichier seul
     *
     * @param myId
     * @param file
     * @return
     */
    @PostMapping(path = "single-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadSingleFile(@RequestParam long myId, @RequestParam(name = "file") MultipartFile file);

    /**
     * Upload de fichier mais le fichier n'est pas requis ...
     *
     * @param myId
     * @param files
     * @return
     */
    @PostMapping(path = "non-required-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> uploadNonRequiredFiles(@RequestParam long myId, @RequestParam(name = "files", required = false) MultipartFile[] files);
}
