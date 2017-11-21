import com.mda.nubel.json.JsonProduct
import com.mda.nubel.scraper.ProductScraper
import com.mda.nubel.scraper.sessionId

fun main(args: Array<String>) {
    val jsonProduct = JsonProduct(ProductScraper(sessionId))
    jsonProduct.handleErrors()
}