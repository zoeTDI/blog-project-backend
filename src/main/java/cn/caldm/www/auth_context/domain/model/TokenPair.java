package cn.caldm.www.auth_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
