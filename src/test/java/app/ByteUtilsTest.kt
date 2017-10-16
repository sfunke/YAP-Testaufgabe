package app
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteOrder

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 15.10.17
 * Time: 12:18
 */
class ByteUtilsTest {

    //--------------------------------------------------------------------------
    //
    //  Tests custom utility methods
    //
    //--------------------------------------------------------------------------
    @Test
    fun testUintToByteArray() {
        val zero = 0
        val lowInt = 3
        val largestInt = 4294967295 // largest UInt value possible (2^32)-1 , actually has to be Long on JVM

        //----------------------------------
        //  Big Endian
        //----------------------------------
        var converted = uintToBytes(zero.toLong(), ByteOrder.BIG_ENDIAN)
        assertArrayEquals(byteArrayOf(0x0, 0x0, 0x0, 0x0), converted)

        converted = uintToBytes(lowInt.toLong(), ByteOrder.BIG_ENDIAN)
        assertArrayEquals(byteArrayOf(0x0, 0x0, 0x0, 0x3), converted)

        converted = uintToBytes(largestInt, ByteOrder.BIG_ENDIAN)
        assertArrayEquals(byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()), converted)

        //----------------------------------
        //  Little Endian
        //----------------------------------
        converted = uintToBytes(zero.toLong(), ByteOrder.LITTLE_ENDIAN)
        assertArrayEquals(byteArrayOf(0x0, 0x0, 0x0, 0x0), converted)

        converted = uintToBytes(lowInt.toLong(), ByteOrder.LITTLE_ENDIAN)
        assertArrayEquals(byteArrayOf(0x03, 0x0, 0x0, 0x0), converted)

        converted = uintToBytes(largestInt, ByteOrder.LITTLE_ENDIAN)
        assertArrayEquals(byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()), converted)
    }

    @Test
    fun testByteArrayToUint() {
        val zero = 0
        val lowInt = 3
        val largestInt = 4294967295 // largest UInt value possible (2^32)-1 , actually has to be Long on JVM

        //----------------------------------
        //  BIG Endian
        //----------------------------------
        assertEquals(largestInt, bytesToUint(byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()), ByteOrder.BIG_ENDIAN))
        assertEquals(lowInt.toLong(), bytesToUint(byteArrayOf(0x0, 0x0, 0x0, 0x3), ByteOrder.BIG_ENDIAN))
        assertEquals(zero.toLong(), bytesToUint(byteArrayOf(0, 0, 0, 0), ByteOrder.BIG_ENDIAN))

        //----------------------------------
        //  LITTLE Endian
        //----------------------------------
        assertEquals(largestInt, bytesToUint(byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()), ByteOrder.LITTLE_ENDIAN))
        assertEquals(lowInt.toLong(), bytesToUint(byteArrayOf(0x3, 0x0, 0x0, 0x0), ByteOrder.LITTLE_ENDIAN))
        assertEquals(zero.toLong(), bytesToUint(byteArrayOf(0, 0, 0, 0), ByteOrder.LITTLE_ENDIAN))


        // other variations
        assertEquals(0x01020304, bytesToUint(byteArrayOf(0x04, 0x03, 0x02, 0x01), ByteOrder.LITTLE_ENDIAN))
        assertEquals(0x04030201, bytesToUint(byteArrayOf(0x04, 0x03, 0x02, 0x01), ByteOrder.BIG_ENDIAN))
    }

    @Test
    fun testFloatToByteArray() {
        // https://gregstoll.dyndns.org/~gregstoll/floattohex/
        // value to test: 23.1234
        // expected result: 0x41b8fcb9
        val floatValue = 23.1234f

        // Big Endian
        var converted = floatToBytes(floatValue, ByteOrder.BIG_ENDIAN)
        assertArrayEquals(byteArrayOf(0x41.toByte(), 0xb8.toByte(), 0xfc.toByte(), 0xb9.toByte()), converted)

        // Little Endian
        converted = floatToBytes(floatValue, ByteOrder.LITTLE_ENDIAN)
        assertArrayEquals(byteArrayOf(0xb9.toByte(), 0xfc.toByte(), 0xb8.toByte(), 0x41.toByte()), converted)
    }

    @Test
    fun testByteArrayToFloat() {
        // https://gregstoll.dyndns.org/~gregstoll/floattohex/
        // value to test: 23.1234
        // expected result: 0x41b8fcb9
        val floatValue = 23.1234f

        assertEquals(floatValue, bytesToFloat(byteArrayOf(0x41.toByte(), 0xb8.toByte(), 0xfc.toByte(), 0xb9.toByte()), ByteOrder.BIG_ENDIAN))
        assertEquals(floatValue, bytesToFloat(byteArrayOf(0xb9.toByte(), 0xfc.toByte(), 0xb8.toByte(), 0x41.toByte()), ByteOrder.LITTLE_ENDIAN))
    }


    //----------------------------------
    //  Not Used methods
    //----------------------------------
    @Test
    fun testWriteInt32_BigEndian() {
        val data = ByteArray(4)
        writeInt32(0x01020304, data, 0, ByteOrder.BIG_ENDIAN)
        assertArrayEquals(byteArrayOf(0x01, 0x02, 0x03, 0x04), data)
    }

    @Test
    fun testWriteInt32_LittleEndian() {
        val data = ByteArray(4)
        writeInt32(0x01020304, data, 0, ByteOrder.LITTLE_ENDIAN)
        assertArrayEquals(byteArrayOf(0x04, 0x03, 0x02, 0x01), data)
    }

    @Test
    fun readInt32_BigEndian() {
        val result = readInt32(byteArrayOf(0x01, 0x02, 0x03, 0x04), 0, ByteOrder.BIG_ENDIAN)
        assertEquals(0x01020304, result)
    }

    @Test
    fun readInt32_LittleEndian() {
        val result = readInt32(byteArrayOf(0x04, 0x03, 0x02, 0x01), 0, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x01020304, result)
    }




}