package cn.caldm.www.post_context.infrastructure.persistence.mapper;

import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogPostMapper extends BaseMapper<BlogPostPO> {
}
