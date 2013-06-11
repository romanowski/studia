import io.Source
import util.matching.Regex.Match

/**
 * program entry point, parse code and call CompilationUnit methods
 */
object Interpreter {

	/**
	 * interpreter loop
	 * @param code
	 * @param heapSize
	 */
	def interpret(code: String, heapSize: Int) {
		val stringConstRegex = "\"[^\"]*\"" r

		// maps String to hash representation
		var stringRefs = Map[String, String]()


		//maps string to representation
		val toInterpret = stringConstRegex.replaceAllIn(code, (mt: Match) => {
			stringRefs.get(mt.group(0)).getOrElse {
				val key = "#" + stringRefs.size
				stringRefs = stringRefs + (mt.group(0).replace("\"", "") -> key)
				key
			}
		})
		//create CU - reverse map -> map representation to String
		val cu = CompilationUnit(stringRefs.map(el => el._2 -> el._1), heapSize)

		//main loop, cuts all new lines and tabs and split at ';'
		toInterpret replace("\n", "") replace("\t", "") split (";") foreach {
			line =>
			// split to token on spaces
				line.split(" +").toList match {
					case "VarDeclT" :: name :: _ => cu.putVarT(name)
					case "VarDeclS" :: name :: "NULL" :: _ => cu.putNUllVarS(name)
					case "VarDeclS" :: name :: value :: _ => cu.putVarS(name, value)
					case rName :: "=" :: "NULL" :: _ => cu.assignNull(rName)
					case rName :: "=" :: lValue :: _ => cu.assign(rName, lValue)
					case "Print" :: what :: Nil => cu.myPrint(what)
					case "HeapAnalyze" :: _ => cu.heapAnal()
					case "Collect" :: _ => cu.collect()
					case _ => print("[ERROR] at: " + line)
				}
		}
	}

	/**
	 * main method
	 * @param arg
	 */
	def main(arg: Array[String]) {

		//gets thr heap size
		val heapSize = System.getProperty("npj.heap.size") match {
			case null => arg(1).split("=")(2)
			case a => a
		}

		interpret(Source.fromFile(arg(0)).mkString, heapSize.toInt)
	}

}



