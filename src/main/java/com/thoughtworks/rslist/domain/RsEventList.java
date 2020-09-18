package com.thoughtworks.rslist.domain;

import java.util.ArrayList;
import java.util.List;

public class RsEventList {
    static List<RsEvent> rsEventList = new ArrayList<>();

    static public List<RsEvent> getRsEventList() {
        return rsEventList;
    }

    public void setRsEventList(List<RsEvent> userList) {
        this.rsEventList = userList;
    }

    public static void reSetRsEventList() {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsEventList.clear();
        rsEventList.add(new RsEvent("第一条事件","无标签",1));
        rsEventList.add(new RsEvent("第二条事件","无标签",1));
        rsEventList.add(new RsEvent("第三条事件","无标签",1));
    }
}
