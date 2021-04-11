package com.vwmin.k8sawd.web.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/11 12:17
 */
@Data
public class LiveLogEvent {
    private String type;
    private long timestamp;
    private Map<String, String> message;

    public static LiveLogEvent AttackEvent(String from, String to, String competitionTitle){
        LiveLogEvent event = new LiveLogEvent();
        event.setType("submitFlag");
        event.setTimestamp(System.currentTimeMillis());
        event.setMessage(new HashMap<String, String>(){{
            put("from", from);
            put("to", to);
            put("challenge", competitionTitle);
        }});
        return event;
    }
}
