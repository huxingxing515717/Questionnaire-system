package com.code.questionnaireSystem.service;

import javax.servlet.http.HttpServletRequest;

import com.code.questionnaireSystem.utils.Result;

public interface ParticipateService {

	Result getDeviceType(HttpServletRequest request);

	Result getIp(HttpServletRequest request);

	Result getAddress(HttpServletRequest request) throws Exception;

}
