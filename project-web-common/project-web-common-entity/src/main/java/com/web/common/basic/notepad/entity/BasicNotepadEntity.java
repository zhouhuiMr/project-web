package com.web.common.basic.notepad.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 记事本（包含代办事项）
 * </p>
 *
 * @author zhouhui
 * @since 2023-02-07
 */
@Getter
@Setter
@Schema(title = "BasicNotepadEntity对象", description = "记事本（包含代办事项）")
public class BasicNotepadEntity extends BasicNotepad{

    private static final long serialVersionUID = 1L;
    
    /** 事件类型描述 */
    @Schema(title = "事件类型描述")
    private String typeName;
    
    /** 开始日期 */
    @Schema(title = "开始日期")
    private String startDate;
    
    /** 结束日期 */
    @Schema(title = "结束日期")
    private String endDate;
    
    /** 当前的年月，格式yyyy-MM */
    @Schema(title = "当前的年月，格式yyyy-MM")
    private String month;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
