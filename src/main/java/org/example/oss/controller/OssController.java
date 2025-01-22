package org.example.oss.controller;

import org.example.oss.model.Oss;
import org.example.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic) {
        return ossService.uploadFile(file, isPublic);
    }

    @GetMapping(value = "/download/{objectName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadFile(@PathVariable String objectName) {
        return ossService.downloadFile(objectName);
    }

    @GetMapping("/files")
    public List<Oss> listFiles() {
        return ossService.listFiles();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id) {
        return ossService.deleteFile(id);
    }
}