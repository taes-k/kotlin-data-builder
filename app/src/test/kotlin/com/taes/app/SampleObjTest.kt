package com.taes.app

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SampleObjTest {
    @Test
    fun generateByBuilder() {
        // given
        val name = "tester"
        val age = 10

        // when
        val result = SampleObjBuilder()
            .name(name)
            .age(age)
            .build()

        // then
        Assertions.assertEquals(name, result.name)
        Assertions.assertEquals(age, result.age)
    }
}