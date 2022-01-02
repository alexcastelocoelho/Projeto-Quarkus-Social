package io.github.alexcastelocoelho.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    private Integer followerscount;
    private List<FollowerResponse> content;
}
