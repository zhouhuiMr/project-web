package com.web.generator.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.basic.generator.service.impl.GeneratorServiceImpl;
import com.web.common.basic.generator.api.GeneratorApi;
import com.web.common.basic.generator.entity.GeneratorConfig;
import com.web.common.basic.generator.entity.GeneratorDatabase;
import com.web.common.basic.generator.entity.GeneratorTable;
import com.web.common.result.R;
import com.web.common.result.ResultEnum;
import com.web.service.tool.ResponseResultTool;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "代码生成")
@Controller
@RequestMapping(path = "/basic/generation")
public class GeneratorController implements GeneratorApi{
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private GeneratorServiceImpl generatorServiceImpl;

	@Operation(summary = "获取数据库列表")
	@Override
	public R<ArrayList<GeneratorDatabase>> getDatabaseList() {
		return generatorServiceImpl.getDatabaseList();
	}

	@Operation(summary = "获取数据库对应的表")
	@Override
	public R<ArrayList<GeneratorTable>> getTableList(@RequestBody GeneratorConfig config) {
		R<ArrayList<GeneratorTable>> json = new R<>();
		if(!StringUtils.hasText(config.getDatabaseName())) {
			json.setResultEnum(ResultEnum.ERROR);
			json.setMessage("请选择数据库名称！");
			return json;
		}
		json = generatorServiceImpl.getTableList(config);
		return json;
	}

	@Operation(summary = "代码生成")
	@Override
	public void createGeneration(@RequestBody GeneratorConfig config) {
		R<byte[]> result = generatorServiceImpl.createGeneration(config);
		if(!ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
			R<Object> json = new R<>();
			json.setCode(result.getCode());
			json.setMessage(result.getMessage());
			ResponseResultTool.setWebResponseJson(response, json);
			return;
		}
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-disposition", "attachment;filename=" + config.getModelName() + ".zip");
		try {
			ServletOutputStream out = response.getOutputStream();
			out.write(result.getData());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
