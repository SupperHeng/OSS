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

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

@Service
public class OssService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private OssRepository ossRepository;

    public String uploadFile(MultipartFile file, boolean isPublic) {
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

            return oss.getFilePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed due to IO error.";
        } catch (Exception e) {
            e.printStackTrace();
            return "File upload failed.";
        }
    }

    public byte[] downloadFile(String objectName) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("stage")
                        .object(objectName)
                        .build()
        )) {
            return stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Oss> listFiles() {
        return ossRepository.findAll();
    }

    public String deleteFile(Long id) {
        Oss oss = ossRepository.findById(id).orElse(null);
        if (oss == null) {
            return "File not found.";
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("stage")
                            .object(oss.getFileName())
                            .build()
            );
            ossRepository.delete(oss);
            return "File deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "File deletion failed.";
        }
    }

    public List<Oss> getAllOss() {
        return ossRepository.findAll();
    }

    public Oss saveOss(Oss oss) {
        return ossRepository.save(oss);
    }
}