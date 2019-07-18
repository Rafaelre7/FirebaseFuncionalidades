package rafaelpimenta.studio.com.firebasemoduloii.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfCreator extends PdfPageEventHelper {

    Phrase [] phrases = new Phrase[2];

    int numero_pagina;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

        phrases[0] = new Phrase("pdf");

    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {

        phrases[1] = new Phrase(title.getContent());
        numero_pagina = 1;

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {

       numero_pagina ++;

    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        Rectangle rectangle = writer.getBoxSize("box_a");

        //Colocando numeracao pera direita superior
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,new Phrase(""),
                rectangle.getRight(),rectangle.getTop(),0);

        //Colocando numeracao pera direita inferior
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,new Phrase(String.format("%d",numero_pagina)),
                rectangle.getRight()+rectangle.getRight()/2,rectangle.getBottom() -18,0);


    }
}
