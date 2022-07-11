package co.shareleaf.instagram4j.exceptions;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.responses.accounts.LoginResponse;

import lombok.Getter;

@Getter
public class IGLoginException extends IGResponseException {
    private final IGClient client;
    private final LoginResponse loginResponse;

    public IGLoginException(IGClient client, LoginResponse body) {
        super(body);
        this.client = client;
        this.loginResponse = body;
    }

}
