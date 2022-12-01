package com.lifemind.bluer.entity.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname ListItem
 * @Description TODO
 * @Date 2022/11/6 9:35
 * @Created by senorisky
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListData {
    private String colum;
    private ArrayList<String> items;//主要存name info

    public ListData(String colum) {
        this.colum = colum;
        items = new ArrayList<>();
    }

    public void addItem(String json) {
        items.add(json);
    }
}
