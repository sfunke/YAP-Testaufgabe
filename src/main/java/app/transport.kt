package app

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 19:49
 */
interface Transport {
    fun open()
    fun close()
    fun read(dataPoint: DataPoint): DataResult
}

class IpTransport(val hostName: String, val port: Int = 12345, val framingAlgorithm: FramingAlgorithm) : Transport {
    lateinit var socket: Socket
    lateinit var inStream: InputStream
    lateinit var outStream: OutputStream

    override fun open() {
        socket = Socket()
        socket.connect(InetSocketAddress(hostName, port))
        socket.soTimeout = 5000

        inStream = socket.getInputStream()
        outStream = socket.getOutputStream()
    }

    override fun close() {
        socket.close()
    }

    override fun read(dataPoint: DataPoint): DataResult {
        // frame datapoint ID and send it
        val frame = framingAlgorithm.encode(dataPoint.idBytes)
        outStream.write(frame)

        val inBuffer = ByteArray(32)
        val bos = ByteArrayOutputStream()

        var bytesRead = -1
        // read into buffer and write to ByteArrayOutputStream
        while ({ bytesRead = inStream.read(inBuffer); bytesRead }() != -1) {
            bos.write(inBuffer, 0, bytesRead)
        }

        val decoded = framingAlgorithm.decode(bos.toByteArray())
        return DataResult(dataPoint, ByteBuffer.wrap(decoded))
    }

}

class DummyTransport : Transport {
    override fun open() {
    }

    override fun close() {
    }

    override fun read(dataPoint: DataPoint): DataResult {
        return when (dataPoint) {
            DataPoint.SERIAL_NUMBER -> DataResult(dataPoint, ByteBuffer.wrap("AFG4387X01".toByteArray(Charsets.UTF_8)))
            DataPoint.WORK_INPUT_KILOWATTHOURS -> DataResult(dataPoint, ByteBuffer.wrap(uintToBytes(23, ByteOrder.BIG_ENDIAN)))
            DataPoint.POWER_OUTPUT_WATT -> DataResult(dataPoint, ByteBuffer.wrap(floatToBytes(23456.54321f, ByteOrder.LITTLE_ENDIAN)))
            else -> throw NotImplementedError("Not implemented")
        }
    }
}
