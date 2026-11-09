package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;

import java.util.List;

public interface UserRepository {
    SysUser findByEmail(String email);
    SysUser findByUsername(String username);
    SysUser findById(Long id);
    SysUser insert(String creator, String username, String password, String email, String nickname);
    boolean updateStatus(Long id, SysUserStatusEnum status, String updater);
    boolean markDeleted(Long id, String updater);
    boolean updatePassword(Long id, String encodedPassword, String updater);

    /**
     * 分页查询未删除的用户（按主键升序）。
     *
     * @param page 页码，从 1 开始
     * @param size 每页条数
     * @return 用户分页结果；无数据时 records 为空列表
     */
    PageResult<SysUser> findPage(long page, long size);
}
