package com.mardpop.jrocket.util;

import org.junit.jupiter.api.Test;

public class QuaternionTest {
    
    @Test
    void testToFromRotationMatrix() {
        Quaternion q = new Quaternion(0.5,0.3,0.4,0.6);
        q.normalize();
        Matrix3 m = q.toRotationMatrix();
        q.fromRotationMatrix(m);
        assert q.equals(q);
    }

    @Test
    void testQuaternionMultiplication() {
        Quaternion q = new Quaternion(0.5,0.3,0.4,0.6);
        q.normalize();
        Quaternion q2 = new Quaternion(0.5,0.3,0.4,0.6);
        q2.normalize();
        Quaternion q3 = q.mult(q2);
    }
}
