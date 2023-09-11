package com.web.common.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 页面对象，存储显示数据的页数对象
 * 
 * @since 2021.01.27
 * @author zhouhui
 */
@Getter
@Setter
@Schema(title = "分页信息")
public class WebPage {
	
	/** 当前页数，从1开始   */
	@Schema(title = "当前页数，从1开始")
	private Integer page;
	
	/** 每页显示的数量 */
	@Schema(title = "每页显示的数量")
	private Integer pageSize;
	
	/** 数据总条数 */
	@Schema(title = "数据总条数 ")
	private Integer pageCount;
	
	/** 数据开始的行数 （pageSize - 1）* pageSize */
	@Schema(title = "数据开始的行数 （pageSize - 1）* pageSize")
	private Integer pageStart;
	
	/** 数据结束的行数 pageSize * pageSize */
	@Schema(title = "数据结束的行数 pageSize * pageSize")
	private Integer pageEnd;
	
	/** 是否使用分页，true使用分页，false不适用分页 */
	@Schema(title = "是否使用分页，true使用分页，false不适用分页")
	private boolean isUsePage = true;
}
