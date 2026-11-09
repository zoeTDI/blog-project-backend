package cn.caldm.www.post_context.publicpost;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;

/**
 * 公开详情装配回归钉：
 * 字段白名单映射 + DTO 结构上不存在 password 等敏感字段（防泄漏钉）。
 */
public class PublicPostAssemblerTest {

    private final PublicPostAssembler assembler = new PublicPostAssembler();

    @Test
    void toDto_shouldMapPublicFields() {
        BlogPost domain = new BlogPost();
        domain.setId(5L);
        domain.setAuthorId(2L);
        domain.setCreator("zhangsan");
        domain.setTitle("公开文章");
        domain.setContentHtml("<p>正文</p>");
        domain.setSummary("摘要");
        domain.setType(BlogPostTypeEnum.NORMAL);
        domain.setIsOriginal(true);
        domain.setSlug("public-post");
        domain.setTags(List.of(new BlogPostTag().setId(1L).setName("C++")));

        PublicPostDTO dto = assembler.toDto(domain);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getAuthorId()).isEqualTo(2L);
        assertThat(dto.getAuthor()).isEqualTo("zhangsan");
        assertThat(dto.getTitle()).isEqualTo("公开文章");
        assertThat(dto.getContentHtml()).isEqualTo("<p>正文</p>");
        assertThat(dto.getSummary()).isEqualTo("摘要");
        assertThat(dto.getIsOriginal()).isTrue();
        assertThat(dto.getSlug()).isEqualTo("public-post");
        assertThat(dto.getTags()).hasSize(1);
        assertThat(dto.getTags().get(0).getName()).isEqualTo("C++");
    }

    @Test
    void toDto_shouldReturnNullForNullDomain() {
        assertThat(assembler.toDto(null)).isNull();
    }

    @Test
    void publicPostDTO_shouldNotExposeSensitiveFields() {
        // 防泄漏回归钉：password/status/deleted/updater/sortWeight 永不得进入公开 DTO
        List<String> fields = Arrays.stream(PublicPostDTO.class.getDeclaredFields())
                .map(field -> field.getName())
                .toList();

        assertThat(fields)
                .doesNotContain("password", "status", "deleted", "updater", "sortWeight");
    }
}
