package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.repository.ReferenciaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferenciaPdfExportService {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReferenciaRepository referenciaRepository;

    public byte[] exportarPdf() {
        log.info("Exportando tabela de preços referência para PDF");

        List<Referencia> referencias = referenciaRepository.findAll(Sort.by(Sort.Direction.ASC, "categoria", "descricao"));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 30, 30, 40, 30);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            adicionarTitulo(document);
            adicionarTabela(document, referencias);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            log.error("Erro ao gerar PDF da tabela de preços referência", e);
            throw new IllegalStateException("Erro ao gerar PDF da tabela de preços referência", e);
        }
    }

    private void adicionarTitulo(Document document) throws DocumentException {
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph titulo = new Paragraph("Tabela de Preços Referência", tituloFont);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
        Paragraph geradoEm = new Paragraph(
                "Gerado em: " + java.time.LocalDateTime.now().format(DATA_FORMATTER), subtituloFont);
        geradoEm.setSpacingAfter(15);
        document.add(geradoEm);
    }

    private void adicionarTabela(Document document, List<Referencia> referencias) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{2.2f, 1.6f, 3.5f, 2.3f, 1.5f, 1.8f});
        table.setWidthPercentage(100);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        String[] headers = {"Categoria", "Preço Referência", "Descrição", "Material Base", "Unidade", "Atualizado em"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(40, 60, 90));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (Referencia referencia : referencias) {
            table.addCell(criarCelula(referencia.getCategoria() != null ? referencia.getCategoria().getValor() : "-", cellFont));
            table.addCell(criarCelula(
                    referencia.getPrecoReferencia() != null
                            ? "R$ " + referencia.getPrecoReferencia().setScale(2, java.math.RoundingMode.HALF_UP)
                            : "-",
                    cellFont));
            table.addCell(criarCelula(referencia.getDescricao(), cellFont));
            table.addCell(criarCelula(referencia.getMaterialBase(), cellFont));
            table.addCell(criarCelula(referencia.getUnidade() != null ? referencia.getUnidade().getValor() : "-", cellFont));
            table.addCell(criarCelula(
                    referencia.getDataAtualizacao() != null ? referencia.getDataAtualizacao().format(DATA_FORMATTER) : "-",
                    cellFont));
        }

        if (referencias.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Nenhuma referência de preço cadastrada.", cellFont));
            emptyCell.setColspan(6);
            emptyCell.setPadding(8);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(emptyCell);
        }

        document.add(table);
    }
}
