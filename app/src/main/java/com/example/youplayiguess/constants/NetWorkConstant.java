package com.example.youplayiguess.constants;

public interface NetWorkConstant {
    String LOGIN_URL = "http://49.232.215.214:8080/users/login";
    String REGISTER_URL = "http://49.232.215.214:8080/users/register";

    String PLAYER_MATCH_URL = "http://49.232.215.214:8080/users/{username}/match";

    String GET_RANK_URL = "http://49.232.215.214:8080/users/top/{topN}/summary";
    String GET_MYSELF_URL = "http://49.232.215.214:8080/users/{username}/summary";

    String WEB_SOCKET_URL = "ws://49.232.215.214:8080/websocket/{roomNo}/{username}";

}