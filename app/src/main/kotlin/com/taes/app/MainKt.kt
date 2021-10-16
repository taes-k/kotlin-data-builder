package com.taes.app

fun main(){
    val obj1: SampleObj = SampleObjBuilder()
        .name("1")
        .age(12)
        .build()
    println(obj1)

    val obj2 = obj1.copy(
        age = 13
    )
    println(obj2)
}
