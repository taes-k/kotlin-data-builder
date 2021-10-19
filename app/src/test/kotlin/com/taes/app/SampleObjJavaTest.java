package com.taes.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SampleObjJavaTest {
    @Test
    void generateByBuilderInJava() {
        // given
        var name = "tester";
        var age = 10;

        // when
        var result = new SampleObjBuilder()
            .name(name)
            .age(age)
            .build();

        // then
        Assertions.assertEquals(name, result.getName());
        Assertions.assertEquals(age, result.getAge());
    }
}
