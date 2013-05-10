package demo;

import com.alibaba.fastjson.JSON;
import org.rythmengine.play.RythmPlugin;
import org.rythmengine.utils.JSONWrapper;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String roles;
    
    public User() {}

    public User(String username, String firstName, String lastName) {
        if (null == username) {
            throw new NullPointerException();
        }
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    public void setRoles(String roles) {
        this.roles = roles;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String uname) {
        this.username = uname;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String fn) {
        firstName = fn;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String ln) {
        lastName = ln;
    }
    
    public String getName() {
        return RythmPlugin.toString("@_.getFirstName() @_.getLastName()", this);
    }
    
    public String toString() {
        return RythmPlugin.toString(this);
    }

    public static void main(String[] args) {
        String json = "{\"username\": \"greenlaw110\",\"firstName\": \"Gelin\", \"lastName\": \"Luo\"}";
        User user = JSON.parseObject(json, User.class);
        System.out.println(user);
        
        String s = RythmPlugin.substitute("{\"user\": @1}", json);
        System.out.println(RythmPlugin.render("@args demo.User user;@user.getName()", JSONWrapper.wrap(s)));
    }
}
