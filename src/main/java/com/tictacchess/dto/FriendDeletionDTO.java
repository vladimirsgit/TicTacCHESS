package com.tictacchess.dto;

public class FriendDeletionDTO {
    private String friend;
    private String friendOf;

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getFriendOf() {
        return friendOf;
    }

    public void setFriendOf(String friendOf) {
        this.friendOf = friendOf;
    }
}
