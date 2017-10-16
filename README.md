# Testaufgabe
## Preconfigurations
### Demo-Server*
There has to be a server running on Port `12345` to simulate the YAP-Device.
There is a Demo-Server within the Directory `server`, 
which can be started via Kotlin-Script like this (assuming you are inside the `server`-directory):

`kotlinc -script server.kts`

(To install Kotlin-Script for example via homebrew, type `brew install kotlin`)

***If you already have a Server running, you can omit this step.**

## Running the App
To build the project via Gradle, switch to the project directory and enter following command:

`gradlew clean build`

After the Client project has been built, and assuming the Server is running under `127.0.0.1` on Port`12345`, you can execute the resulting `jar` as following:
 
 `java -jar build/libs/testaufgabe-1.0.jar 127.0.0.1`
 
 This should output the following:
 
 ```
 DataPoint: SERIAL_NUMBER => Result<String>: "AFG4387X01"
 DataPoint: PRODUCT_TYPE => Result<String>: "YAP-Reader"
 DataPoint: POWER_INPUT_WATT => Result<Float>: 1234.5
 DataPoint: POWER_OUTPUT_WATT => Result<Float>: 23456.543
 DataPoint: WORK_INPUT_KILOWATTHOURS => Result<UInt32>: 23
 DataPoint: WORK_OUTPUT_KILOWATTHOURS => Result<UInt32>: 42
 DataPoint: WORKING_HOURS => Result<UInt32>: 4294967295
 ```
 ## Source Code
 The Client Project is written using Kotlin 1.1, the Build-System is Gradle.
 
 It can be imported into Intellij IDEA without problems.

 ```
 + build # Output Folder
 + server # Server Folder, see above
 + src
 	+ main
 		+ java # Sources root
 			main.kt # Entry function
 			+ app
 				algorithms.kt # contains Framing Algorithms, abstracted into interface and COBS-Implementation
 				byteutils.kt # Helper functinos for ByteArray, UInt and Float Operations
 				models.kt # Data Model classes
 				transport.kt # contains transport algorithms, potentially extendable with more implementations
 				YAPReader.kt # Main Reader App class
 	+ test
 		+ java # Tests root
 				
 ```
 *Convention: Kotlin files which contain multiple classes, Top-Level functions, or Extensions have a **`lowercase_filename.kt`.**
 Otherwise, if there is a single Type per File, the filename begins with an Uppercase Letter, just like in Java (e.g. `MyClass.kt`).* 
 

 ## Running the Tests
 To run the tests via Gradle, switch to the project directory and enter following command:
                            
`gradlew test`

You can then visit the generated Test-Report-HTML under `build/reports/tests/test/index.html`