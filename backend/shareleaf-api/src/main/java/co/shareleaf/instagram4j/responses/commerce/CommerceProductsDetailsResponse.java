package co.shareleaf.instagram4j.responses.commerce;

import co.shareleaf.instagram4j.models.commerce.Product;
import co.shareleaf.instagram4j.models.user.Profile;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class CommerceProductsDetailsResponse extends IGResponse {
    private Profile merchant;
    private Product product_item;
}
