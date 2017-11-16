package com.mda.nubel.scraper

import com.mda.nubel.model.Product
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ProductScraper {
    val url: String = "http://www.internubel.be"
    val cookieSession: String = "ASP.NET_SessionId"
    val groups: String = "Groups.aspx"
    val search: String = "Search.aspx"
    val langEn = "1"
    val langFr = "2"
    val langNl = "3"
    val langAl = "4"
    var language: String = langFr
    var currentSessionId: String = ""

    fun scrapProduct(id: Int) : Product  {
        val doc = Jsoup.connect("$url/$search?lId=$language&pId=$id").cookie(cookieSession, currentSessionId).get()
        val name = doc.select("#lblTitle").text()
        //val url = doc.select("#imgProduct").attr("src")

        return Product(id = id, name = name)


        /*Document doc = Jsoup.connect(URL + "/" + SEARCH + "?lId=" + FR + "&pId=" + id).cookie(COOKIE_SESSION, currentId)
				.get();
		String name = doc.select("#lblTitle").text();
		String url = doc.select("#imgProduct").attr("src");
		HashMap<String, Unit> units = parseTable(doc.select("#units>table").first());
		HashMap<String, Unit> macro = parseTable(doc.select("#macro>table").first());
		HashMap<String, Unit> micro = parseTable(doc.select("#micro>table").first());
		return new Product(name, url, units, macro, micro, id);*/
    }

}