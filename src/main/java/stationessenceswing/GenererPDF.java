package stationessenceswing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;

public class GenererPDF {

    public static File genererRecuEntretien(Entretien ent, ServiceEnt serv) {
        try {
            File fichier = new File("recu_" + ent.getNumEntr() + ".pdf");
            PdfWriter writer = new PdfWriter(fichier.getAbsolutePath());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            doc.add(new Paragraph("STATION ESSENCE").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Recu d'Entretien").setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addCell(new Cell().add(new Paragraph("Numero Entretien :")).setBold());
            table.addCell(new Cell().add(new Paragraph(ent.getNumEntr())));

            table.addCell(new Cell().add(new Paragraph("Service :")).setBold());
            table.addCell(new Cell().add(new Paragraph(serv.getService())));

            table.addCell(new Cell().add(new Paragraph("Immatriculation :")).setBold());
            table.addCell(new Cell().add(new Paragraph(ent.getImmatriculation())));

            table.addCell(new Cell().add(new Paragraph("Client :")).setBold());
            table.addCell(new Cell().add(new Paragraph(ent.getNomClient())));

            table.addCell(new Cell().add(new Paragraph("Date :")).setBold());
            table.addCell(new Cell().add(new Paragraph(ent.getDateEntretien())));

            table.addCell(new Cell().add(new Paragraph("Prix :")).setBold());
            table.addCell(new Cell().add(new Paragraph(String.valueOf(serv.getPrix()) + " FCFA")));

            doc.add(table);
            doc.add(new Paragraph("\nMerci pour votre visite !").setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return fichier;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
