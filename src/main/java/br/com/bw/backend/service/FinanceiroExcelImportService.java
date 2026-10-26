package br.com.bw.backend.service;

import br.com.bw.backend.dto.response.FinanceiroImportResultDTO;
import br.com.bw.backend.entity.Financeiro;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.repository.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinanceiroExcelImportService {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final int COL_DESCRICAO = 0;
    private static final int COL_QUANTIDADE = 1;
    private static final int COL_VALOR = 2;
    private static final int COL_FRETE = 3;
    private static final int COL_VALOR_TOTAL = 4;
    private static final int COL_VALOR_FINAL_DESCONTO = 5;
    private static final int COL_VALOR_ENTRADA = 6;
    private static final int COL_RECEBER_ENTREGA = 7;
    private static final int COL_VALOR_RECEBIDO_FINAL = 8;
    private static final int COL_VALOR_TOTAL_LIQUIDO = 9;
    private static final int COL_DATA_PEDIDO = 10;
    private static final int COL_DATA_ENTREGA = 11;
    private static final int COL_CONTATO_CLIENTE = 12;
    private static final int COL_REGIAO_VENDA = 13;
    private static final int TOTAL_COLUNAS = 14;

    private final FinanceiroRepository financeiroRepository;
    private final DataFormatter dataFormatter = new DataFormatter();

    public FinanceiroImportResultDTO importarExcel(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessValidationException("Arquivo de importação não enviado ou vazio");
        }

        log.info("Importando registros financeiros a partir de planilha Excel - arquivo: {}", arquivo.getOriginalFilename());

        try (Workbook workbook = WorkbookFactory.create(arquivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            int linhaCabecalho = localizarLinhaCabecalho(sheet);
            if (linhaCabecalho == -1) {
                throw new BusinessValidationException(
                        "Não foi possível localizar o cabeçalho da planilha. Utilize o modelo gerado pela exportação");
            }

            List<String> erros = new ArrayList<>();
            int totalLinhasProcessadas = 0;
            int totalImportado = 0;

            for (int i = linhaCabecalho + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (linhaVazia(row)) {
                    continue;
                }

                totalLinhasProcessadas++;
                int numeroLinha = i + 1;

                try {
                    Financeiro financeiro = converterLinha(row);
                    financeiroRepository.save(financeiro);
                    totalImportado++;
                } catch (Exception e) {
                    log.warn("Erro ao importar linha {} da planilha financeiro: {}", numeroLinha, e.getMessage());
                    erros.add("Linha " + numeroLinha + ": " + e.getMessage());
                }
            }

            log.info("Importação de registros financeiros concluída - processadas: {}, importadas: {}, erros: {}",
                    totalLinhasProcessadas, totalImportado, erros.size());

            return new FinanceiroImportResultDTO(totalLinhasProcessadas, totalImportado, erros);
        } catch (IOException e) {
            log.error("Erro ao ler planilha de importação do financeiro", e);
            throw new BusinessValidationException("Não foi possível ler o arquivo enviado. Verifique se é um Excel (.xlsx) válido");
        }
    }

    private int localizarLinhaCabecalho(Sheet sheet) {
        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 10); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            String primeiraCelula = lerTexto(row.getCell(COL_DESCRICAO));
            if ("Descrição".equalsIgnoreCase(primeiraCelula)) {
                return i;
            }
        }
        return -1;
    }

    private boolean linhaVazia(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = 0; c < TOTAL_COLUNAS; c++) {
            if (!lerTexto(row.getCell(c)).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private Financeiro converterLinha(Row row) {
        Integer quantidade = lerInteiro(row.getCell(COL_QUANTIDADE));
        BigDecimal valor = lerDecimal(row.getCell(COL_VALOR));
        BigDecimal frete = lerDecimal(row.getCell(COL_FRETE));
        BigDecimal valorTotal = lerDecimal(row.getCell(COL_VALOR_TOTAL));

        if (quantidade == null) {
            throw new BusinessValidationException("Quantidade é obrigatória");
        }
        if (valor == null) {
            throw new BusinessValidationException("Valor é obrigatório");
        }
        if (frete == null) {
            throw new BusinessValidationException("Frete é obrigatório");
        }
        if (valorTotal == null) {
            throw new BusinessValidationException("Valor Total é obrigatório");
        }

        return Financeiro.builder()
                .descricao(lerStringOpcional(row.getCell(COL_DESCRICAO)))
                .quantidade(quantidade)
                .valor(valor)
                .frete(frete)
                .valorTotal(valorTotal)
                .valorFinalDesconto(lerDecimal(row.getCell(COL_VALOR_FINAL_DESCONTO)))
                .valorEntrada(lerDecimal(row.getCell(COL_VALOR_ENTRADA)))
                .receberEntrega(lerDecimal(row.getCell(COL_RECEBER_ENTREGA)))
                .valorRecebidoFinal(lerDecimal(row.getCell(COL_VALOR_RECEBIDO_FINAL)))
                .valorTotalLiquido(lerDecimal(row.getCell(COL_VALOR_TOTAL_LIQUIDO)))
                .dataPedido(lerData(row.getCell(COL_DATA_PEDIDO)))
                .dataEntrega(lerData(row.getCell(COL_DATA_ENTREGA)))
                .contatoCliente(lerStringOpcional(row.getCell(COL_CONTATO_CLIENTE)))
                .regiaoVenda(lerStringOpcional(row.getCell(COL_REGIAO_VENDA)))
                .build();
    }

    private String lerTexto(Cell cell) {
        if (cell == null) {
            return "";
        }
        return dataFormatter.formatCellValue(cell).trim();
    }

    private String lerStringOpcional(Cell cell) {
        String valor = lerTexto(cell);
        return (valor.isBlank() || valor.equals("-")) ? null : valor;
    }

    private Integer lerInteiro(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        String texto = lerTexto(cell);
        if (texto.isBlank() || texto.equals("-")) {
            return null;
        }
        try {
            return Integer.parseInt(texto.replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            throw new BusinessValidationException("Quantidade inválida: " + texto);
        }
    }

    private BigDecimal lerDecimal(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String texto = lerTexto(cell);
        if (texto.isBlank() || texto.equals("-")) {
            return null;
        }

        String numerico = texto
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("\\s", "")
                .trim();

        try {
            return new BigDecimal(numerico);
        } catch (NumberFormatException e) {
            throw new BusinessValidationException("Valor monetário inválido: " + texto);
        }
    }

    private LocalDate lerData(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String texto = lerTexto(cell);
        if (texto.isBlank() || texto.equals("-")) {
            return null;
        }
        try {
            return LocalDate.parse(texto, DATA_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessValidationException("Data inválida: " + texto + " (use o formato dd/MM/yyyy)");
        }
    }
}
