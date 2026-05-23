package org.heg.dto;

import java.util.List;

public class TechniquesResponse {
    public List<Technique> items;

    public TechniquesResponse() {}

    public TechniquesResponse(List<Technique> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "TechniquesResponse{" +
                "items=" + items +
                '}';
    }
}

