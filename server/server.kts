
import java.lang.IllegalArgumentException
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 15.10.17
 * Time: 11:20
 */

//--------------------------------------------------------------------------
//
//  Server Loop
//
//--------------------------------------------------------------------------
val server = ServerSocket(12345)
println("Waiting for connections ...")
while (true) {
    val client = server.accept()
    println("Client connected: ${client.remoteSocketAddress}")

    val inputStream = client.getInputStream()
    val outputStream = client.getOutputStream()

    val inputBytes = ByteArray(6)
    inputStream.read(inputBytes)
    println("Input: ${Arrays.toString(inputBytes)}")

    // decode the request
    val decoded = COBSAlgorithm.decode(inputBytes)

    println("Decoded Input: ${Arrays.toString(decoded)}")
    val result =
//            if (decoded contentEquals DataPoint.SERIAL_NUMBER.idBytes) byteArrayOf(0x0B, 0x41, 0x46, 0x47, 0x34, 0x33, 0x38, 0x37, 0x58, 0x30, 0x31, 0x00)
            if (decoded contentEquals DataPoint.SERIAL_NUMBER.idBytes) "AFG4387X01".toByteArray(Charsets.UTF_8)
            else if (decoded contentEquals DataPoint.PRODUCT_TYPE.idBytes) "YAP-Reader".toByteArray(Charsets.UTF_8)
            else if (decoded contentEquals DataPoint.POWER_INPUT_WATT.idBytes) ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(1234.5f).array()
            else if (decoded contentEquals DataPoint.POWER_OUTPUT_WATT.idBytes) ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(23456.54321f).array()
            else if (decoded contentEquals DataPoint.WORK_INPUT_KILOWATTHOURS.idBytes) uintToBytes(23, ByteOrder.BIG_ENDIAN)
            else if (decoded contentEquals DataPoint.WORK_OUTPUT_KILOWATTHOURS.idBytes) uintToBytes(42, ByteOrder.BIG_ENDIAN)
            else if (decoded contentEquals DataPoint.WORKING_HOURS.idBytes) uintToBytes(4294967295, ByteOrder.LITTLE_ENDIAN)
            else kotlin.byteArrayOf(0x00)

    // encode the response
    val encoded = COBSAlgorithm.encode(result)
    outputStream.write(encoded)
    client.close()
}
server.close()





//--------------------------------------------------------------------------
//
//  Helper
//
//--------------------------------------------------------------------------

object COBSAlgorithm {
    /**
     * Implementation inspired by:
     * https://github.com/jacquesf/COBS-Consistent-Overhead-Byte-Stuffing/blob/master/cobs.c
     * https://github.com/rockthethird/Consistent-Overhead-Byte-Stuffing/blob/master/Consistent%20Overhead%20Byte%20Stuffing/COBS.cs
     *

    Beispiel 1:

    - Telegramm(hexadezimal): 01 02 00 04
    - Anwenden von Präfix und Suffix: 00 01 02 00 04 00
    - Anwenden des COBS Algorithmus: 03 01 02 02 04 00


    Beispiel 2:
    - Telegramm(hexadezimal): 01 02 00 33 00 04
    - Anwenden von Präfix und Suffix: 00 01 02 00 33 00 04 00
    - Anwenden des COBS Algorithmus: 03 01 02 02 33 02 04 00
     */

    fun encode(input: ByteArray): ByteArray {
        if (input.isEmpty()) return byteArrayOf()

        val output = ByteArray(input.size + 2)
        var diffFromZero = 1
        var lastZeroIdx = 0
        var i = 0

        while (i < input.size) {
            if (input[i] == 0.toByte()) {
                // if we encounter a 0
                output[lastZeroIdx] = diffFromZero.toByte()
                // save current idx in lastZeroIdx
                lastZeroIdx = i + 1
                // reset block start
                diffFromZero = 1
            } else {
                output[i + 1] = input[i]
                diffFromZero++
            }
            i++
        }
        output[lastZeroIdx] = diffFromZero.toByte()
        return output
    }

    fun decode(input: ByteArray): ByteArray {
        if(input.size < 3) return byteArrayOf()

        val output = ByteArray(input.size - 2)

        var read_index = 0
        var write_index = 0
        val length = input.size - 1

        while (read_index < length) {
            val code = input[read_index].toInt()
            read_index++
            var c = 1
            while (c++ < code && read_index < length) {
                output[write_index++] = input[read_index++]
            }
            if(code != 0xff && read_index < length) {
                output[write_index++] = 0
            }
        }
        return output
    }

}
object DataPointType {
    const val TYPE_STRING = 0
    const val TYPE_UINT32 = 1
    const val TYPE_FLOAT = 2
}

enum class DataPoint(val idBytes: ByteArray, val type: Int, val endianness: ByteOrder? = null, val length: Int = 0) {
    SERIAL_NUMBER(byteArrayOf(0x00, 0x00, 0x00, 0x01), DataPointType.TYPE_STRING, length = 10),
    PRODUCT_TYPE(byteArrayOf(0x00, 0x00, 0x00, 0x02), DataPointType.TYPE_STRING, length = 10),
    POWER_INPUT_WATT(byteArrayOf(0x00, 0x00, 0x01, 0x01), DataPointType.TYPE_FLOAT, ByteOrder.LITTLE_ENDIAN),
    POWER_OUTPUT_WATT(byteArrayOf(0x00, 0x00, 0x01, 0x02), DataPointType.TYPE_FLOAT, ByteOrder.LITTLE_ENDIAN),
    WORK_INPUT_KILOWATTHOURS(byteArrayOf(0x00, 0x00, 0x02, 0x01), DataPointType.TYPE_UINT32, ByteOrder.BIG_ENDIAN),
    WORK_OUTPUT_KILOWATTHOURS(byteArrayOf(0x00, 0x00, 0x02, 0x02), DataPointType.TYPE_UINT32, ByteOrder.BIG_ENDIAN),
    WORKING_HOURS(byteArrayOf(0x00, 0x00, 0x04, 0x01), DataPointType.TYPE_UINT32, ByteOrder.LITTLE_ENDIAN),
}

/**
 * Uint to ByteArray conversion
 * @param uint unsigned Integer (actually Long, bc. Java does not contain unsigned primitive types) which must be in range 0 ... (2 ^ 32) - 1
 * @return 4 Byte ByteArray which represents the unsigned Integer
 */
fun uintToBytes(uint: Long, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    if(uint > 4294967295) throw IllegalArgumentException("uint value must not exceed 4294967295")// largest UInt value possible (2^32)-1 , actually has to be Long on JVM
    return ByteBuffer.allocate(8).order(endianness).putLong(uint).array().slice(if (endianness == ByteOrder.LITTLE_ENDIAN) 0..3 else 4..7).toByteArray()
}

fun bytesToUint(bytes: ByteArray, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Long {
    if (bytes.size > 8) throw IllegalArgumentException("bytes.size must not exceed 8!")
    val remainingBytes = ByteArray(8 - bytes.size)
    return ByteBuffer.wrap(if (endianness == ByteOrder.LITTLE_ENDIAN) bytes + remainingBytes else remainingBytes + bytes).order(endianness).long
}


