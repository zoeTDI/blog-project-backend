package cn.caldm.www.post_context.application.service;

import cn.caldm.www.common.domain.PageResult;
import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {
    @Mock
    private BlogPostRepository blogPostRepository;

    @InjectMocks
    private BlogPostService blogPostService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.Manager.clear();
    }

    @Test
    void getsRequestedPageOnlyForAuthenticatedUser() {
        SecurityContextHolder.Manager.setCurrentUser(42L, "author");
        PageResult<BlogPost> expected = new PageResult<>(List.of(new BlogPost()), 21, 2, 20, 2);
        when(blogPostRepository.findPageByAuthorId(42L, 2, 20)).thenReturn(expected);

        PageResult<BlogPost> actual = blogPostService.getCurrentUserPosts(2, 20);

        assertEquals(expected, actual);
        verify(blogPostRepository).findPageByAuthorId(42L, 2, 20);
    }

    @Test
    void rejectsOutOfRangePageSizeBeforeQueryingRepository() {
        SecurityContextHolder.Manager.setCurrentUser(42L, "author");

        assertThrows(IllegalArgumentException.class, () -> blogPostService.getCurrentUserPosts(1, 101));

        verify(blogPostRepository, never()).findPageByAuthorId(42L, 1, 101);
    }
}
