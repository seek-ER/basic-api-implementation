package com.thoughtworks.rslist.domain;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    static List<User> userList = new ArrayList<>();

    static public List<User> getUserList() {
        return userList;
    }

    public static void reSetUserList() {
        userList.clear();
    }
}
