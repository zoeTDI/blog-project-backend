package cn.caldm.www.system_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class File {
    private Long id;

    private Long configId;

    private String name;

    private String path;

    private String url;

    private String type;

    private Integer size;

    private String creator;

    private LocalDateTime createTime;

    private String updater;

    private LocalDateTime updateTime;

    private Boolean deleted;
}
