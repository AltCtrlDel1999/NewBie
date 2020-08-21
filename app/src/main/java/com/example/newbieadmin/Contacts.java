package com.example.newbieadmin;

public class Contacts
{
    private String username,password,phoneno,image,address;

    Contacts(){

    }

    public Contacts(String username, String password, String phoneno, String image, String address) {
        this.username = username;
        this.password = password;
        this.phoneno = phoneno;
        this.image = image;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
