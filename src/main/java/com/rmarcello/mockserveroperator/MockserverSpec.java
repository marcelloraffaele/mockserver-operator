package com.rmarcello.mockserveroperator;

public class MockserverSpec {

    private String image;
    private Integer replica;
    private String config;
    private String ingressHost;
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Integer getReplica() {
        return replica;
    }
    public void setReplica(Integer replica) {
        this.replica = replica;
    }
    public String getConfig() {
        return config;
    }
    public void setConfig(String config) {
        this.config = config;
    }
    public String getIngressHost() {
        return ingressHost;
    }
    public void setIngressHost(String ingressHost) {
        this.ingressHost = ingressHost;
    }
    @Override
    public String toString() {
        return "MockserverSpec [image=" + image + ", replica=" + replica + ", config=" + config + ", ingressHost="
                + ingressHost + "]";
    }

    
}