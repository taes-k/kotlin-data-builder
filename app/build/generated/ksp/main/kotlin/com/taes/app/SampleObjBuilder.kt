package com.taes.app

class SampleObjBuilder{
    private var name: kotlin.String? = null
    fun name(name: kotlin.String): SampleObjBuilder {
        this.name = name
        return this
    }

    private var age: kotlin.Int? = null
    fun age(age: kotlin.Int): SampleObjBuilder {
        this.age = age
        return this
    }

    fun build(): com.taes.app.SampleObj {
        return com.taes.app.SampleObj(name!!, age!!)
    }
}
