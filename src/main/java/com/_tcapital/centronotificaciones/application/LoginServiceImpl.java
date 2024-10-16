package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.Adapter.LoginAdapter;
import com._tcapital.centronotificaciones.application.Dto.LoginCamerResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final LoginAdapter loginAdapter;

    public LoginServiceImpl(LoginAdapter loginAdapter) {
        this.loginAdapter = loginAdapter;
    }

    @Override
    public LoginCamerResponse login() {
        return loginAdapter.login();
    }
}