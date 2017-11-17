import com.mda.nubel.scraper.ProductScraper

fun main(args: Array<String>) {
    val scraper = ProductScraper(com.mda.nubel.scraper.sessionId)

    print(scraper.scrapProduct(2837))
}