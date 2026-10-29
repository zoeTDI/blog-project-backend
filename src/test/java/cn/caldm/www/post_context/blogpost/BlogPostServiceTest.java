package cn.caldm.www.post_context.blogpost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;
import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
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

        // 创建恒为草稿（P3：作者禁止自设状态）
        ArgumentCaptor<BlogPost> postCaptor = ArgumentCaptor.forClass(BlogPost.class);
        verify(blogPostRepository).save(postCaptor.capture());
        assertThat(postCaptor.getValue().getStatus()).isEqualTo(BlogPostStatusEnum.DRAFT);

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

    @Test
    void getBlogPostById_shouldAllowAdminToViewOthersPost() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(2L);
        post.setDeleted(false);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getRoles()).thenReturn(List.of("ADMIN"));
        when(blogPostRepository.findById(5L)).thenReturn(post);
        when(categoryRelationRepository.findByPostId(5L)).thenReturn(List.of());
        when(tagRelationRepository.findByPostId(5L)).thenReturn(List.of());

        BlogPost result = blogPostService.getBlogPostById(5L);

        assertThat(result).isSameAs(post);
    }

    @Test
    void updateBlogPost_shouldAllowAdminToUpdateOthersPost() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(2L);
        post.setDeleted(false);

        BlogPostUpdateCommand command = new BlogPostUpdateCommand();
        command.setTargetPostId(5L);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getUsername()).thenReturn("admin");
        when(currentUserProvider.getRoles()).thenReturn(List.of("ADMIN"));
        when(blogPostRepository.findById(5L)).thenReturn(post);
        when(blogPostRepository.updateById(any(BlogPost.class))).thenReturn(true);

        blogPostService.updateBlogPost(command);

        verify(blogPostRepository).updateById(any(BlogPost.class));
    }

    @Test
    void toReview_shouldSubmitDraft() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(1L);
        post.setStatus(BlogPostStatusEnum.DRAFT);
        post.setDeleted(false);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getRoles()).thenReturn(List.of("AUTHOR"));
        when(blogPostRepository.findById(5L)).thenReturn(post);

        blogPostService.toReview(5L);

        verify(blogPostRepository).updateStatusById(5L, BlogPostStatusEnum.REVIEWING, null);
    }

    @Test
    void toReview_shouldRejectNonDraft() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(1L);
        post.setStatus(BlogPostStatusEnum.PUBLISHED);
        post.setDeleted(false);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getRoles()).thenReturn(List.of("AUTHOR"));
        when(blogPostRepository.findById(5L)).thenReturn(post);

        assertThatThrownBy(() -> blogPostService.toReview(5L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("draft");
    }

    @Test
    void toPublished_shouldRejectDraft() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(1L);
        post.setStatus(BlogPostStatusEnum.DRAFT);
        post.setDeleted(false);

        when(currentUserProvider.getRoles()).thenReturn(List.of("ADMIN"));
        when(blogPostRepository.findById(5L)).thenReturn(post);

        // P3 核心回归钉：草稿不得绕过审核直达发布
        assertThatThrownBy(() -> blogPostService.toPublished(5L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("reviewing");
    }

    @Test
    void toPublished_shouldPublishReviewing() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(1L);
        post.setStatus(BlogPostStatusEnum.REVIEWING);
        post.setDeleted(false);

        when(currentUserProvider.getRoles()).thenReturn(List.of("ADMIN"));
        when(blogPostRepository.findById(5L)).thenReturn(post);

        blogPostService.toPublished(5L);

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(blogPostRepository)
                .updateStatusById(org.mockito.ArgumentMatchers.eq(5L),
                        org.mockito.ArgumentMatchers.eq(BlogPostStatusEnum.PUBLISHED),
                        timeCaptor.capture());
        assertThat(timeCaptor.getValue()).isNotNull();
    }

    @Test
    void updateBlogPost_shouldBeTransactional() throws NoSuchMethodException {
        // P6 回归钉：五步写必须整体事务，防未来误删注解
        Method method = BlogPostService.class.getMethod("updateBlogPost", BlogPostUpdateCommand.class);
        Transactional transactional = method.getAnnotation(Transactional.class);

        assertThat(transactional).isNotNull();
        assertThat(transactional.rollbackFor()).contains(Exception.class);
    }

    @Test
    void softDeletePost_shouldArchiveOwnPost() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(1L);
        post.setDeleted(false);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getRoles()).thenReturn(List.of("AUTHOR"));
        when(blogPostRepository.findById(5L)).thenReturn(post);
        when(blogPostRepository.archiveById(5L)).thenReturn(true);

        blogPostService.softDeletePost(5L);

        verify(blogPostRepository).archiveById(5L);
        assertThat(post.getDeleted()).isTrue();
    }

    @Test
    void softDeletePost_shouldRejectWhenNotAuthorOrAdmin() {
        BlogPost post = new BlogPost();
        post.setId(5L);
        post.setAuthorId(2L);
        post.setDeleted(false);

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getRoles()).thenReturn(List.of("AUTHOR"));
        when(blogPostRepository.findById(5L)).thenReturn(post);

        assertThatThrownBy(() -> blogPostService.softDeletePost(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("没有权限");
    }

    @Test
    void softDeletePost_shouldThrowWhenNotFound() {
        when(blogPostRepository.findById(5L)).thenReturn(null);

        assertThatThrownBy(() -> blogPostService.softDeletePost(5L))
                .isInstanceOf(cn.caldm.www.shared_kernel.domain.ResourceNotFoundException.class);
    }

    @Test
    void createPost_shouldThrowWhenSaveFailed() {
        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(currentUserProvider.getUsername()).thenReturn("tester");
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(null);

        BlogPostCreateCommand command = new BlogPostCreateCommand();
        command.setTitle("测试标题");

        assertThatThrownBy(() -> blogPostService.createPost(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("创建文章失败");
    }
}
