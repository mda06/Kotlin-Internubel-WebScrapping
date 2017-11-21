package com.mda.nubel.scraper

import com.mda.nubel.exception.NotConnectedException
import com.mda.nubel.model.Nutriment
import com.mda.nubel.model.Product
import com.mda.nubel.model.ProductGroup
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

class ProductScraper(var currentSessionId: String) {
    private val url: String = "http://www.internubel.be"
    private val cookieSession: String = "ASP.NET_SessionId"
    private val groups: String = "Groups.aspx"
    private val search: String = "Search.aspx"
    val langEn = Pair("en", "3")
    val langFr = Pair("fr","2")
    val langNl = Pair("nl", "1")
    val langAl = Pair("al", "4")
    var language = langFr

    private fun scrapTable(table: Element?) : List<Nutriment> {
        val lst = arrayListOf<Nutriment>()
        if(table == null) return lst
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
        val doc = Jsoup.connect("$url/$search?lId=${language.second}&pId=$id").cookie(cookieSession, currentSessionId).get()
        val name = doc.select("#lblTitle").text()
        if(name.isNullOrEmpty()) {
            File("Webscrapping/res/error/product_$id.html")
                    .writeText(doc.toString())
            throw NotConnectedException("No session for id: $currentSessionId")
        }
        val url = doc.select("#imgProduct").attr("src")
        val units = scrapTable(doc.select("#units>table")?.first())
        val macro = scrapTable(doc.select("#macro>table")?.first())
        val micro = scrapTable(doc.select("#micro>table")?.first())

        return Product(id = id, name = name, imgUrl = url, units = units, macros = macro,  micros = micro)
    }

    fun scrapProductIds() : List<Int>{
        val lst = arrayListOf<Int>()
        val doc = Jsoup.parse(File("Webscrapping/res/products.html"), "UTF-8")
        val els = doc.select("ul.text > li > a")
        els
            .map { it.attr("href") }
            .map { it.split("=") }
            .mapTo(lst) { Integer.parseInt(it[it.size - 1]) }
        return lst
    }

    private fun scrapProductGroup(g: ProductGroup) {
        val doc = Jsoup.connect("$url/$groups?lId=${language.second}&gId=$g.id&mgId=$g.mId")
                .cookie(cookieSession, currentSessionId).get()
        for(el in doc.select("ul[class=text] > li a")) {
            val sp = el.attr("href").split("=")
            val id = Integer.parseInt(sp[sp.size - 1])
            g.add(id)
        }
    }

    //Use Element? Can be null
    private fun scrapProductGroupSubChilds(el : Element) : List<ProductGroup> {
        val ul = el.select("ul").first()
        if(ul != null) {
            val lst = arrayListOf<ProductGroup>()
            for(li in ul.children()) {
                val a = li.select("a").first()
                val name = a.text()
                val path = a.attr("href")
                val href = path.split("=")
                val gId = Integer.parseInt(href[2].split("&")[0])
                val mgId = Integer.parseInt(href[3])
                val g = ProductGroup(gId, mgId, name, path)
                scrapProductGroup(g)
                val tmp = scrapProductGroupSubChilds(li)
                g.addAll(tmp)
                lst.add(g)
            }
            return lst
        }
        return arrayListOf()
    }

    private fun scrapMainGroup(e : Element) : ProductGroup {
		val name = e.select("a").first().text()
		val id = Integer.parseInt(e.attr("id").replace("maingroup", ""))
		val g = ProductGroup(id, -1, name, "")
		val ul = e.select("ul").first()
		g.addAll(scrapProductGroupSubChilds(ul))
		return g
	}

	fun scrapProductGroups() : List<ProductGroup> {
        val lst = arrayListOf<ProductGroup>()
        val d = Jsoup.parse(File("Webscrapping/res/groups_nubel.html"), "UTF-8")
        val els = d.select(".maingroup")
        els.mapTo(lst) { scrapMainGroup(it) }
        return lst
	}

}