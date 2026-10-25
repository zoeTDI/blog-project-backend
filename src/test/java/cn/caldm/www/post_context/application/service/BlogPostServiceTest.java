package cn.caldm.www.post_context.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;
import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
public class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;
    @Mock
    private BlogPostCategoryRelationRepository categoryRelationRepository;
    @Mock
    private BlogPostTagRelationRepository tagRelationRepository;
    @Mock
    private BlogPostCategoryRepository categoryRepository;
    @Mock
    private BlogPostTagRepository tagRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private BlogPostService blogPostService;

    @Test
    void createPost_shouldPersistTagAndCategoryRelations() {
        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getUsername()).thenReturn("tester");
        when(blogPostRepository.save(any(BlogPost.class))).thenAnswer(invocation -> {
            BlogPost post = invocation.getArgument(0);
            post.setId(10L);
            return post;
        });

        BlogPostCreateCommand command = new BlogPostCreateCommand();
        command.setTitle("测试标题");
        command.setTagIds(List.of(1L, 2L));
        command.setCategoryTrees(List.of(List.of(5L, 6L)));

        Long postId = blogPostService.createPost(command);

        assertThat(postId).isEqualTo(10L);

        // 标签关联必须落库（P1 防回归：旧实现此处从不调用 batchSave）
        ArgumentCaptor<List<BlogPostTagRelation>> tagCaptor = ArgumentCaptor.forClass(List.class);
        verify(tagRelationRepository).batchSave(tagCaptor.capture());
        assertThat(tagCaptor.getValue())
                .extracting(BlogPostTagRelation::getPostId)
                .containsOnly(10L);
        assertThat(tagCaptor.getValue())
                .extracting(BlogPostTagRelation::getTagId)
                .containsExactlyInAnyOrder(1L, 2L);

        // 分类关联必须落库（isDirect=true）
        ArgumentCaptor<List<BlogPostCategoryRelation>> categoryCaptor = ArgumentCaptor.forClass(List.class);
        verify(categoryRelationRepository).batchSave(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue())
                .extracting(BlogPostCategoryRelation::getPostId)
                .containsOnly(10L);
        assertThat(categoryCaptor.getValue())
                .extracting(BlogPostCategoryRelation::getCategoryId)
                .containsExactlyInAnyOrder(5L, 6L);
        assertThat(categoryCaptor.getValue())
                .allMatch(BlogPostCategoryRelation::isDirectRelation);
    }
}
