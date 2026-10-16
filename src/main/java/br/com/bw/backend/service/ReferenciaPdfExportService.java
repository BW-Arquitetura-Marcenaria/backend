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
}
