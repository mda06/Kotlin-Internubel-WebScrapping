package com.mda.nubel.model

data class ProductGroup(val id: Int, val mId: Int, val name: String, val path: String) {
    val groups = arrayListOf<ProductGroup>()
    val productIds = arrayListOf<Int>()

    fun add(group: ProductGroup) = groups.add(group)
    fun addAll(group: List<ProductGroup>) = groups.addAll(group)
    fun add(productId: Int) = productIds.add(productId)

    fun sizeOfProducts() = productIds.size
    fun fullSize() : Int = groups.map { it.fullSize() }.sum() + sizeOfProducts()
}