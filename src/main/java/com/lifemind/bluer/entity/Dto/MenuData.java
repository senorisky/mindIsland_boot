package com.lifemind.bluer.entity.Dto;

import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.View;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname MenuData
 * @Description TODO
 * @Date 2022/11/6 0:10
 * @Created by senorisky
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuData {
    private ArrayList<Note> notes = new ArrayList<>();
    private Integer length = 0;
    public void addParentNote(Note note) {
        notes.add(note);
        length += 1;
    }
    public void addParentChildren(List<View> children) {
        notes.get(length - 1).setChildren(children);
    }

}
