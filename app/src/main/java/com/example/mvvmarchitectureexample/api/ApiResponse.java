package com.example.mvvmarchitectureexample.api;

import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class ApiResponse<T> {
    private static final Pattern LINK_PATTERN = Pattern
            .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"");
    private static final Pattern PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)");
    private final int NUMBER_OF_LINKS = 2;
    private final int INTERNAL_SERVER_ERROR = 500;
    private static final String NEXT_LINK = "next";
    private final int statusCode;
    private  T body;
    private  String errorMessage;
    private Map<String, String> links;

    public ApiResponse(Throwable error) {
        this.statusCode = INTERNAL_SERVER_ERROR;
        this.body = null;
        this.errorMessage = error.getMessage();
        this.links = Collections.emptyMap();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ApiResponse(Response<T> response) {
        statusCode = response.code();

        setUptResponse(response);

        String linkHeader = response.headers().get("link");

        setUpLinks(linkHeader);
    }

    private void setUptResponse(Response<T> response) {
        if(response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;

            if(response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException exception) {
                    Log.d(exception.getMessage(), "Error while parsing response");
                }
            }

            if((message == null) || (message.trim().length() == 0)) {
                message = response.message();
            }

            errorMessage = message;
            body = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpLinks(String linkHeader) {
        if(linkHeader == null) {
            links = Collections.emptyMap();
        } else {
            links = new ArrayMap<>();
            Matcher matcher = LINK_PATTERN.matcher(linkHeader);

            while (matcher.find()) {
                int count = matcher.groupCount();

                if(count == NUMBER_OF_LINKS) {
                    links.put(matcher.group(2), matcher.group(1));
                }
            }
        }
    }

    public boolean isSuccessful() {
        boolean isSuccess = (statusCode >= 200) && (statusCode < 300);
        return isSuccess;
    }

    public Integer getNextPage() {
        String nextPage = links.get(NEXT_LINK);

        if(nextPage == null) {
            return null;
        }

        Matcher matcher = PAGE_PATTERN.matcher(nextPage);

        if((!matcher.find()) || (matcher.groupCount() != 1)) {
            return null;
        }

        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException exception) {
            Log.d("Cannot parse next", nextPage);
            return null;
        }
    }

    public static String getNextLink() {
        return NEXT_LINK;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getBody() {
        return body;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, String> getLinks() {
        return links;
    }
}
