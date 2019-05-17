package com.code.questionnaireSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code.questionnaireSystem.service.SurverService;
import com.code.questionnaireSystem.utils.QrCodeUtils;
import com.code.questionnaireSystem.utils.Result;
import com.code.questionnaireSystem.utils.ResultCode;

/**
 * 问卷controller层
 * 
 * @author 皮艳萍
 *
 */
@RestController
@RequestMapping("/surver")
public class SurverController {
	@Autowired
	private SurverService surverService;

	/**
	 * 新增问卷
	 * 
	 * @param userId
	 * @return
	 */
	@PostMapping("/add")
	public Result insert(String userId) {
		return surverService.insert(userId);
	}

	/**
	 * 根据问卷id更新问卷title,desription
	 * 
	 * @param title
	 * @param description
	 * @param id
	 * @return
	 */
	@PostMapping("/update")
	public Result update(String title, String description, String id) {
		return surverService.update(title, description, id);
	}

	/**
	 * 问卷发布时，更新问卷的填写截止时间和是否限制同一ip填写多次
	 */
	@PostMapping("/updatePublish")
	public Result updateEndTime(String surverId, String EndTime, int limitIP) {
		return surverService.updateEndTime(surverId, EndTime, limitIP);
	}

	/**
	 * 根据问卷id查询该问卷的信息
	 * 
	 * @param surverId
	 * @return
	 */
	@GetMapping("/search")
	public Result search(String surverId) {
		return surverService.search(surverId);
	}

	/**
	 * 根据userid查询该用户所创建的所有问卷
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/seachByUserId")
	public Result seachByUserId(String userId) {
		return surverService.seachByUserId(userId);
	}

	/**
	 * 根据问卷id 删除该问卷
	 * 
	 * @param surverId
	 * @return
	 */
	@PostMapping("/deleteById")
	public Result deleteById(String surverId) {
		return surverService.deleteById(surverId);
	}

	@GetMapping("/getQRimage")
	public Result productcode(String url) {
		String imagePath = QrCodeUtils.zxingCodeCreate(url, "E:/CODE/Questionnaire-system/front/static/img/QR/", 500,
				"E:/CODE/Questionnaire-system/material/img/logo.jpg");
		if (imagePath != null) {
			return Result.success(imagePath);
		} else {
			return Result.failure(ResultCode.FAIL);
		}
	}

	/**
	 * 根据问卷类型筛选问卷
	 * 
	 * @param surverTypeId
	 * @return
	 */

	@GetMapping("selectSurvers")
	public Result selectSurvers(String surverTypeId, String surverTitle) {
		return surverService.selectSurvers(surverTypeId, surverTitle);
	}
}
