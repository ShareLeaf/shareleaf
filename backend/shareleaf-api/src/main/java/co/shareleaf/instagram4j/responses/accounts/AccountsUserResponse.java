package co.shareleaf.instagram4j.responses.accounts;

import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class AccountsUserResponse extends IGResponse {
    private User user;
}
