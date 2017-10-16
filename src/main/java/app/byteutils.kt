package app

import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and


/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 15.10.17
 * Time: 12:10
 */

/**
 *  write 32 bit unsigned Integer to ByteArray
 */
fun writeInt32(value: Long, data: ByteArray, offset: Int, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN) {
    if (endianness == ByteOrder.LITTLE_ENDIAN) {
        data[offset] = (value and 0xFF).toByte()
        data[offset + 1] = (value and (0xFF shl 8) shr 8).toByte()
        data[offset + 2] = (value and (0xFF shl 16) shr 16).toByte()
        data[offset + 3] = (value and (0xFF.toLong() shl 24) shr 24).toByte()
    } else {
        data[offset + 3] = (value and 0xFF).toByte()
        data[offset + 2] = (value and (0xFF shl 8) shr 8).toByte()
        data[offset + 1] = (value and (0xFF shl 16) shr 16).toByte()
        data[offset] = (value and (0xFF.toLong() shl 24) shr 24).toByte()
    }
}

/**
 *  read 32 bit unsigned Integer to ByteArray
 */
fun readInt32(data: ByteArray, offset: Int, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long {
    var value: Long = 0
    if (endianness == ByteOrder.LITTLE_ENDIAN) {
        value = (value shl 8) + (data[offset + 3] and 0xff.toByte())
        value = (value shl 8) + (data[offset + 2] and 0xff.toByte())
        value = (value shl 8) + (data[offset + 1] and 0xff.toByte())
        value = (value shl 8) + (data[offset] and 0xff.toByte())
    } else {
        value = (value shl 8) + (data[offset] and 0xff.toByte())
        value = (value shl 8) + (data[offset + 1] and 0xff.toByte())
        value = (value shl 8) + (data[offset + 2] and 0xff.toByte())
        value = (value shl 8) + (data[offset + 3] and 0xff.toByte())
    }
    return value
}

/**
 * Uint to ByteArray conversion
 * @param uintValue unsigned Integer (actually Long, bc. Java does not contain unsigned primitive types) which must be in range 0 ... (2 ^ 32) - 1
 * @param endianness the Byte order
 * @return 4 Byte ByteArray which represents the unsigned Integer
 */
fun uintToBytes(uintValue: Long, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
    if(uintValue > 4294967295) throw IllegalArgumentException("uintValue value must not exceed 4294967295")// largest UInt value possible (2^32)-1 , actually has to be Long on JVM
    return ByteBuffer.allocate(8).order(endianness).putLong(uintValue).array().slice(if (endianness == ByteOrder.LITTLE_ENDIAN) 0..3 else 4..7).toByteArray()
}

/**
 * ByteArray to Uint conversion
 * @param bytes the ByteArray
 * @param endianness the Byte order
 * @return the UInt, typed as a JVM Long
 */
fun bytesToUint(bytes: ByteArray, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long {
    if (bytes.size > 8) throw IllegalArgumentException("bytes.size must not exceed 8!")
    val remainingBytes = ByteArray(8 - bytes.size)
    return ByteBuffer.wrap(if (endianness == ByteOrder.LITTLE_ENDIAN) bytes + remainingBytes else remainingBytes + bytes).order(endianness).long
}

/**
 * Float to ByteArray conversion
 * @param floatValue the Float to be converted
 * @param endianness the Byte Order
 * @return 4-Byte ByteArray representing the Float Value
 */
fun floatToBytes(floatValue: Float, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray = ByteBuffer.allocate(4).order(endianness).putFloat(floatValue).array()

/**
 * ByteArray to Float conversion
 * @param bytes the ByteArray, being a 4-Byte ByteArray
 * @param endianness the Byte Order
 * @return the Float
 */
fun bytesToFloat(bytes:ByteArray, endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN):Float {
    if (bytes.size > 4) throw IllegalArgumentException("bytes.size must not exceed 4!")
    return ByteBuffer.wrap(bytes).order(endianness).float
}

