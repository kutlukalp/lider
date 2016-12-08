package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.enums.PdfReportParamType;

/**
 * Request class for report generation.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportGenerationRequest extends IRequest {

	Long getViewId();

	Map<String, Object> getParamValues();

	PdfReportParamType getTopLeft();

	String getTopLeftText();

	PdfReportParamType getTopRight();

	String getTopRightText();

	PdfReportParamType getBottomLeft();

	String getBottomLeftText();

	PdfReportParamType getBottomRight();

	String getBottomRightText();

}
