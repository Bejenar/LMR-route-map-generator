package com.whiteroom.frontend.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Label {

    private String name;

    private String text;

    private List<Choice> choices;

    private List<Condition> conditions;

    private final List<Label> next = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
