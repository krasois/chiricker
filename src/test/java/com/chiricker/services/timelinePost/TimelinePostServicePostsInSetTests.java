package com.chiricker.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimelinePostServicePostsInSetTests {

    @Mock
    private TimelinePostRepository timelinePostRepository;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private TimelinePostServiceImpl timelinePostService;

    private Pageable pageable;
    private Page<TimelinePost> pagePosts;

    @Before
    public void setup() {
        this.pageable = Mockito.mock(Pageable.class);
        List<TimelinePost> posts = new ArrayList<>() {{
            add(new TimelinePost() {{
                setId("asdasdadadas");
            }});
            add(new TimelinePost() {{
                setId("aqweqweqweqwe");
            }});
        }};
        this.pagePosts = new PageImpl<>(posts);

        when(this.timelinePostRepository.findAllInSet(eq(new HashSet<>()), eq(this.pageable))).thenReturn(this.pagePosts);
        when(this.mapper.map(any(TimelinePost.class), eq(TimelinePostServiceModel.class))).thenAnswer(a -> {
            TimelinePost p = a.getArgument(0);
            TimelinePostServiceModel m = new TimelinePostServiceModel();
            m.setId(p.getId());
            return m;
        });
    }

    @Test
    public void testPostsInSet_WithValidData_ShouldMapCorrectly() {
        Page<TimelinePostServiceModel> posts = this.timelinePostService.getPostsInCollection(new HashSet<>(), this.pageable);

        List<TimelinePost> content = this.pagePosts.getContent();
        List<TimelinePostServiceModel> mappedContent = posts.getContent();
        for (int i = 0; i < mappedContent.size(); i++) {
            assertTrue("Id is not mapped correctly for post with Id '" + content.get(i) + "'", content
                    .get(i).getId()
                    .equals(mappedContent.get(i).getId()));
        }
    }
}
