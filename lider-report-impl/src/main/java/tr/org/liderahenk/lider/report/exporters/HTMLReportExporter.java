package tr.org.liderahenk.lider.report.exporters;

import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class HTMLReportExporter {

	public static String export(IReportView view, List<Object[]> resultList) {
		int tableWidth = 0;
		String html = "<html><body><table border=\"1\" style=\"width:#TABLEWIDTH#px\"><tr>";

		// Table headers
		ArrayList<IReportViewColumn> columns = new ArrayList<IReportViewColumn>(view.getViewColumns());
		int[] colWidths = new int[view.getViewColumns().size()];
		int[] colIndices = new int[view.getViewColumns().size()];
		for (int i = 0; i < columns.size(); i++) {
			IReportViewColumn column = columns.get(i);
			html += "<td style=\"width:" + column.getWidth() + "px\"><strong>" + column.getReferencedCol().getName()
					+ "</strong></td>";
			colWidths[i] = column.getWidth();
			colIndices[i] = column.getReferencedCol().getColumnOrder() - 1;
			tableWidth += column.getWidth();
		}
		html += "</tr>";

		// Table rows
		for (Object[] row : resultList) {
			html += "<tr>";
			for (int index : colIndices) {
				html += "<td>";
				html += (index >= row.length || row[index] == null) ? " " : row[index].toString();
				html += "</td>";
			}
			html += "</tr>";
		}

		// Finalise table
		html += "</body></html>";
		return html.replaceFirst("#TABLEWIDTH#", tableWidth + "");
	}

}
