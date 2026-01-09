package com.garosugil.route.model;

import com.garosugil.domain.location.dto.RouteSearchRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RouteGraph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<List<Edge>> adj = new ArrayList<>();

    public int addNode(RouteSearchRequest.Location loc, String label) {
        int id = nodes.size();
        nodes.add(new Node(id, loc, label));
        adj.add(new ArrayList<>());
        return id;
    }

    public void addEdge(Edge e) {
        adj.get(e.getFrom()).add(e);
    }
}
