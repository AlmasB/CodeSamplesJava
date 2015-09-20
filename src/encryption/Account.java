package com.almasb.common.encryption;

public abstract class Account implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -663381662772982891L;

    private String username, password, key;

    protected Account(String username, String password, String key) {
        this.username = username;
        this.password = password;
        this.key = key;
    }

    public String getUserName() {
        return username;
    }

    public String getEncryptedPassword() {
        return password;
    }

    public String getKey() {
        return key;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEncryptedPassword(String password) {
        this.password = password;
    }
}
