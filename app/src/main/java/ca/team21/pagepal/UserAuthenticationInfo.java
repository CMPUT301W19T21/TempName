package ca.team21.pagepal;

public class UserAuthenticationInfo {
    private String username;
    private String password;

    public UserAuthenticationInfo(){}

    public UserAuthenticationInfo(String username, String password) {}

    public boolean login(String username, String password) {return false;}

    public boolean logout(User user) {return true;}
}