package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.repository.ReferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferenciaExcelExportService {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] HEADERS = {"Categoria", "Preço Referência", "Descrição", "Material Base", "Unidade", "Atualizado em"};
    private static final int[] LARGURAS_COLUNAS = {22, 16, 35, 23, 15, 18};
    private static final byte[] COR_CABECALHO = {(byte) 40, (byte) 60, (byte) 90};

    private final ReferenciaRepository referenciaRepository;

    public byte[] exportExcel() {
        log.info("Exportando tabela de preços referência para Excel");

        List<Referencia> referencias = referenciaRepository.findAll(Sort.by(Sort.Direction.ASC, "categoria", "descricao"));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tabela de Preços Referência");

            int linhaCabecalho = addTitle(workbook, sheet);
            addTable(workbook, sheet, referencias, linhaCabecalho);
            setColumnWidths(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Erro ao gerar Excel da tabela de preços referência", e);
            throw new IllegalStateException("Erro ao gerar Excel da tabela de preços referência", e);
        }
    }

    private int addTitle(Workbook workbook, Sheet sheet) {
        Font tituloFont = workbook.createFont();
        tituloFont.setBold(true);
        tituloFont.setFontHeightInPoints((short) 16);

        CellStyle tituloStyle = workbook.createCellStyle();
        tituloStyle.setFont(tituloFont);

        Row tituloRow = sheet.createRow(0);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("Tabela de Preços Referência");
        tituloCell.setCellStyle(tituloStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

        Font subtituloFont = workbook.createFont();
        subtituloFont.setItalic(true);
        subtituloFont.setFontHeightInPoints((short) 10);

        CellStyle subtituloStyle = workbook.createCellStyle();
        subtituloStyle.setFont(subtituloFont);

        Row subtituloRow = sheet.createRow(1);
        Cell subtituloCell = subtituloRow.createCell(0);
        subtituloCell.setCellValue("Gerado em: " + LocalDateTime.now().format(DATA_FORMATTER));
        subtituloCell.setCellStyle(subtituloStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, HEADERS.length - 1));

        return 3;
    }

    private void addTable(Workbook workbook, Sheet sheet, List<Referencia> referencias, int linhaCabecalho) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook, false);
        CellStyle moedaStyle = createCellStyle(workbook, true);

        Row headerRow = sheet.createRow(linhaCabecalho);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = linhaCabecalho + 1;

        if (referencias.isEmpty()) {
            Row emptyRow = sheet.createRow(rowIndex);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("Nenhuma referência de preço cadastrada.");
            emptyCell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, HEADERS.length - 1));
            return;
        }

        for (Referencia referencia : referencias) {
            Row row = sheet.createRow(rowIndex++);

            Cell categoriaCell = row.createCell(0);
            categoriaCell.setCellValue(referencia.getCategoria() != null ? referencia.getCategoria().getValor() : "-");
            categoriaCell.setCellStyle(cellStyle);

            Cell precoCell = row.createCell(1);
            if (referencia.getPrecoReferencia() != null) {
                precoCell.setCellValue(referencia.getPrecoReferencia().doubleValue());
            } else {
                precoCell.setCellValue("-");
            }
            precoCell.setCellStyle(moedaStyle);

            Cell descricaoCell = row.createCell(2);
            descricaoCell.setCellValue(referencia.getDescricao() != null ? referencia.getDescricao() : "-");
            descricaoCell.setCellStyle(cellStyle);

            Cell materialCell = row.createCell(3);
            materialCell.setCellValue(referencia.getMaterialBase() != null ? referencia.getMaterialBase() : "-");
            materialCell.setCellStyle(cellStyle);

            Cell unidadeCell = row.createCell(4);
            unidadeCell.setCellValue(referencia.getUnidade() != null ? referencia.getUnidade().getValor() : "-");
            unidadeCell.setCellStyle(cellStyle);

            Cell dataCell = row.createCell(5);
            dataCell.setCellValue(
                    referencia.getDataAtualizacao() != null ? referencia.getDataAtualizacao().format(DATA_FORMATTER) : "-");
            dataCell.setCellStyle(cellStyle);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(new XSSFColor(COR_CABECALHO, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorders(headerStyle);
        return headerStyle;
    }

    private CellStyle createCellStyle(Workbook workbook, boolean moeda) {
        Font cellFont = workbook.createFont();
        cellFont.setFontHeightInPoints((short) 9);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        applyBorders(cellStyle);

        if (moeda) {
            DataFormat formatoDados = workbook.createDataFormat();
            cellStyle.setDataFormat(formatoDados.getFormat("R$ #,##0.00"));
        }

        return cellStyle;
    }

    private void applyBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void setColumnWidths(Sheet sheet) {
        for (int i = 0; i < LARGURAS_COLUNAS.length; i++) {
            sheet.setColumnWidth(i, LARGURAS_COLUNAS[i] * 256);
        }
    }
}
