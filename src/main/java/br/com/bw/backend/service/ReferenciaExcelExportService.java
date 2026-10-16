package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.repository.ReferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferenciaExcelExportService {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] HEADERS = {"Categoria", "Preço Referência", "Descrição", "Material Base", "Unidade", "Atualizado em"};

    private final ReferenciaRepository referenciaRepository;

    public byte[] exportarExcel() {
        log.info("Exportando tabela de preços referência para Excel");

        List<Referencia> referencias = referenciaRepository.findAll(Sort.by(Sort.Direction.ASC, "categoria", "descricao"));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tabela de Preços Referência");

            CellStyle headerStyle = criarEstiloCabecalho(workbook);
            CellStyle moedaStyle = criarEstiloMoeda(workbook);

            criarCabecalho(sheet, headerStyle);
            preencherDados(sheet, referencias, moedaStyle);
            ajustarLargurasColunas(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Erro ao gerar Excel da tabela de preços referência", e);
            throw new IllegalStateException("Erro ao gerar Excel da tabela de preços referência", e);
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return headerStyle;
    }

    private CellStyle criarEstiloMoeda(Workbook workbook) {
        CellStyle moedaStyle = workbook.createCellStyle();
        DataFormat formatoDados = workbook.createDataFormat();
        moedaStyle.setDataFormat(formatoDados.getFormat("R$ #,##0.00"));
        return moedaStyle;
    }
}
