package com.web.common.basic.catalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件目录对应的文件
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-16
 */
@Getter
@Setter
@Schema(title = "BasicFileCatalogFileEntity对象", description = "文件目录对应的文件")
public class BasicFileCatalogFileEntity extends BasicFileCatalogFile{

    private static final long serialVersionUID = 1L;
    
    /** 当前页数 */
	@Schema(title = "当前页数")
	private int page = 1;
	
	/** 每页大小，小于0不进行分页 */
	@Schema(title = "每页大小")
	private int size = 10;
}
