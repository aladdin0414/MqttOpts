package com.epoint.mqttopts.mqtt;

/**
 * @author liyc
 * @date 2020/3/17 2:26 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttChannelConfig {

    String hostUri;

    String username;

    String password;

    String cliendid;

    String persistDir;

    boolean autoConnect;

    IMqttChannelHandler handler;

    String[] subtopics;

    int[] subqos;

    public String getHostUri() {
        return hostUri;
    }

    public void setHostUri(String hostUri) {
        this.hostUri = hostUri;
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

    public String getCliendid() {
        return cliendid;
    }

    public void setCliendid(String cliendid) {
        this.cliendid = cliendid;
    }

    public String getPersistDir() {
        return persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public IMqttChannelHandler getHandler() {
        return handler;
    }

    public void setHandler(IMqttChannelHandler handler) {
        this.handler = handler;
    }

    public String[] getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(String[] subtopics) {
        this.subtopics = subtopics;
    }

    public int[] getSubqos() {
        return subqos;
    }

    public void setSubqos(int[] subqos) {
        this.subqos = subqos;
    }
}
