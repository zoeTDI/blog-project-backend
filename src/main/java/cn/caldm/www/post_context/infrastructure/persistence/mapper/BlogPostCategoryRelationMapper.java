package cn.caldm.www.post_context.infrastructure.persistence.mapper;

import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryRelationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogPostCategoryRelationMapper extends BaseMapper<BlogPostCategoryRelationPO> {
    /**
     * 批量插入关联记录
     * @param list 关联PO列表
     * @return 插入条数
     */
    int insertBatch(@Param("list") List<BlogPostCategoryRelationPO> list);
}
