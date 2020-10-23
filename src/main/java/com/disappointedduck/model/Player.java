package com.disappointedduck.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    String id;
    String name;
    RoleEnum role;
    String extendedInfo = "";
}
