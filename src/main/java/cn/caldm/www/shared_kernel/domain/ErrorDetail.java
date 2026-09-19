package cn.caldm.www.shared_kernel.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {
    private String traceId;
    private String message;
}
