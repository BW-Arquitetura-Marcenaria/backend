package br.com.bw.backend.service;

import br.com.bw.backend.entity.Financeiro;
import br.com.bw.backend.repository.FinanceiroRepository;
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
@Slf4j
@RequiredArgsConstructor
public class FinanceiroExcelExportService {

    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] HEADERS = {
            "Descrição", "Quantidade", "Valor", "Frete", "Valor Total", "Valor Final c/ Desconto",
            "Valor Entrada", "Receber na Entrega", "Valor Recebido Final", "Valor Total Líquido",
            "Data do Pedido", "Data da Entrega", "Contato do Cliente", "Região da Venda"
    };
    private static final int[] LARGURAS_COLUNAS = {30, 12, 14, 12, 14, 20, 14, 16, 18, 16, 16, 16, 20, 20};
    private static final byte[] COR_CABECALHO = {(byte) 40, (byte) 60, (byte) 90};

    private final FinanceiroRepository financeiroRepository;

    public byte[] exportarExcel() {
        log.info("Exportando registros financeiros para Excel");

        List<Financeiro> financeiros = financeiroRepository.findAll(Sort.by(Sort.Direction.DESC, "dataPedido"));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Financeiro");

            int linhaCabecalho = adicionarTitulo(workbook, sheet);
            adicionarTabela(workbook, sheet, financeiros, linhaCabecalho);
            definirLargurasColunas(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Erro ao gerar Excel dos registros financeiros", e);
            throw new IllegalStateException("Erro ao gerar Excel dos registros financeiros", e);
        }
    }

    private int adicionarTitulo(Workbook workbook, Sheet sheet) {
        Font tituloFont = workbook.createFont();
        tituloFont.setBold(true);
        tituloFont.setFontHeightInPoints((short) 16);

        CellStyle tituloStyle = workbook.createCellStyle();
        tituloStyle.setFont(tituloFont);

        Row tituloRow = sheet.createRow(0);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("Financeiro");
        tituloCell.setCellStyle(tituloStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

        Font subtituloFont = workbook.createFont();
        subtituloFont.setItalic(true);
        subtituloFont.setFontHeightInPoints((short) 10);

        CellStyle subtituloStyle = workbook.createCellStyle();
        subtituloStyle.setFont(subtituloFont);

        Row subtituloRow = sheet.createRow(1);
        Cell subtituloCell = subtituloRow.createCell(0);
        subtituloCell.setCellValue("Gerado em: " + LocalDateTime.now().format(DATA_HORA_FORMATTER));
        subtituloCell.setCellStyle(subtituloStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, HEADERS.length - 1));

        return 3;
    }

    private void adicionarTabela(Workbook workbook, Sheet sheet, List<Financeiro> financeiros, int linhaCabecalho) {
        CellStyle headerStyle = criarEstiloCabecalho(workbook);
        CellStyle cellStyle = criarEstiloCelula(workbook, false);
        CellStyle moedaStyle = criarEstiloCelula(workbook, true);

        Row headerRow = sheet.createRow(linhaCabecalho);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = linhaCabecalho + 1;

        if (financeiros.isEmpty()) {
            Row emptyRow = sheet.createRow(rowIndex);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("Nenhum registro financeiro cadastrado.");
            emptyCell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, HEADERS.length - 1));
            return;
        }

        for (Financeiro financeiro : financeiros) {
            Row row = sheet.createRow(rowIndex++);

            Cell descricaoCell = row.createCell(0);
            descricaoCell.setCellValue(financeiro.getDescricao() != null ? financeiro.getDescricao() : "-");
            descricaoCell.setCellStyle(cellStyle);

            Cell quantidadeCell = row.createCell(1);
            if (financeiro.getQuantidade() != null) {
                quantidadeCell.setCellValue(financeiro.getQuantidade());
            } else {
                quantidadeCell.setCellValue("-");
            }
            quantidadeCell.setCellStyle(cellStyle);

            adicionarCelulaMoeda(row, 2, financeiro.getValor(), moedaStyle);
            adicionarCelulaMoeda(row, 3, financeiro.getFrete(), moedaStyle);
            adicionarCelulaMoeda(row, 4, financeiro.getValorTotal(), moedaStyle);
            adicionarCelulaMoeda(row, 5, financeiro.getValorFinalDesconto(), moedaStyle);
            adicionarCelulaMoeda(row, 6, financeiro.getValorEntrada(), moedaStyle);
            adicionarCelulaMoeda(row, 7, financeiro.getReceberEntrega(), moedaStyle);
            adicionarCelulaMoeda(row, 8, financeiro.getValorRecebidoFinal(), moedaStyle);
            adicionarCelulaMoeda(row, 9, financeiro.getValorTotalLiquido(), moedaStyle);

            Cell dataPedidoCell = row.createCell(10);
            dataPedidoCell.setCellValue(
                    financeiro.getDataPedido() != null ? financeiro.getDataPedido().format(DATA_FORMATTER) : "-");
            dataPedidoCell.setCellStyle(cellStyle);

            Cell dataEntregaCell = row.createCell(11);
            dataEntregaCell.setCellValue(
                    financeiro.getDataEntrega() != null ? financeiro.getDataEntrega().format(DATA_FORMATTER) : "-");
            dataEntregaCell.setCellStyle(cellStyle);

            Cell contatoCell = row.createCell(12);
            contatoCell.setCellValue(financeiro.getContatoCliente() != null ? financeiro.getContatoCliente() : "-");
            contatoCell.setCellStyle(cellStyle);

            Cell regiaoCell = row.createCell(13);
            regiaoCell.setCellValue(financeiro.getRegiaoVenda() != null ? financeiro.getRegiaoVenda() : "-");
            regiaoCell.setCellStyle(cellStyle);
        }
    }

    private void adicionarCelulaMoeda(Row row, int indice, java.math.BigDecimal valor, CellStyle moedaStyle) {
        Cell cell = row.createCell(indice);
        if (valor != null) {
            cell.setCellValue(valor.doubleValue());
        } else {
            cell.setCellValue("-");
        }
        cell.setCellStyle(moedaStyle);
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
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
        aplicarBordas(headerStyle);
        return headerStyle;
    }

    private CellStyle criarEstiloCelula(Workbook workbook, boolean moeda) {
        Font cellFont = workbook.createFont();
        cellFont.setFontHeightInPoints((short) 9);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        aplicarBordas(cellStyle);

        if (moeda) {
            DataFormat formatoDados = workbook.createDataFormat();
            cellStyle.setDataFormat(formatoDados.getFormat("R$ #,##0.00"));
        }

        return cellStyle;
    }

    private void aplicarBordas(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void definirLargurasColunas(Sheet sheet) {
        for (int i = 0; i < LARGURAS_COLUNAS.length; i++) {
            sheet.setColumnWidth(i, LARGURAS_COLUNAS[i] * 256);
        }
    }
}
