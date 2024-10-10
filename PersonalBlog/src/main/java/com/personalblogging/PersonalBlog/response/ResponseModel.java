package com.personalblogging.PersonalBlog.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel implements Serializable {
    @Setter
    @Getter
    @SerializedName("data")
    private Object data;
    private String message;

    private Integer totalElements;
    private Integer pageNo;
    private Integer pageSize;

    private Integer totalPages;

    public void setError(String message) {
    }
}
