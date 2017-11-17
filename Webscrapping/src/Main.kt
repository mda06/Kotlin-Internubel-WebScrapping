import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mda.nubel.model.Product
import com.mda.nubel.scraper.ProductScraper

fun main(args: Array<String>) {
    val scraper = ProductScraper(com.mda.nubel.scraper.sessionId)

    val mapper = jacksonObjectMapper()
    val writer = mapper.writerWithDefaultPrettyPrinter()

    val json = writer.writeValueAsString(scraper.scrapProduct(4498))
    print(json)
}