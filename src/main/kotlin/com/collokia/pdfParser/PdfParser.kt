package com.collokia.pdfParser

import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



/**
 * Created by istvan on 7/11/17.
 */
class PdfParserTest {

    companion object {

        val FILE_NAME = "itext.pdf"

        @JvmStatic
        fun main(args: Array<String>) {
            //writeUsingIText()
            readUsingIText()
        }

        fun writeUsingIText() {

            val document = Document()

            try {

                PdfWriter.getInstance(document, FileOutputStream(File(FILE_NAME)))

                //open
                document.open()

                val p = Paragraph()
                p.add("This is my paragraph 1")
                p.alignment = Element.ALIGN_CENTER

                document.add(p)

                val p2 = Paragraph()
                p2.add("This is my paragraph 2") //no alignment

                document.add(p2)

                val f = Font()
                f.style = Font.BOLD
                f.size = 8.0F

                document.add(Paragraph("This is my paragraph 3", f))

                //close
                document.close()

                println("Done")

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun readUsingIText() {
            val reader: PdfReader

            try {

                reader = PdfReader(FILE_NAME)

                // pageNumber = 1
                val textFromPage = PdfTextExtractor.getTextFromPage(reader, 1)

                println(textFromPage)

                reader.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}