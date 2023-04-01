package ru.papapers.optimalchoice.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Purpose {

    private String name;

    private List<String> properties = new ArrayList<String>();

}
