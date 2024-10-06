package com.apromore.compliance_centre.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

    private int status;
    private String message;
    private T data;
    private List<ResponseError> errorDetails;
    private String timestamp;

    private Response(HttpStatus status, T data, List<ResponseError> errorDetails) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        this.status = status.value();
        this.message = status.name();
        this.timestamp = df.format(new Date());
        this.data = data;
        this.errorDetails = errorDetails;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(HttpStatus.OK, data, null);
    }

    public static <T> Response<T> error(HttpStatus status, List<ResponseError> errorDetails) {
        return new Response<>(status, null, errorDetails);
    }

    public static <T> Response<T> badRequest(List<ResponseError> errorDetails) {
        return error(HttpStatus.BAD_REQUEST, errorDetails);
    }

    public static <T> Response<T> notFound(List<ResponseError> errorDetails) {
        return error(HttpStatus.NOT_FOUND, errorDetails);
    }

    public static <T> Response<T> duplicateEntity(List<ResponseError> errorDetails) {
        return error(HttpStatus.CONFLICT, errorDetails);
    }
}
