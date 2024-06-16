package com.example.dipl.config

class InternetAddress {

    public val webSocketAddress =
        "ws://ec2-13-59-82-93.us-east-2.compute.amazonaws.com:8080/my-ws/websocket"

    //public static final String webSocketAddress = "ws://192.168.1.2:8080/my-ws/websocket";

    //public static final String webSocketAddress = "ws://192.168.1.2:8080/my-ws/websocket";
    val volleyUserPostAddress =
        "http://ec2-13-59-82-93.us-east-2.compute.amazonaws.com:8080/api/users/post"

    //  public static final String volleyUserPostAddress = "http://192.168.1.2:8080/api/users/post";
    companion object {
        const val webSocketAddress =
            "ws://172.17.0.2:8080/my-ws/websocket"
    }
}