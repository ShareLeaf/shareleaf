package co.shareleaf.instagram4j.models.discover;

import java.util.List;

import co.shareleaf.instagram4j.models.IGBaseModel;

import lombok.Data;

@Data
public class Cluster extends IGBaseModel {
    private String id;
    private String title;
    private String context;
    private String description;
    private List<String> labels;
    private String name;
    private String type;
}
