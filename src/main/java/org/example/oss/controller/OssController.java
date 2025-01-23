package org.example.oss.controller;

import org.example.oss.model.Oss;
import org.example.oss.service.OssService;
import org.example.oss.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic) {
        return ossService.uploadFile(file, isPublic);
    }

    @GetMapping(value = "/download/{objectName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadFile(@PathVariable String objectName) {
        Map<String, Object> response = ossService.downloadFile(objectName);
        if ((int) response.get("code") == 200) {
            return (byte[]) response.get("data");
        } else {
            throw new RuntimeException((String) response.get("msg"));
        }
    }

    @GetMapping("/files")
    public List<Oss> listFiles() {
        return ossService.listFiles();
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteFile(@PathVariable Long id) {
        return ossService.deleteFile(id);
    }
}