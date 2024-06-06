package edu.ucsb.cs156.github;

public class JwtProvider {
    private String appId;
    private String keyFile;

    private String jwt;
    private long jwtCreatedAt;

    public JwtProvider(String appId, String keyFile) {
        this.appId = appId;
        this.keyFile = keyFile;
    }

    private void checkJwtExpiration() {
        if (jwt == null || System.currentTimeMillis() - jwtCreatedAt > 600000) {
            try {
                jwt = Utils.createJWT(appId, 600000, keyFile);
                jwtCreatedAt = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getJwt() {
        checkJwtExpiration();
        return jwt;
    }
}
