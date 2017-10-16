package app

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 21:17
 */
class FramingAlgorithmTests {
    @org.junit.Test
    fun testEncode() {
        val algo = COBSAlgorithm()

        assertArrayEquals(
                byteArrayOf(0x05, 0x01, 0x01, 0x02, 0x01, 0x00),
                algo.encode(byteArrayOf(0x01, 0x01, 0x02, 0x01))
        )
        assertArrayEquals(
                byteArrayOf(0x03, 0x01, 0x01, 0x03, 0x02, 0x01, 0x00),
                algo.encode(byteArrayOf(0x01, 0x01, 0x00, 0x02, 0x01))
        )

        assertArrayEquals(
                byteArrayOf(0x01, 0x03, 0x01, 0x03, 0x00),
                algo.encode(byteArrayOf(0x00, 0x01, 0x03))
        )
    }

    @org.junit.Test
    fun testDecode() {
        val algo = COBSAlgorithm()

        assertArrayEquals(
                byteArrayOf(0x01, 0x01, 0x02, 0x01),
                algo.decode(byteArrayOf(0x05, 0x01, 0x01, 0x02, 0x01, 0x00))
        )
        assertArrayEquals(
                byteArrayOf(0x01, 0x01, 0x00, 0x02, 0x01),
                algo.decode(byteArrayOf(0x03, 0x01, 0x01, 0x03, 0x02, 0x01, 0x00))
        )

        assertArrayEquals(
                byteArrayOf(0x00, 0x01, 0x03),
                algo.decode(byteArrayOf(0x01, 0x03, 0x01, 0x03, 0x00))
        )

        assertArrayEquals(
                byteArrayOf(0x00, 0xff.toByte(), 0x03),
                algo.decode(byteArrayOf(0x01, 0x03, 0xff.toByte(), 0x03, 0x00))
        )
    }


    @org.junit.Test
    fun testDecodeString() {
        val algo = COBSAlgorithm()
        val input = byteArrayOf(0x0B, 0x41, 0x46, 0x47, 0x34, 0x33, 0x38, 0x37, 0x58, 0x30, 0x31, 0x00) // COBS encoded

        val decoded = algo.decode(input)
        val string = String(decoded, Charsets.UTF_8)
        assertEquals("AFG4387X01", string)
    }
}