package com.rmarcello.mockserveroperator;

public class MockserverSpec {

    private String image;
    private Integer replica;
    private String config;

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getReplica() {
        return this.replica;
    }

    public void setReplica(Integer replica) {
        this.replica = replica;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }


    @Override
    public String toString() {
        return "{" +
            " image='" + getImage() + "'" +
            ", replica='" + getReplica() + "'" +
            ", config='" + getConfig() + "'" +
            "}";
    }


}