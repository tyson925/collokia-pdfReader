package com.collokia.pdfParser

import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.snowtide.PDF
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.input.PortableDataStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.WriteOutContentHandler
import pdfts.examples.XMLOutputTarget
import scala.Tuple2
import java.io.*






class PdfParser {

    companion object {

        val FILE_NAME = "itext.pdf"

        @JvmStatic
        fun main(args: Array<String>) {
            //writeUsingIText()
            //readUsingIText()
            //readPdfWithApacheTika()
            readUsingPDFTextStream()
        }

        fun readUsingPDFTextStream() {
            val file = File("pdfs/pdfurl-guide.pdf")
            val pdf = PDF.open(file)
            println("---------Start metadata---------")
            pdf.attributeKeys.forEach {
                println("$it: ${pdf.getAttribute(it)}")
            }
            println("---------End metadata---------")
            val xmlOutputTarget = XMLOutputTarget()
            pdf.pipe(xmlOutputTarget)
            val buffer = StringWriter()
            buffer.write(xmlOutputTarget.xmlAsString)
            pdf.close()
            println(buffer.toString())
            buffer.close()
        }

        fun readPdfWithApacheTika() {
            val filesPath = "pdfs/*"
            val conf = SparkConf().setAppName("TikaFileParser").setMaster("local[2]")
            val sc = JavaSparkContext(conf)
            val fileData = sc.binaryFiles(filesPath, 2)

            fileData.foreach { t -> tikaFunc(t) }
        }

        private fun tikaFunc (a: Tuple2<String, PortableDataStream>)  {

            val file = File(a._1.drop(5))
            val myParser = AutoDetectParser()
            val stream = FileInputStream(file)
            val handler = WriteOutContentHandler(-1)
            val metadata = Metadata()
            val context = ParseContext()

            myParser.parse(stream, handler, metadata, context)

            stream.close()

            println(metadata.toString())
            println("------------------------------------------------")
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