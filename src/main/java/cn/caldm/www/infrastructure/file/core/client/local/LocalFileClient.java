package cn.caldm.www.infrastructure.file.core.client.local;

import cn.caldm.www.infrastructure.file.core.client.AbstractFileClient;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储客户端实现
 *
 * @author caldm
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() throws Exception {
        File file = new File(config.getBasePath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public String upload(byte[] content, String path) throws Exception {
        Path absolutePath = Paths.get(config.getBasePath(), path);

        if (!Files.exists(absolutePath.getParent())) {
            Files.createDirectories(absolutePath.getParent());
        }
        Files.write(absolutePath, content);

        String domain = config.getDomain();
        if (!domain.endsWith("/")) {
            domain += "/";
        }
        return domain + path;
    }

    @Override
    public void delete(String path) throws Exception {
        Path absolutePath = Paths.get(config.getBasePath(), path);
        Files.deleteIfExists(absolutePath);
    }

    @Override
    public byte[] getContent(String path) throws Exception {
        Path absolutePath = Paths.get(config.getBasePath(), path);
        if (!Files.exists(absolutePath)) {
            return null;
        }
        try (InputStream in = new FileInputStream(absolutePath.toFile())) {
            return StreamUtils.copyToByteArray(in);
        }
    }
}
