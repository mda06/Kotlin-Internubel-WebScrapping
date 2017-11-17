package com.mda.nubel.scraper

import com.mda.nubel.exception.NotConnectedException
import com.mda.nubel.model.Nutriment
import com.mda.nubel.model.Product
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ProductScraper(var currentSessionId: String) {
    val url: String = "http://www.internubel.be"
    val cookieSession: String = "ASP.NET_SessionId"
    val groups: String = "Groups.aspx"
    val search: String = "Search.aspx"
    val langEn = "1"
    val langFr = "2"
    val langNl = "3"
    val langAl = "4"
    var language: String = langFr

    fun scrapTable(table: Element) : List<Nutriment> {
        val lst = arrayListOf<Nutriment>()
        val bodies = table.select("tbody")
        if(bodies.size > 0) {
            val body = bodies.first()
            for(tr in body.select("tr")) {
                val tds = tr.select("td")
                if(tds.size == 2) {
                    val split = tds.last().text().split(" ")
                    val unit = Nutriment(tds.first().text(), (split[0].replace(",", ".")).toDouble(), split[1])
                    lst.add(unit)
                }
            }
        }
        return lst
    }


    fun scrapProduct(id: Int) : Product  {
        val doc = Jsoup.connect("$url/$search?lId=$language&pId=$id").cookie(cookieSession, currentSessionId).get()
        val name = doc.select("#lblTitle").text()
        if(name.isNullOrEmpty()) throw NotConnectedException("No session for id: $currentSessionId")
        val url = doc.select("#imgProduct").attr("src")
        val units = scrapTable(doc.select("#units>table").first())
        val macro = scrapTable(doc.select("#macro>table").first())
        val micro = scrapTable(doc.select("#micro>table").first())

        return Product(id = id, name = name, imgUrl = url, units = units, macros = macro,  micros = micro)
    }

}