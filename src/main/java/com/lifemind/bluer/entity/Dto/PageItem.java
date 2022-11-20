package com.lifemind.bluer.entity.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PageItem
 * @Description TODO
 * @Date 2022/11/15 19:17
 * @Created by senorisky
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageItem {
    private String pageId;
    private String name;
    private String text;//可以是文本或者图片的路径
}
