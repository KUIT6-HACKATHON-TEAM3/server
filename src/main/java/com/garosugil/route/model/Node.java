package com.garosugil.route.model;

import com.garosugil.dto.route.RouteSearchRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Node {
    private final int id;
    private final RouteSearchRequest.Location loc;
    private final String label; // 디버그용: START, END, R:123:S 등
}
