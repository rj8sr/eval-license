package com.eval.licenses.response;

public class ResponseBuilder {

	private ResponseBuilder() {

	}

	public static BaseApiResponse getSuccessResponseForCreation(Object responseData) {

		BaseApiResponse baseApiResponse = new BaseApiResponse();
		baseApiResponse.setResponseStatus(new ResponseStatus(201));
		baseApiResponse.setResponseData(responseData);

		return baseApiResponse;
	}

	public static BaseApiResponse resourceNotFound() {
		BaseApiResponse baseApiResponse = new BaseApiResponse();
		baseApiResponse.setResponseStatus(new ResponseStatus(404));
		baseApiResponse.setResponseData(null);
		return baseApiResponse;
	}

	public static BaseApiResponse getSuccessResponse(Object responseData) {
		BaseApiResponse baseApiResponse = new BaseApiResponse();
		baseApiResponse.setResponseStatus(new ResponseStatus(200));
		baseApiResponse.setResponseData(responseData);

		return baseApiResponse;
	}

}