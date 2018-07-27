package com.cmcc.exam.kotlin

import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.cmcc.exam.AppLogger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.experimental.buildSequence
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Created by ding on 10/01/2018.
 */
class SynTax<T> {

    private lateinit var mListener: ValueListener

    private val readOnlyValue: Int by lazy { 5 }

    private var delegateValue: Int by MyDelegate()

    private var custom: String
        get() {
            return "custom is me"
        }
        set(value) {
            this.custom = value
        }


    /**
     * Delegate
     */
    class MyDelegate {

        private var value: Int = 0

        operator fun <T> getValue(thisRef: SynTax<T>, property: KProperty<*>): Int {
            return value
        }

        operator fun <T> setValue(thisRef: SynTax<T>, property: KProperty<*>, i: Int) {
            value = i
        }
    }

    /**
     * 可观察属性
     */
    private var observerValue: String by Delegates.observable("default") {

        prop, oldValue, newValue ->

        AppLogger.i("oldValue=$oldValue, newValue=$newValue")
    }

    /**
     * 静态构造器
     */
    companion object Builder {
        fun <T> create(): SynTax<T> {

            AppLogger.i("**singleton")
            return SynTax<T>()
        }
    }

    open class A constructor(a: Int) {

        var value: Int = a

        open fun foo(i: Int = 10) {

        }

        open fun defaultFun(p1: Int = 1, p2: Int = 3) {

            AppLogger.e("p1=$p1, p2=$p2")
        }
    }

    class B constructor(b: Int) : A(b) {

        override fun foo(i: Int) {
            super.foo(i)

            defaultFun(p2 = 4)
        }
    }

    /**
     * 扩展操作符
     */
    private fun doSpread() {

        val array = arrayOf(1, "2", 3, A(2))

        val list = listOf(4, 5, *array, 6)

        for (item in list) {
//            AppLogger.e("item=$item")
        }
    }

    /**
     * in ... out 型变
     */
    private fun copy(fromArray: Array<out Any>, toArray: Array<Any>) {

        assert(fromArray.size == toArray.size)

        for (i in toArray.indices) {
            toArray[i] = fromArray[i]
        }
    }

    private infix fun A.plus(a: A): Int {

        return this.value + a.value
    }

    /**
     * Lambda 函数
     */
    private fun <P> lock(lock: Lock, a: P, b: P, body: (x: P, y: P) -> P): P {

        lock.lock()

        try {
            return body(a, b)
        } finally {
            lock.unlock()
        }
    }

    /**
     * 中缀函数
     */
    private fun infixFun() {

        val a1 = A(2)
        val a2 = A(3)

        val ret = a1.plus(a2)

        AppLogger.i("ret=$ret")
    }

    /**
     * lambda & 匿名函数
     */
    private fun anonymousFun() {

        val titleArray = arrayOf("a1", "b1", "c1", "d1", "a2", "a4", "a3")

        val newArray = titleArray.map { it + "dcc" }.filter { it.contains("a") }.sortedBy { it }.filter(fun(x: String) = x.contains("2"))

        for (title in newArray) {
            AppLogger.i("title=$title")
        }
    }

    /**
     * 型变与逆变
     */
    private fun typeFun() {

        val fromArray: Array<Int> = arrayOf(1, 2, 3)

        val toArray: Array<Any> = Array<Any>(3) { "" }

        copy(fromArray, toArray)
    }

    /**
     * 通用测试
     */
    private fun doCommon() {

        val b = B(3)

        b.foo(1)

        // -------------------------------------- Skill -------------------------------------- //
        val rem = 20.rem(3)

        AppLogger.e("rem=$rem")

        20 % ({ AppLogger.i("将看到打印") })
    }

    private operator fun Int.rem(blk: () -> Unit) {
        if (this < 100) blk()
    }

    /**
     * 带接收者的函数字面值
     */
    private fun withReceiverFunValue() {

        val sum1: Int.(other: Int) -> Int = { other -> this + other }

        val ret1 = 3.sum1(5)

        AppLogger.i("sum1=$ret1")

        // 带接收者的函数字面值
        val sum2 = fun Int.(other: Int): Int = this + other

        val ret2 = 5.sum2(8)
        AppLogger.i("sum2=$ret2")

        val represents: String.(Int) -> Boolean = { other -> this.toIntOrNull() == other }

        AppLogger.i("represents=${"123".represents(123)}")

        fun testOp(op: (String, Int) -> Boolean, a: String, b: Int, c: Boolean) = assert(op(a, b) == c)

        val opRet = testOp(represents, "111", 111, false)

        AppLogger.i("opRet=$opRet")
    }

    /**
     * Lambda处理View的事件
     */
    private fun lambdaView() {

        val view: View = View(null)
        view.setOnClickListener { v -> }

        val listView = ListView(null)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            AppLogger.i("position=$position")
            AppLogger.i("parent=$parent")

        }
    }

    /**
     * lambda函数
     */
    private fun lambdaFoo() {

        //        val sum : (x: Int, y: Int) -> Int = { x, y -> x + y }
        val sum = { x: Int, y: Int -> x + y }

        val lockValue: Int = lock(ReentrantLock(), 2, 3, sum)

        AppLogger.i("lockValue=$lockValue")

        val listener: (x: Int) -> Unit = { x -> valueChange(x) }
        addValueListener(listener)

        listener(3)
    }

    private fun valueChange(value: Int) {

        AppLogger.i("value=$value")
    }

    private fun addValueListener(l: (Int) -> Unit) {

    }

    /**
     * 数值变化的回调
     */
    interface ValueListener {
        fun onValueChanged(value: Int)
    }

    private inline fun <T> List<T>.forEach1(action: (T) -> Unit): Unit {

        for (element in this) {

            action(element)
        }
    }

    /**
     * 是否包含0
     */
    private fun hasZeros(ints: List<Int>): Boolean {

        ints.forEach1 { if (it == 0) return true else AppLogger.i("it=$it") }

        return false
    }

    /**
     * 实现接口的通用方法，建议用Lambda
     */
    private fun listenerFun() {

        val view = View(null)

        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
            }
        })

        view.setOnClickListener { v -> }
    }

    /**
     * 主函数
     */
    fun doMain() {

        AppLogger.i("doMain")

        // 通用测试
        doCommon()

        // 型变与逆变
        typeFun()

        // 扩展操作符
        doSpread()

        // 中缀函数
        infixFun()

        // lambda函数
        lambdaFoo()

        // lambda & 匿名函数
        anonymousFun()

        // 带接收者的函数字面值
        withReceiverFunValue()

        hasZeros(listOf(1, 2, 3, 0, 5))

        // 密封类 - when
        testExpr(Expr.ExprB())

        // 可观察的字段
        this.delegateValue = 3
        AppLogger.i("delegateValue = $delegateValue")

        AppLogger.i("observerValue = $observerValue")

        observerValue = "name 1"
        observerValue = "name 2"

        // 具体化类型参数
//        findParentOfType<Unit1>()

        // 协程
        coroutine()

        // 解构函数
        parseFun()

        // 测试集合类
        listFoo()

        // 类型擦除与泛型检测
        pairFoo()
    }

    /**
     * 解构函数
     */
    private fun parseFun() {

        val map: Map<String, Int> = mapOf("a" to 1, "b" to 2, "c" to 3)

        map.mapValues { entry ->
            {
                AppLogger.i("value=${entry.value}!")
                AppLogger.i("111 *")
            }
        }

        val map2 = map.mapValues { (key, value) ->
            {
                AppLogger.i("value()=$value")
                value * 2

            }
        }

        for ((key, value) in map) {
            AppLogger.i("key=$key, value=$value")
        }

        for ((key, value) in map2) {
            AppLogger.i("key=$key, value=${value()}")
        }
    }

    /**
     * 非局部返回（crossinline）
     * // TODO 需要再看看
     *
     */
    private inline fun testRun(crossinline body: () -> Unit) {

        val f = object : Runnable {
            override fun run() = body()
        }
    }

    /**
     * 具体化类型参数
     */
    private inline fun <reified X> findParentOfType(): X? {

        val p = Any()
        return p as X?
    }

    /**
     * 测试密封类
     */
    private fun testExpr(expr: Expr) {
        when (expr) {
            is Expr.ExprA -> AppLogger.i("Expr is ExprA")
            is Expr.ExprB -> AppLogger.i("Expr is ExprB")
            is Expr.ExprC -> AppLogger.i("Expr is ExprC")
        }
    }

    /**
     * 密封类
     */
    sealed class Expr {

        class ExprA : Expr()

        class ExprB : Expr()

        object ExprC : Expr()

    }

    /**
     * 协程（Kotlin中还处于测试阶段）
     */
    private val seq = buildSequence {

        AppLogger.i("build sequence start ")

        for (i in 1000..5000 step 1000) {
            yield(i)
            AppLogger.i("build sequence step ")
        }

        AppLogger.i("build sequence end ")
    }

    /**
     * 测试协程
     */
    private fun coroutine() {

        seq.take(3).forEach { AppLogger.i("it=$it") }
    }

    /**
     * 测试集合类
     */
    private fun listFoo() {

        AppLogger.i("listFoo")

        val list = listOf<Int>(1, 2, 3, 8)

        val result = list.none { it > 6 }

        AppLogger.i("result=$result")

        val ret = list.firstOrNull { it > 10 }

        // 数列
        val intP = IntProgression.fromClosedRange(0, 10, 2)

        intP.forEach { AppLogger.i("it=$it") }

        val nullableList = listOf(1, 2, 3, null, 5)

        nullableList.filterNotNull().forEach { AppLogger.i("it=$it") }

    }

    /**
     * 类型擦除与泛型检测
     */
    private inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A, B>? {

        if (first !is A || second !is B) return null
        return first as A to second as B
    }

    /**
     * 类型擦除与泛型检测
     */
    private fun pairFoo() {

        val somePair: Pair<Any?, Any?> = "item" to listOf(1, 2, 3)

        val stringToSomething = somePair.asPairOf<String, Any>()

        AppLogger.i("stringToSomething.second=${stringToSomething?.second}")

        val stringToInt = somePair.asPairOf<String, Int>()

        AppLogger.i("stringToInt.second=${stringToInt?.second}")

        val stringToList = somePair.asPairOf<String, List<*>>()

        AppLogger.i("stringToList.second=${stringToList?.second}")

        val stringToListInt = somePair.asPairOf<String, List<Int>>()

        AppLogger.i("stringToListInt.second=${stringToListInt?.second}")

        val stringToListFloat = somePair.asPairOf<String, List<Float>>()

        AppLogger.i("stringToListFloat.second=${stringToListFloat?.second}")

    }

    /**
     * val name : String? = null
     * val failedName = name ?: failed()
     */
    private fun failed(): Nothing {

        throw IllegalArgumentException("error")
    }

}



