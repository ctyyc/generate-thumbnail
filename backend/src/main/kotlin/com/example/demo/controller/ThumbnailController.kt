package com.example.demo.controller

import net.coobird.thumbnailator.Thumbnails
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RestController
@RequestMapping("/thumbnail")
class ThumbnailController {

    @PostMapping("/ppt", produces = [MediaType.IMAGE_PNG_VALUE])
    fun generatePptThumbnail(@RequestBody param: MultipartFile): ResponseEntity<ByteArray> {
        println(" === start api === ")
        try {
            // Load PPT file into XMLSlideShow
            val inputStream = ByteArrayInputStream(param.bytes)
            val ppt = XMLSlideShow(inputStream)

            // Get the first slide's image
            val slideImage = extractSlideImage(ppt)

            // Convert BufferedImage to byte array
            val baos = ByteArrayOutputStream()
            Thumbnails.of(slideImage).size(200, 200).outputFormat("png").toOutputStream(baos)
            val thumbnailBytes = baos.toByteArray()

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(thumbnailBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PostMapping("/pdf", produces = [MediaType.IMAGE_PNG_VALUE])
    fun generatePdfThumbnail(@RequestBody param: MultipartFile): ResponseEntity<ByteArray> {
        println(" === start api === ")
        try {
            val inputStream = ByteArrayInputStream(param.bytes)
            val document = PDDocument.load(inputStream)
            val renderer = PDFRenderer(document)

            // Render the first page of the PDF (300 DPI)
            val pageImage: BufferedImage = renderer.renderImageWithDPI(0, 300f)

            // Convert BufferedImage to byte array
            val baos = ByteArrayOutputStream()
            Thumbnails.of(pageImage).size(200, 200).outputFormat("png").toOutputStream(baos)
            val thumbnailBytes = baos.toByteArray()

            document.close()
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(thumbnailBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    private fun extractSlideImage(ppt: XMLSlideShow): BufferedImage {
        val slide = ppt.slides[0]
        val dimension = Dimension(720, 540)
        val image = BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()

        try {
            slide.draw(graphics)
        } catch (e: Exception) {
            throw Exception("ERROR : ${e.message}")
        } finally {
            graphics.dispose()
        }

        return image
    }
}