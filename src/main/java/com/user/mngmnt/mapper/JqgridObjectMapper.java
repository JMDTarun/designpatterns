package com.user.mngmnt.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.mngmnt.model.JqgridFilter;

public class JqgridObjectMapper {

    public static JqgridFilter map(String jsonString) {

        if (jsonString != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                return mapper.readValue(jsonString, JqgridFilter.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
