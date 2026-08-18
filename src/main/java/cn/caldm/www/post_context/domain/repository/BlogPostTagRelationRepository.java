package cn.caldm.www.post_context.domain.repository;

import java.util.List;

public interface BlogPostTagRelationRepository {
    /**
     * 根据标签ID删除所有关联记录（物理删除）
     *
     * @param tagId 标签ID
     * @return 删除的记录数
     */
    int deleteByTagId(Long tagId);

    /**
     * 根据标签ID列表批量删除所有关联记录（物理删除）
     *
     * @param tagIds 标签ID列表
     * @return 删除的记录数
     */
    int deleteByTagIds(List<Long> tagIds);
}
