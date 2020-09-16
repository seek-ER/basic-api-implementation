package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;

import java.util.ArrayList;
import java.util.List;

public class RsDataHelper {

    public List<RsEvent> provideInitialRsEventList() {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        List<RsEvent> rsEventList = new ArrayList<>();
        rsEventList.add(new RsEvent("第一条事件","无标签", user));
        rsEventList.add(new RsEvent("第二条事件","无标签", user));
        rsEventList.add(new RsEvent("第三条事件","无标签", user));
        return rsEventList;
    }

    public void reSetRsEventList(List<RsEvent> rsEventList) {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsEventList.clear();
        rsEventList.add(new RsEvent("第一条事件","无标签",user));
        rsEventList.add(new RsEvent("第二条事件","无标签",user));
        rsEventList.add(new RsEvent("第三条事件","无标签",user));
    }
}
