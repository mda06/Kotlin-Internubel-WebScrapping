import com.mda.nubel.scraper.ProductScraper

fun main(args: Array<String>) {
    val scraper = ProductScraper()

    print(scraper.scrapProduct(5020))
}