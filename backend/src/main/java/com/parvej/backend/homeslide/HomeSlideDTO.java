package com.parvej.backend.homeslide;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeSlideDTO {
    private String  title;
    private String  description;
    private String  titleBn;
    private String  descriptionBn;
    private String  linkUrl;
    private Integer displayOrder;
    private Boolean active;
}