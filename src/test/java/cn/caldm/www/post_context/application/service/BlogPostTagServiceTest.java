package cn.caldm.www.post_context.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
public class BlogPostTagServiceTest {

    @Mock
    private BlogPostTagRepository tagRepository;
    @Mock
    private BlogPostTagRelationRepository tagRelationRepository;
    @Mock
    private BlogPostRepository postRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private BlogPostTagService blogPostTagService;

    @Test
    void addTagToPost_shouldRejectWhenNotAuthor() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(2L);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(postRepository.findById(5L)).thenReturn(post);

        boolean result = blogPostTagService.addTagToPost(5L, "新标签");

        assertThat(result).isFalse();
        // 不得以文章作者身份创建任何标签
        verify(tagRepository, never()).add(any(BlogPostTag.class));
    }

    @Test
    void addTagsToPost_shouldRejectWhenNotAuthor() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(2L);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(postRepository.findById(5L)).thenReturn(post);

        int result = blogPostTagService.addTagsToPost(5L, List.of("标签A", "标签B"));

        assertThat(result).isZero();
        verify(tagRelationRepository, never()).batchSave(any());
    }

    @Test
    void batchDeleteTags_shouldOnlyDeleteOwnedTags() {
        BlogPostTag ownTag1 = new BlogPostTag();
        ownTag1.setId(1L);
        ownTag1.setAuthorId(1L);
        BlogPostTag otherTag = new BlogPostTag();
        otherTag.setId(2L);
        otherTag.setAuthorId(2L);
        BlogPostTag ownTag3 = new BlogPostTag();
        ownTag3.setId(3L);
        ownTag3.setAuthorId(1L);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(tagRepository.findByIds(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(ownTag1, otherTag, ownTag3));
        when(tagRepository.batchDeleteByIds(List.of(1L, 3L))).thenReturn(2);

        int result = blogPostTagService.batchDeleteTags(List.of(1L, 2L, 3L));

        assertThat(result).isEqualTo(2);
        // 只删归属自己的标签，他人的标签原封不动
        verify(tagRelationRepository).deleteByTagIds(List.of(1L, 3L));
        verify(tagRepository).batchDeleteByIds(List.of(1L, 3L));
    }
}
