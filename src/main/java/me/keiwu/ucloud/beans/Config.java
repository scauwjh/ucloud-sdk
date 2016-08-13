package me.keiwu.ucloud.beans;

/**
 * Created by kei on 8/1/16.
 */
public class Config {

    private String privateKey;
    private String publicKey;

    public Config(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Config(String privateKey, String publicKey, String baseUrl) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
