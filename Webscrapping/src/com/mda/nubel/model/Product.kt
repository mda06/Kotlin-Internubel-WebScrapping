package com.mda.nubel.model

data class Product(
        val id: Int,
        val name: String,
        val imgUrl: String,
        val units: List<Nutriment>,
        val macros: List<Nutriment>,
        val micros: List<Nutriment>)