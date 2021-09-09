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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RouteBuilderService {

    @Value("${lmr.days}")
    private List<String> files;

    public Collection<Label> traceRoute() {
        Map<String, Label> labels = new LinkedHashMap<>();

        getDays().stream()
            .flatMap(d -> d.getLabels().stream())
            .forEach(l -> labels.put(l.getName(), l));

        labels.forEach((k, v) -> {
            System.out.println(k);
            Pattern jumpPattern = Pattern.compile("jump (.*)");
            Matcher jumpMatcher = jumpPattern.matcher(v.getText());
            while (jumpMatcher.find()) {
                System.out.println("matcher:" + jumpMatcher.group());
                v.getNext().add(labels.get(jumpMatcher.group(1)));
            }
        });

        Iterator<Label> iterator = labels.values().iterator();

        while(iterator.hasNext()){
            Label curr = iterator.next();
            if (curr.getNext().size() == 0 && iterator.hasNext()) {
                curr.getNext().add(iterator.next());
            }
        }

        return labels.values();

//        Label head = labels.get("start");
//        while (head != null) {
//            System.out.println(head.getName() + " ->");
//            if (head.getNext().size() > 0) {
//                head = head.getNext().get(0);
//            } else {
//                head = null;
//            }
//        }
    }

    public List<Day> getDays() {
        List<Day> days = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        files.stream()
            .map(this::getScenario)
            .forEach(s -> {
                Day day = Day.builder()
                    .value(s)
                    .labels(getLabels(s))
                    .name("День-" + count.incrementAndGet())
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
                .text(labelText)
                .name(labelMatcher.group(1).trim())
                .choices(getChoices(labelText))
                .conditions(getConditions(labelText))
                .build();
            labels.add(label);
        }

        return labels;

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

        Pattern ifPattern = Pattern.compile("(if [^\\{]*)\\{([^\\{\\}]*)\\}(?:.*(else[^\\{]*)\\{([^\\{\\}]*)\\})?", Pattern.DOTALL);
        Matcher ifMatcher = ifPattern.matcher(labelText);
        while (ifMatcher.find()) {
            Condition.ConditionBuilder condition = Condition.builder()
                .ifCondition(ifMatcher.group(1).trim().replaceAll(" +", " "))
                .ifBody(ifMatcher.group(2).trim().replaceAll(" +", " "));

            if (ifMatcher.group(3) != null) {
                condition = condition
                    .elseCondition(ifMatcher.group(3).trim().replaceAll(" +", " "))
                    .elseBody(ifMatcher.group(4).trim().replaceAll(" +", " "));
            }

            conditions.add(condition.build());
        }

        return conditions;
    }


}
