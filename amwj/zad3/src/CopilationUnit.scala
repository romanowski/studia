/**
 * Pointer types
 */
object Types extends Enumeration {
	val S = Value(-1, "Type S")
	val T = Value(-2, "Type T")
	val N = Value(-3, "NULL type")
	//for null
	val G = Value(-5, "GOD pointer")
	// for global object for easier manipulation
	val I = Value(-4, "Int type - only mock:)") //autoboxing :) -> for easier manipulation
}

import Types._

/**
 * class representing environment of our NPJVM ;)
 * manage heap and variables and does GC
 * @param strings string maps
 * @param heapSize size of heap
 */
case class CompilationUnit(strings: Map[String, String], heapSize: Int = 500) {
	val heap: Array[Int] = new Array(heapSize)

	//standard pointer - changes in ptr field change only ptr field
	case class PTR(t: Types.Value, private val _ptr: Int) {
		private var ptr_inner = _ptr

		//setter for ptr
		def ptr_=(int: Int) {
			ptr_inner = int
		}

		//getter for ptr
		def ptr = ptr_inner
	}

	//pointer for object fields -> gets value from heap and sets value on heap
	case class MemPTR(_t: Types.Value, __ptr: Int) extends PTR(_t, __ptr) {

		//setter for ptr
		override def ptr_=(int: Int) = {
			heap(__ptr) = int
		}

		//getter for ptr
		override def ptr = heap(__ptr)

	}

	val NULL = PTR(N, 0)
	val GOD_PTR = PTR(G, -1)

	//T heap constants
	val TSize = 4
	val F1Offset = 1
	val F2Offset = 2
	val DataOffset = 3

	//S heap constants
	val SHeaderLength = 2
	val textOffset = 2
	val lengthOffset = 1

	//local variables
	var vars = Map[String, PTR]()

	//heap state
	var heapStart = 1
	//next free space on heap
	var freePtrSize = 1
	//where heep ends
	var maxPtrSize = heapSize / 2

	//counts for files
	var analCount = 0
	var collectCount = 0

	/**
	 * inner function for allocate memory on heap
	 * @param size size od memory block
	 * @return memory start
	 */
	def allocateInner(size: Int): Int = {
		if (freePtrSize + size > maxPtrSize) {
			collect()
			if (freePtrSize + size > maxPtrSize) {
				throw new RuntimeException("no memory exeption!")
			}
		}
		val out = freePtrSize
		freePtrSize = freePtrSize + size
		out
	}

	/**
	 * allocate T object on heap
	 * @param f1
	 * @param f2
	 * @param data
	 * @return PTR to new object
	 */
	def allocateT(f1: Int = 0, f2: Int = 0, data: Int = 0): PTR = {
		val out = PTR(T, allocateInner(TSize))
		heap(out.ptr + F1Offset) = f1
		heap(out.ptr + F2Offset) = f2
		heap(out.ptr + DataOffset) = data
		heap(out.ptr) = T.id
		out
	}

	/**
	 * allocate S object on heap and copy s value to heap
	 * @param value content of S object
	 * @return
	 */
	def allocateS(value: String): PTR = {
		val out = PTR(S, allocateInner(SHeaderLength + value.size))
		heap(out.ptr) = S.id
		heap(out.ptr + lengthOffset) = value.size
		(0 to value.size - 1) foreach {
			i =>
				heap(out.ptr + textOffset + i) = value(i).toInt
		}
		out
	}

	//create new var T
	def putVarT(name: String) {
		vars = vars + (name -> allocateT())
	}

	//create new var S that points to null
	def putNUllVarS(name: String) {
		vars = vars + (name -> NULL)
	}

	//create new var S that points to value
	def putVarS(name: String, value: String) {
		vars = vars + (name -> allocateS(strings(value)))
	}

	/**
	 * gets PTR to val with given name
	 * @param name   name of var (eg. "ala" or "ala.f1.f2.data")
	 * @param ptr ptr to previous PTR for local variable GOD_PTR
	 * @return  requested PTR
	 */
	private def getVarInner(name: List[String], ptr: PTR): PTR = {
		ptr.t match {
			case G => getVarInner(name.tail, vars(name.head))
			case S => ptr
			case _ => name match {
				case Nil => ptr
				case _ if ptr.ptr == 0 => throw new NullPointerException()
				case "f1" :: tail => getVarInner(tail, MemPTR(T, ptr.ptr + F1Offset))
				case "f2" :: tail => getVarInner(tail, MemPTR(T, ptr.ptr + F2Offset))
				case "data" :: _ => MemPTR(I, ptr.ptr + DataOffset)
				case head :: _ => throw new RuntimeException("no such field: " + head)
			}
		}
	}

	/**
	 * wrapper for {getVarInner}
	 * @param name
	 * @return
	 */
	def getVar(name: String): PTR = getVarInner(name.split("\\.").toList, GOD_PTR)

	/**
	 * assign to name var value (constant or variable)
	 * @param name
	 * @param value
	 */
	def assign(name: String, value: String) {
		val field = getVar(name)
		val obj = value match {
			case str if str startsWith ("#") => allocateS(strings(str))
			case nr if nr.forall(_.isDigit) => PTR(I, nr.toInt)
			case field => getVar(field)
		}
		field.ptr = obj.ptr
	}

	/**
	 * sets Null on variable
	 * @param name
	 */
	def assignNull(name: String) {
		getVar(name).ptr = 0
	}

	/**
	 * get string value for variable
	 * for S it is given string for T it is value of data field
	 * @param v
	 * @return
	 */
	def getVal(v: PTR): String = {
		v.t match {
			case S =>
				heap.slice(v.ptr + textOffset, v.ptr + textOffset + heap(v.ptr + lengthOffset)).map(_.toChar).mkString
			case T =>
				heap(v.ptr + DataOffset).toString
			case any => any.toString
		}
	}

	/**
	 * gets values form T PTR
	 * @param v
	 * @return (f1, f2, data)
	 */
	def getFields(v: PTR): (Int, Int, Int) = {
		assert(v.t == T)
		(heap(v.ptr + F1Offset), heap(v.ptr + F2Offset), heap(v.ptr + DataOffset))
	}

	// method for printing result - escapes strings and gets values form vars
	def myPrint(what: String) {
		println(what match {
			case str if str.startsWith("") => strings(what)
			case number if number.forall(_.isDigit) => number
			case v => getVal(getVar(v))
		})
	}

	/**
	 * perform GC
	 */
	def collect() {
		collectCount += 1
		Util.writeInFile("collect.txt", collectCount + "")

		//maps old ptr to new value
		var refMap: Map[Int, Int] = Map((0, 0))

		//switching siedes of heap
		if (maxPtrSize == heapSize - 1) {
			heapStart = 1
			maxPtrSize = heapSize / 2 + 1
			freePtrSize = heapStart
		} else {
			heapStart = heapSize / 2 + 1
			maxPtrSize = heapSize - 1
			freePtrSize = heapStart
		}


		/**
		 * method that copy pointers form one side to another
		 * if obj was copied returns new ptr
		 * copy all fields ref in object
		 * @param ptr
		 * @return new ptr to object (int)
		 */
		def cpyRef(ptr: PTR): Int = {
			if (ptr == 0)
				0
			else {
				refMap.get(ptr.ptr).getOrElse {
					val out: Int = (ptr.t match {
						case S => allocateS(getVal(ptr)).ptr
						case T =>
							allocateT().ptr
						case _ => 0
					})
					refMap = refMap + (ptr.ptr -> out)
					if (ptr.t == T) {
						val v = getFields(ptr)
						heap(out + F1Offset) = cpyRef(PTR(T, v._1))
						heap(out + F2Offset) = cpyRef(PTR(T, v._2))
						heap(out + DataOffset) = v._3
					}
					out
				}
			}
		}

		vars.foreach {
			el => el._2.ptr = cpyRef(el._2)
		}
	}

	/**
	 * heap anal;)
	 */
	def heapAnal() {
		analCount += 1

		//for debugging - should be removed but stays here in case:)

		/*    println("Heap ANAL from %s to %s".format(heapStart, freePtrSize))
println(heap.slice(heapStart, freePtrSize).map(i => {
	if (i < 0) {
		" #" + i
	} else " " + i
}).mkString)*/

		Util.writeInFile("heapAnalyze_%d.txt".format(analCount),
			"typeTVariables:\n%s\ntypeSVariables:\n%s".format(
				vars.filter(el => el._2.t == T).map(el => getVal(el._2)).mkString("\n"),
				vars.filter(el => el._2.t == S).map(el => getVal(el._2)).mkString("\n"))
		)

	}
}
