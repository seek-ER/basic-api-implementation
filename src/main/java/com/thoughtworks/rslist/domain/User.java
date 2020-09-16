package com.thoughtworks.rslist.domain;

import javax.validation.constraints.*;
import java.util.Objects;

public class User {
    @NotBlank
    @Size(max = 8)
    private String name;
    @NotBlank
    private String gender;
    @Min(18)
    @Max(100)
    private int age;
    @Email
    private String email;
    @Pattern(regexp = "1\\d{10}")
    private String phone;
    private int voteNumber=10;

    public User() {
    }

    public User(String name, String gender, int age, String email, String phone) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getVoteNumber() {
        return voteNumber;
    }

    public void setVoteNumber(int voteNumber) {
        this.voteNumber = voteNumber;
    }
}
