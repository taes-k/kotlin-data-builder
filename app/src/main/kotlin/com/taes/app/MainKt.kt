package com.taes.app

import com.taes.annotation.KtBuilder

fun main(){
    println("The compiler should have told you I am interesting")

    val obj = SampleObj("ABC", 12)
    println(obj)
//    val obj1: SampleObj = SampleObjBuilder()
//        .name("1")
//        .age(12)
//        .build()
//    println(obj1)
//
//    val obj2 = obj1.copy(
//        age = 13
//    )
//    println(obj2)
}
