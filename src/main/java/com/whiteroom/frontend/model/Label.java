package com.whiteroom.frontend.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Label {

    String name;

    List<Choice> choices;

    List<Condition> conditions;
}
