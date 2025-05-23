package co.shareleaf.instagram4j.responses.news;

import java.util.List;

import co.shareleaf.instagram4j.models.news.NewsStory;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class NewsInboxResponse extends IGResponse {
    private NewsCounts counts;
    private List<NewsStory> new_stories;
    private List<NewsStory> old_stories;

    @Data
    public static class NewsCounts {
        private int likes;
        private int comments;
        private int shopping_notification;
        private int usertags;
        private int relationships;
        private int campaign_notification;
        private int comment_likes;
        private int photos_of_you;
        private int requests;
    }
}
