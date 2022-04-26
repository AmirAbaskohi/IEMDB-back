package com.iemdb.info;

public class AccountInfo {
    private final String email;
    private final boolean isLoggedIn;

    public AccountInfo(String _email) {
        email = _email;
        isLoggedIn = !(_email.isEmpty() || _email.isBlank());
    }

    public String getEmail() { return email; }
    public boolean getIsLoggedIn() { return isLoggedIn; }
}
