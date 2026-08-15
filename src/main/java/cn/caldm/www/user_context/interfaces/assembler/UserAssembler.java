package cn.caldm.www.user_context.interfaces.assembler;

import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class UserAssembler implements BaseAssembler<SysUser, SysUserPO> {
    /**
     * 将数据库持久化对象 (PO) 转换为领域层实体 (SysUser)
     */
    @Override
    public SysUser toDomain(SysUserPO po) {
        if (po == null) {
            return null;
        }
        SysUser domainUser = new SysUser();
        domainUser.setId(po.getId());
        domainUser.setUsername(po.getUsername());
        domainUser.setPassword(po.getPassword());
        domainUser.setNickname(po.getNickname());
        domainUser.setEmail(po.getEmail());
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

    @Override
    public SysUserPO toPO(SysUser domain) {
        if (domain == null) {
            return null;
        }
        SysUserPO po = new SysUserPO();
        po.setId(domain.getId());
        po.setUsername(domain.getUsername());
        po.setPassword(domain.getPassword());
        po.setNickname(domain.getNickname());
        po.setEmail(domain.getEmail());
        po.setAvatar(domain.getAvatar());
        po.setStatus(domain.getStatus());
        po.setLoginIp(domain.getLoginIp());
        po.setLoginDate(domain.getLoginDate());
        po.setCreator(domain.getCreator());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdater(domain.getUpdater());
        po.setUpdateTime(domain.getUpdateTime());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}
