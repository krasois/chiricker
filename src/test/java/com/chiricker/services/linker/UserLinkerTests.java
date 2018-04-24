package com.chiricker.services.linker;

import com.chiricker.util.linker.UserLinker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.HtmlUtils;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserLinkerTests {

    @Test
    public void testLinker_WithNoHandle_ShouldNotDoAnything() {
        String content = "some content to check if it doesnt link when it shouldnt.";
        String result = UserLinker.linkUsers(content);

        assertEquals("Result and passed content should be the same.", content, result);
    }

    @Test
    public void testLinker_WithOneHandle_ShouldLinkCorrectly() {
        String result = UserLinker.linkUsers("some random content @krasois and some more of it.");

        String expected = "some random content <a class=\"text-info\" href=\"@krasois\">@krasois</a> and some more of it.";

        assertEquals("Linker did not link correctly.", expected, result);
    }

    @Test
    public void testLinker_WithOneHandleRepeated_ShouldLinkCorrectly() {
        String result = UserLinker.linkUsers("some random content @krasois and some more of it @krasois and that it is.");

        String expected = "some random content <a class=\"text-info\" href=\"@krasois\">@krasois</a> and some more of it <a class=\"text-info\" href=\"@krasois\">@krasois</a> and that it is.";

        assertEquals("Linker did not link correctly.", expected, result);
    }

    @Test
    public void testLinker_WithTwoHandles_ShouldLinkCorrectly() {
        String result = UserLinker.linkUsers("some random content @krasois and some more of it @cyecize and that it is.");

        String expected = "some random content <a class=\"text-info\" href=\"@krasois\">@krasois</a> and some more of it <a class=\"text-info\" href=\"@cyecize\">@cyecize</a> and that it is.";

        assertEquals("Linker did not link correctly.", expected, result);
    }

    @Test
    public void testLinker_WithTwoHandlesOneIsRepeated_ShouldLinkCorrectly() {
        String result = UserLinker.linkUsers("some random content @krasois and some more of it @cyecize and that it is. Will it repeat @cyecize");

        String expected = "some random content <a class=\"text-info\" href=\"@krasois\">@krasois</a> and some more of it <a class=\"text-info\" href=\"@cyecize\">@cyecize</a> and that it is. Will it repeat <a class=\"text-info\" href=\"@cyecize\">@cyecize</a>";

        assertEquals("Linker did not link correctly.", expected, result);
    }
}