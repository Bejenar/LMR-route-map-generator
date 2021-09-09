package com.whiteroom.frontend.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Day {

    private List<Label> labels;

    private String value;

    private String name;
}
