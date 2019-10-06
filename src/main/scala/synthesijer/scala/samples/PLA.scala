
import synthesijer.scala._
import synthesijer.scala.xilinx._
import synthesijer.scala.xilinx.series7._

class PLA(depth:Int, width:Int)
    extends Module("PLA", "clk", "reset"){

  private val tmpl = new AndArray(depth); tmpl.genVHDL()
  // ANDアレイのインスタンスを必要な指定した個数生成
  private val u = for(i <- 0 until width) yield
    instance(tmpl, "AndArray_" + i)

  val WE = for(i <- 0 until width) yield
    for(j <- 0 until depth) yield inP("WE_" + i + "_" + j)
  val ADDR = for(i <- 0 until width) yield
    for(j <- 0 until depth) yield
      for(k <- 0 until 4) yield inP("ADDR_" + i + "_" + j + "_" + k, 6)
  val DI = for(i <- 0 until width) yield
    for(j <- 0 until depth) yield
      for(k <- 0 until 4) yield inP("DI_" + i + "_" + j + "_" + k)

  for(i <- 0 until width){
    u(i).sysClk := sysClk
    u(i).sysReset := sysReset
    for(j <- 0 until depth){
      u(i).signalFor(tmpl.WE(j)) := WE(i)(j)
      (ADDR(i)(j) zip tmpl.ADDR(j)).foreach(n => u(i).signalFor(n._2) := n._1)
      (DI(i)(j) zip tmpl.DI(j)).foreach(n => u(i).signalFor(n._2) := n._1)
    }
  }

  private val outputs = for(i <- 0 until width) yield signal()
  for(i <- 0 until width) outputs(i) := u(i).signalFor(tmpl.Q)
  val Q = outP("Q")
  Q := outputs.reduce((a:ExprItem, b:ExprItem) => (a or b))

}

class AndArray(depth:Int)
    extends Module("AndArray", "clk", "reset"){
  Xilinx.init(this)

  // ユニットのテンプレート
  private val tmpl = new PlaElem()

  // ANDアレイに必要な要素のインスタンスを必要個数生成
  private val u = for(i <- 0 until depth) yield
    instance(tmpl, "PlaElem_" + i)

  val WE = for(i <- 0 until depth) yield inP("WE_" + i);
  val ADDR = for(i <- 0 until depth) yield
    for(j <- 0 until 4) yield inP("ADDR_" + i + "_" + j, 6)
  val DI = for(i <- 0 until depth) yield
    for(j <- 0 until 4) yield inP("DI_" + i + "_" + j)
  val Q = outP("Q");


  // PLAを構成する各ユニットの基本設定
  for(i <- 0 until depth){
    u(i).sysClk := sysClk     // クロック
    u(i).sysReset := sysReset // リセット
    u(i).signalFor(tmpl.WE) := WE(i) // 書き込みイネーブル
    // 各ユニットの各ADDRポートを外部への出力ポートに接続
    (ADDR(i) zip tmpl.ADDR).foreach(n => u(i).signalFor(n._2) := n._1)
    // 各ユニットの各DIポートを外部からの入力ポートに接続
    (DI(i) zip tmpl.DI).foreach(n => u(i).signalFor(n._2) := n._1)
  }

  // 最初段のMUXの'1'の入力は'1'に固定
  u(0).signalFor(tmpl.CYINIT) := HIGH
  // CARRY4を数珠つなぎにする
  for(i <- 1 until depth)
    u(i).signalFor(tmpl.CI) := ref(u(i-1).signalFor(tmpl.CO), 3)
  // 最終段のCARRY4の出力は外部への出力ポートに接続
  Q := ref(u(depth-1).signalFor(tmpl.CO), 3)
}


object PLA{

  def main(args:Array[String]) = {
    val m = new PLA(2, 4)
    m.genVHDL()
  }

}
