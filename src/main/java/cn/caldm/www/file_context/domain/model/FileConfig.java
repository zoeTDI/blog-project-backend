package cn.caldm.www.file_context.domain.model;

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
public class FileConfig {

    private Long id;

    private String name;

    private Integer storage;

    private String remark;

    private Boolean master;

    private String config;

    private String creator;

    private LocalDateTime createTime;

    private String updater;

    private LocalDateTime updateTime;

    private Boolean deleted;
}
