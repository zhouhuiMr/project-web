package ${package.Entity};

import io.swagger.v3.oas.annotations.media.Schema;
<#if entityLombokModel>
import lombok.Getter;
import lombok.Setter;
</#if>

/**
 * <p>
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Getter
@Setter
</#if>
<#if swagger>
@Schema(title = "${entity}Entity对象", description = "${table.comment!}")
</#if>
public class ${entity}Entity extends ${entity}{
<#if entitySerialVersionUID>

    private static final long serialVersionUID = 1L;
</#if>
}
