package com.tictacchess.backend.model;

import com.tictacchess.backend.repository.FriendshipRepository;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(Friendship.FriendshipId.class)
@Table(name="friendships")
public class Friendship {
    public static class FriendshipId implements Serializable {
        private Integer requesterId;
        private Integer recipientId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FriendshipId that = (FriendshipId) o;
            return Objects.equals(requesterId, that.requesterId) && Objects.equals(recipientId, that.recipientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requesterId, recipientId);
        }
    }

    @Id
    private Integer requesterId;

    @Id
    private Integer recipientId;

    private Boolean pending = true;
    private Boolean declined = false;

    public Friendship(){

    }
    public Friendship(Integer requesterId, Integer recipientId){
        this.requesterId = requesterId;
        this.recipientId = recipientId;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public Boolean getDeclined() {
        return declined;
    }

    public void setDeclined(Boolean declined) {
        this.declined = declined;
    }
}
