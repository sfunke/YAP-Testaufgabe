
import app.COBSAlgorithm
import app.IpTransport
import app.YAPReader
import java.net.ConnectException

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 18:37
 */

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Please provide Hostname!")
        return
    }

    val yapReader = YAPReader(transport = IpTransport(hostName = args[0], framingAlgorithm = COBSAlgorithm()))
    try {
        yapReader.start()
    } catch (e:ConnectException) {
        System.err.println("No connection possible, is Server running?")
    }
}