package com.chiricker.areas.users.models.view;

import org.springframework.data.domain.Page;

public class PeerSearchResultViewModel {

    private String query;

    private Page<FollowerViewModel> peers;

    public PeerSearchResultViewModel() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Page<FollowerViewModel> getPeers() {
        return peers;
    }

    public void setPeers(Page<FollowerViewModel> peers) {
        this.peers = peers;
    }
}
