package com.iemdb.info;

public class AccountInfo {
    private final String email;
    private final String JWT;
    private final boolean isLoggedIn;

    public AccountInfo(String _email, String _jwt) {
        email = _email;
        JWT = _jwt;
        isLoggedIn = !(_email.isEmpty() || _email.isBlank());
    }

    public String getEmail() { return email; }
    public String getJWT(){ return JWT;}
    public boolean getIsLoggedIn() { return isLoggedIn; }
}
