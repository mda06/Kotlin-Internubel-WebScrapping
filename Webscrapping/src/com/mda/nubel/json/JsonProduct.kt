package com.mda.nubel.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mda.nubel.exception.NotConnectedException
import com.mda.nubel.model.Product
import com.mda.nubel.scraper.ProductScraper
import java.io.File

class JsonProduct(val scraper: ProductScraper) {
    private val mapper = jacksonObjectMapper()
    private val writer = mapper.writerWithDefaultPrettyPrinter()

    fun getJsonProduct(errorsInIds: (Int) -> Unit, id : Int) : String {
        return try {
            writer.writeValueAsString(scraper.scrapProduct(id))
        } catch(ex: NotConnectedException) {
            errorsInIds(id)
            ""
        }
    }

    fun getJsonProducts(errorsInIds: (Int) -> Unit, allProducts: Boolean = false, quantity: Int = 10) : String {
        val ids = scraper.scrapProductIds()
        val products = arrayListOf<Product>()
        for((index, id) in
            if(allProducts) { ids.withIndex() } else { ids.subList(0, quantity).withIndex() }) {
            println("${index+1}/${if(allProducts) ids.size else quantity} for id: $id")
            try {
                products.add(scraper.scrapProduct(id))
            } catch(ex: NotConnectedException) {
                errorsInIds(id)
            }
        }
        return writer.writeValueAsString(products)
    }

    fun exportJsonProducts() {
        val errorIds = arrayListOf<Int>()
        File("Webscrapping/res/error").walkTopDown().forEach {
            if(!it.isDirectory)
                it.delete()
        }
        val json = getJsonProducts({
            println("Error getJsonProduct for $it")
            errorIds.add(it)},
                true)
        File("Webscrapping/res/products_${scraper.language.first}.json")
                .writeText(json)
        println("There are ${errorIds.size} errors")
        File("Webscrapping/res/error/error_product_ids_${scraper.language.first}.txt")
                .writeText(errorIds.joinToString ("," ))
    }

    private fun handleError() {
        val products = mapper.readValue<ArrayList<Product>>(
                File("Webscrapping/res/products_${scraper.language.first}.json"))
        val errors = File("Webscrapping/res/error/error_product_ids_${scraper.language.first}.txt")
                .readLines().first()
        val newErrors = arrayListOf<Int>()
        errors.split(",")
                .map { Integer.parseInt(it) }
                .forEach {
                    try {
                        val prod = (scraper.scrapProduct(it))
                        println("${prod.id} is no more a error")
                        File("Webscrapping/res/error/product_$it.html")
                                .delete()
                        products.add(prod)
                    } catch(ex: NotConnectedException) {
                        println("Error trying to handle error product N°$it")
                        newErrors.add(it)
                    }
                }

        File("Webscrapping/res/products_${scraper.language.first}.json")
                .writeText(writer.writeValueAsString(products))
        println("There are ${newErrors.size} errors")
        File("Webscrapping/res/error/error_product_ids_${scraper.language.first}.txt")
                .writeText(newErrors.joinToString ("," ))
    }

    fun handleErrors() {
        handleError()
    }
}
