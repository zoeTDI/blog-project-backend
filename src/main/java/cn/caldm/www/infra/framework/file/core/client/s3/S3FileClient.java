package cn.caldm.www.infra.framework.file.core.client.s3;

import cn.caldm.www.infra.framework.file.core.client.AbstractFileClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;

/**
 * 通用 S3 协议对象存储客户端
 * 完美支持 阿里云 OSS、腾讯云 COS、七牛云 Kodo、华为云 OBS、MinIO 等全部主流对象存储
 *
 * @author caldm
 */
public class S3FileClient extends AbstractFileClient<S3FileClientConfig> {
    private S3Client client;

    public S3FileClient(Long id, S3FileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() throws Exception {
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(config.getEnablePathStyleAccess())
                .build();

        String endpoint = config.getEndpoint();
        if (endpoint != null && !endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
            endpoint = "https://" + endpoint;
        }

        this.client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getAccessSecret())
                ))
                .endpointOverride(URI.create(endpoint))
                .region(config.getRegion() != null ? Region.of(config.getRegion()) : Region.US_EAST_1)
                .build();
    }

    @Override
    public String upload(byte[] content, String path) throws Exception {
        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(path);

        if (config.getEnablePublicAccess()) {
            requestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
        }

        client.putObject(requestBuilder.build(), RequestBody.fromBytes(content));

        String domain = config.getDomain();
        if (domain == null || domain.trim().isEmpty()) {
            domain = config.getEndpoint();
            if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                domain = "https://" + domain;
            }
            if (config.getEnablePathStyleAccess()) {
                return domain + "/" + config.getBucket() + "/" + path;
            } else {
                String protocol = domain.startsWith("https://") ? "https://" : "http://";
                String cleanEndpoint = domain.replace(protocol, "");
                return protocol + config.getBucket() + "." + cleanEndpoint + "/" + path;
            }
        }

        if (!domain.endsWith("/")) {
            domain += "/";
        }

        return domain + path;
    }

    @Override
    public void delete(String path) throws Exception {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(config.getBucket())
                .key(path)
                .build();
        client.deleteObject(deleteObjectRequest);
    }

    @Override
    public byte[] getContent(String path) throws Exception {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(config.getBucket())
                .key(path)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = client.getObjectAsBytes(getObjectRequest);
        return objectBytes.asByteArray();
    }
}
