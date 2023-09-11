package com.web.service.tool;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.common.result.R;

public class ResponseResultTool {
	
	private ResponseResultTool() {}

	/**
	 * 返回json数据
	 * @param res 
	 * @param json 结果信息
	 *
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static void setWebResponseJson(HttpServletResponse res,R<?> result) {
		res.setStatus(HttpStatus.OK.value());
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String json = mapper.writeValueAsString(result);
			writer = res.getWriter();
			writer.print(json);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
