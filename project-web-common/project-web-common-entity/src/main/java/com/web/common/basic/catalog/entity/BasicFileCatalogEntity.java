package com.web.common.basic.catalog.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件目录
 * </p>
 *
 * @author zhouhui
 * @since 2023-03-04
 */
@Getter
@Setter
@Schema(title = "BasicFileCatalogEntity对象", description = "文件目录")
public class BasicFileCatalogEntity extends BasicFileCatalog{

    private static final long serialVersionUID = 1L;
    
    /** 子目录 */
	@Schema(title = "子目录")
    private List<BasicFileCatalog> children;
}
