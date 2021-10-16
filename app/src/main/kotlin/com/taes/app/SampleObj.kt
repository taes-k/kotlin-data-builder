package com.taes.app

import com.taes.annotation.KtBuilder

@KtBuilder
data class SampleObj(val name: String, var age: Int) {
}