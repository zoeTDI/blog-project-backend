package cn.caldm.www.user_context.interfaces.assembler;

import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;

/**
 *
 *
 *
 * @author caldm
 */
public class UserAssembler {
    /**
     * 将数据库持久化对象 (PO) 转换为领域层实体 (SysUser)
     */
    public static SysUser toDomain(SysUserPO po) {
        if (po == null) {
            return null;
        }
        SysUser domainUser = new SysUser();
        domainUser.setId(po.getId());
        domainUser.setUsername(po.getUsername());
        domainUser.setPassword(po.getPassword());
        domainUser.setNickname(po.getNickname());
        domainUser.setEmail(po.getEmail());
        domainUser.setSalt(po.getSalt());
        domainUser.setAvatar(po.getAvatar());
        domainUser.setStatus(po.getStatus());
        domainUser.setLoginIp(po.getLoginIp());
        domainUser.setLoginDate(po.getLoginDate());
        domainUser.setCreator(po.getCreator());
        domainUser.setCreateTime(po.getCreateTime());
        domainUser.setUpdater(po.getUpdater());
        domainUser.setUpdateTime(po.getUpdateTime());
        domainUser.setDeleted(po.getDeleted());
        return domainUser;
    }
}
