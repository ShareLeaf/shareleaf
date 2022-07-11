package co.shareleaf.instagram4j.models.commerce;

import co.shareleaf.instagram4j.models.IGBaseModel;
import co.shareleaf.instagram4j.models.user.Profile;

import lombok.Data;

@Data
public class Product extends IGBaseModel {
    private String name;
    private String price;
    private String current_price;
    private String full_price;
    private long product_id;
    private Profile merchant;
    private String compound_product_id;
    private String description;
    private String retailer_id;
    private String external_url;

}
