package projeto.psringboot.utilitario;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Retorna nosso PDF em Byte para download no navegador */
	@SuppressWarnings("rawtypes")
	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext){

		/*
		 * Cria a lista de dados para o relatorio com nossa lista de objetos para
		 * imrpimir
		 */
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);

		/* Carregar o caminho do arquivo jasper compilado */
		String caminhoJasper = servletContext.getRealPath("relatorios") 
				+ File.separator + relatorio + ".jasper";
		
		/*Carrega o arquivo jasper passando os dados*/
		JasperPrint impressoraJasper;
		byte[] pdf = null;
		try {
			impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap<>(), jrbcds);
			/*Exporta para Byte para fazer download do PDF*/
			pdf = JasperExportManager.exportReportToPdf(impressoraJasper);
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return pdf;
	}

}
