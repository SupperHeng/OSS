package org.example.oss.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import org.example.oss.model.Oss;
import org.example.oss.repository.OssRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.oss.util.ResponseUtil;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class OssService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private OssRepository ossRepository;

    public Map<String, Object> uploadFile(MultipartFile file, boolean isPublic) {
        String fileName = file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("stage")
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            Oss oss = new Oss();
            oss.setFileName(fileName);
            oss.setFilePath("http://localhost:9000/stage/" + fileName);
            oss.setIsPublic(isPublic);
            ossRepository.save(oss);

            return ResponseUtil.createResponse(200, oss.getFilePath(), "File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.createResponse(500, null, "File upload failed due to IO error.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.createResponse(500, null, "File upload failed.");
        }
    }

    public Map<String, Object> downloadFile(String objectName) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("stage")
                        .object(objectName)
                        .build()
        )) {
            return ResponseUtil.createResponse(200, stream.readAllBytes(), "File downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.createResponse(500, null, "File download failed due to IO error.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.createResponse(500, null, "File download failed.");
        }
    }

    public List<Oss> listFiles() {
        return ossRepository.findAll();
    }

    public Map<String, Object> deleteFile(Long id) {
        Oss oss = ossRepository.findById(id).orElse(null);
        if (oss == null) {
            return ResponseUtil.createResponse(404, null, "File not found.");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("stage")
                            .object(oss.getFileName())
                            .build()
            );
            ossRepository.delete(oss);
            return ResponseUtil.createResponse(200, null, "File deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.createResponse(500, null, "File deletion failed.");
        }
    }

    public List<Oss> getAllOss() {
        return ossRepository.findAll();
    }

    public Oss saveOss(Oss oss) {
        return ossRepository.save(oss);
    }
}