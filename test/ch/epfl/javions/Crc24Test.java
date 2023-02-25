package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Crc24Test {
    @Test
    public void testCrc24() {
//        Crc24 crc24 = new Crc24(0b11001);
//        int crc = crc24.crc_a(0b11111100111);
//        assertEquals("110", Integer.toBinaryString(crc));
        Crc24 crc24 = new Crc24();
        int crc = crc24.crc_a(2);
        System.out.println(Integer.toBinaryString(crc));
    }
}
