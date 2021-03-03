package com.mikovskycloud.workly.web.v1.sections.payload;

import com.mikovskycloud.workly.domain.Section;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ApiModel(value = "SectionResponse")
public class SectionResponse {

    @ApiModelProperty(required = true, position = 1)
    Long id;

    @ApiModelProperty(required = true, position = 2)
    String name;

    public static SectionResponse fromSection(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .build();
    }

}
