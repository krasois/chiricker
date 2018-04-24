package com.chiricker.util.linker;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserLinker {

    private static final String LINK_PATTERN = "<a class=\"text-info\" href=\"%s\">%s</a>";
    private static final String USER_HANDLE_PATTERN = "(?<!<a class=\"text-info\" href=\"|\">)(@\\w+)(?!</a>)";
    private static final Pattern PATTERN = Pattern.compile(USER_HANDLE_PATTERN);

    public static String linkUsers(String content) {
        content = HtmlUtils.htmlEscapeDecimal(content);
        while(true) {
            Matcher matcher = PATTERN.matcher(content);
            if (matcher.find()) {
                String match = matcher.group(0).trim();
                content = content.replaceAll(match, String.format(LINK_PATTERN, match, match));
            } else {
                break;
            }
        }

        return content;
    }
}