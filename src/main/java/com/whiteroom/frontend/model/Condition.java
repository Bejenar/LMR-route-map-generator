package com.whiteroom.frontend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Condition {

    String condition;

    String expression;
}
