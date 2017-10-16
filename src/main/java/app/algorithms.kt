package app

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 19:33
 */
interface FramingAlgorithm {
    fun encode(input: ByteArray): ByteArray
    fun decode(input: ByteArray): ByteArray
}

class NoFramingAlgorithm : FramingAlgorithm {
    override fun encode(input: ByteArray): ByteArray = input
    override fun decode(input: ByteArray): ByteArray = input
}

class COBSAlgorithm : FramingAlgorithm {
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

    override fun encode(input: ByteArray): ByteArray {
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

    override fun decode(input: ByteArray): ByteArray {
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