import com.mda.nubel.json.JsonProduct
import com.mda.nubel.scraper.ProductScraper
import com.mda.nubel.scraper.sessionId

fun main(args: Array<String>) {
    val scraper = ProductScraper(sessionId)
    scraper.language = scraper.langEn
    val jsonProduct = JsonProduct(scraper)
    jsonProduct.handleErrors()
}