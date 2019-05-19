package com.user.mngmnt.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JqgridFilter {

    private String source;
    private String groupOp;
    private ArrayList<Rule> rules;

    public JqgridFilter(String source) {
        super();
        this.source = source;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Rule {
        private String junction;
        private String field;
        private String op;
        private String data;
    }
}
