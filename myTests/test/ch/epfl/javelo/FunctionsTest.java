package ch.epfl.javelo;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
    public final static double DELTA = 0.2;

    @Test
    void constant() {
        assertEquals(2, Functions.constant(2).applyAsDouble(2));
    }

    @Test
    void sampledWeiEn() {
        assertEquals(3.25, Functions.sampled(new float[]{4,2,3,2,4,1}, 10).applyAsDouble(8.5));
    }

    @Test
    void sampledFlorian() {
        assertEquals(0.65, Functions.sampled(new float[]{0,1}, 1).applyAsDouble(0.65));
    }

    @Test
    void sampledHanaAndLuna() {
        assertEquals(2.4, Functions.sampled(new float[]{0,1,3,2,1}, 2).applyAsDouble(1.3));
    }

    @Test
    void sampled() {
        assertEquals(8.55, Functions.sampled(new float[]{10, 8 ,6, 9, 11, 12}, 10).applyAsDouble(5.7));
    }

    @Test
    void workOnMultipleKnowValueSampled(){
        float [] test = new float[]{10,2,5,7,8,9,1};
        DoubleUnaryOperator result =  Functions.sampled(test,2*(test.length-1));
        double actual = result.applyAsDouble(9);
        double expected = 8.5;
        assertEquals(expected,actual);
    }




    @Test
    void constantTest(){
        DoubleUnaryOperator c = Functions.constant(4);
        assertEquals(4, c.applyAsDouble(7));
    }


    @Test
    void sampledWeiEn2() {
        assertEquals(5.4, Functions.sampled(new float[]{7,0,7,4,6,5}, 5).applyAsDouble(1.75),DELTA);
    }



    @Test
    void sampledExceptionsTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {0};
            DoubleUnaryOperator test1 = Functions.sampled(samples, 4);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {3,5};
            DoubleUnaryOperator test1 = Functions.sampled(samples, -3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            float[] samples = {3,5};
            DoubleUnaryOperator test1 = Functions.sampled(samples, 0);
        });
    }

    @Test
    void constantWorksCorrectly() {
        DoubleUnaryOperator cst = Functions.constant(37.98);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextInt(1000);
            assertEquals(37.98, cst.applyAsDouble(x));
        }
    }

    @Test
    void sampledThrowsExceptionForLength(){
        float[] samples = {1};
        assertThrows(IllegalArgumentException.class, () ->{
            Functions.sampled(samples,5);
        });
    }


    @Test
    void sampledThrowsExceptionForMax(){
        float[] samples = {1,2,3};
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
//            var xMax = rng.nextInt(1000);
//            xMax = xMax - 1000;
            assertThrows(IllegalArgumentException.class, () ->{
                Functions.sampled(samples,-7);
            });
        }
    }

    @Test
    void sampledWorksFor2ValuesInSamples(){
        //Linear
        float[] samples = {0,2,(float)5.1};
        //var rng = newRandom();
        DoubleUnaryOperator linear = Functions.sampled(samples,2);
        //for (var i = 0; i < RANDOM_ITERATIONS; i += 1){
        //var testValue = rng.nextDouble(0, 2);
        assertEquals(4.7,linear.applyAsDouble(1.889),DELTA);
    }
    //}


    @Test
    void sampledWorksForAHugeArray(){
        float[] samples = {2, 0, 5, 6, 0, 0.5f, 7};
        float[] samplesTest = {2,0, 5};
        DoubleUnaryOperator fct = Functions.sampled(samples, 6);
        assertEquals(3.85,fct.applyAsDouble(1.77));
    }
}