package com.namtg.egovernment.dto.response;

public interface ResponseCase {
    ResponseStatus SUCCESS = new ResponseStatus(1000, "SUCCESS");
    ResponseStatus ERROR = new ResponseStatus(4, "ERROR");

    ResponseStatus NOT_LOGIN = new ResponseStatus(1400, "NOT LOGIN");

    ResponseStatus WRONG_PASSWORD = new ResponseStatus(1500, "WRONG PASSWORD");

    ResponseStatus EMAIL_EXIST = new ResponseStatus(1600, "EMAIL EXIST");

    ResponseStatus COMMENT_NOT_FOUND = new ResponseStatus(1601, "COMMENT NOT FOUND");

    ResponseStatus EMAIL_NOT_FOUND = new ResponseStatus(1602, "EMAIL NOT FOUND");

    ResponseStatus NEWS_EXIST = new ResponseStatus(1603, "NEWS EXIST");

    ResponseStatus POST_EXIST = new ResponseStatus(1604, "POST EXIST");


}
