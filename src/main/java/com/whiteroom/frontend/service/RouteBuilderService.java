package com.whiteroom.frontend.service;

import com.whiteroom.frontend.model.Choice;
import com.whiteroom.frontend.model.Condition;
import com.whiteroom.frontend.model.Day;
import com.whiteroom.frontend.model.Label;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RouteBuilderService {

    @Value("${lmr.days}")
    private List<String> files;

    public List<Day> getDays() {
        List<Day> days = new ArrayList<>();

        files.stream()
            .map(this::getScenario)
            .forEach(s -> {
                Day day = Day.builder()
                    .value(s)
                    .labels(getLabels(s))
                    .build();
                days.add(day);
            });

        return days;
    }


    @SneakyThrows
    private String getScenario(final String filename) {
        return Files.lines(Path.of(filename))
            .collect(Collectors.joining("\n"));
    }

    @SneakyThrows
    private List<Label> getLabels(String text) {
        List<Label> labels = new ArrayList<>();

        Pattern textPattern = Pattern.compile("label(?:(?!label).)*", Pattern.DOTALL);
        Matcher textMatcher = textPattern.matcher(text);

        Pattern labelPattern = Pattern.compile("label((?!label).*):");
        Matcher labelMatcher = labelPattern.matcher(text);

        while (textMatcher.find() && labelMatcher.find()) {
            String labelText = textMatcher.group();
            Label label = Label.builder()
                .name(labelMatcher.group(1).trim())
                .choices(getChoices(labelText))
                .conditions(getConditions(labelText))
                .build();
            labels.add(label);
        }

        return  labels;

    }

    private List<Choice> getChoices(String labelText) {
        List<Choice> choices = new ArrayList<>();

        Pattern menuPattern = Pattern.compile("menu:([^\\{]*\\{[^\\{]*\\})");
        Matcher menuMatcher = menuPattern.matcher(labelText);
        while (menuMatcher.find()) {
            Choice choice = Choice.builder()
                .value(menuMatcher.group(1))
                .build();

            choices.add(choice);
        }

        return choices;

    }

    private List<Condition> getConditions(String labelText) {
        List<Condition> conditions = new ArrayList<>();

        Pattern ifPattern = Pattern.compile("(if [^\\{]*)(\\{[^\\{]*\\})");
        Matcher ifMatcher = ifPattern.matcher(labelText);
        while (ifMatcher.find()){
            Condition condition = Condition.builder()
                .condition(ifMatcher.group(1))
                .expression(ifMatcher.group(2))
                .build();

            conditions.add(condition);
        }

        return conditions;
    }




}
