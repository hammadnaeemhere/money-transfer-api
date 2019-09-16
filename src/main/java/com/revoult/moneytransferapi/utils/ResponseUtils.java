package com.revoult.moneytransferapi.utils;

import com.revoult.moneytransferapi.constant.ResponseCode;
import com.revoult.moneytransferapi.dto.Response;

public final class ResponseUtils {

    /**
     * Creates the Response with Response Code.
     *
     * @param responseCode ResponseCode
     * @return Response
     */
    public static Response createResponse(ResponseCode responseCode) {
        return new Response(responseCode);
    }

    /**
     * Creates the Response with Response Code and Data that is to be returned.
     *
     * @param responseCode ResponseCode
     * @param data         Any type of Data
     * @return Response
     */
    public static Response createResponse(ResponseCode responseCode, Object data) {
        return new Response(responseCode, data);
    }

    /**
     * Creates Response with code by updating the response description with placeholder values provided.
     *
     * @param responseCode ResponseCode
     * @param args         Placeholder values
     * @return Response
     */
    public static Response createResponseWithArgs(ResponseCode responseCode, Object... args) {
        return new Response(responseCode.getCode(), StringUtils.formatString(responseCode.getDescription(), args));
    }

    /**
     * Creates Response with code by updating the response description with placeholder values provided.
     *
     * @param responseCode ResponseCode
     * @param args         Placeholder values
     * @param data         Data to be returned
     * @return Response
     */
    public static Response createResponseDataWithArgs(ResponseCode responseCode, Object data, Object... args) {
        return new Response(responseCode.getCode(), StringUtils.formatString(responseCode.getDescription(), args), data);
    }

}
