package co.shareleaf.instagram4j.models.news;

import co.shareleaf.instagram4j.models.IGBaseModel;

import lombok.Data;

@Data
public class NewsStory extends IGBaseModel {
    private int type;
    private int story_type;
    private String pk;
}
