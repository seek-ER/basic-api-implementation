package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;

import java.util.ArrayList;
import java.util.List;

public class RsDataProvider {

    public List<RsEvent> provideInitialRsEventList() {
        List<RsEvent> rsEventList = new ArrayList<>();
        rsEventList.add(new RsEvent("第一条事件","无标签"));
        rsEventList.add(new RsEvent("第二条事件","无标签"));
        rsEventList.add(new RsEvent("第三条事件","无标签"));
        return rsEventList;
    }

    public void reSetRsEventList(List<RsEvent> rsEventList) {
        rsEventList.clear();
        rsEventList.add(new RsEvent("第一条事件","无标签"));
        rsEventList.add(new RsEvent("第二条事件","无标签"));
        rsEventList.add(new RsEvent("第三条事件","无标签"));
    }
}
