package co.shareleaf.instagram4j.responses.users;

import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class UserResponse extends IGResponse {
    private User user;
}
