package cn.deru.backend.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-endpoint}")
    private String publicEndpoint;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file, String folder) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return publicEndpoint + "/" + bucketName + "/" + fileName;
    }

    public void deleteFile(String fileUrl) throws Exception {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String prefix = publicEndpoint + "/" + bucketName + "/";
        if (fileUrl.startsWith(prefix)) {
            String objectName = fileUrl.substring(prefix.length());
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
    }
}
