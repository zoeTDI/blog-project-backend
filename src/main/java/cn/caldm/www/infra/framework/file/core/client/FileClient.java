package cn.caldm.www.infra.framework.file.core.client;

/**
 * 文件客户端顶层接口
 * 采用策略模式（Strategy Pattern）屏蔽底层存储介质差异
 *
 * @author caldm
 */
public interface FileClient {
    /**
     * 获取客户端对应的配置编号（对应数据库的 config_id）
     *
     * @return 配置编号
     */
    Long getId();

    /**
     * 上传文件
     *
     * @param content 文件字节数组
     * @param path    相对路径，例如 "avatar/user_1.png"
     * @return 最终可公开或私有访问的绝对 URL 路径
     * @throws Exception 上传异常
     */
    String upload(byte[] content, String path) throws Exception;

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @throws Exception 删除异常
     */
    void delete(String path) throws Exception;

    /**
     * 获得文件的内容（适用于私有桶或本地文件流下载读取）
     *
     * @param path 相对路径
     * @return 文件字节内容
     * @throws Exception 读取异常
     */
    byte[] getContent(String path) throws Exception;
}
