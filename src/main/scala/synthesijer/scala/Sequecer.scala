package synthesijer.scala

import synthesijer.hdl.sequencer.SequencerState
import synthesijer.hdl.HDLSequencer

class Sequencer(val seq: HDLSequencer){
  
  var uid = 0;
  
	def tick(t: Integer) = seq.setTransitionTime(t);
	
	val idle = new State(seq.getIdleState())
	
	def add(name:String) : State = new State(seq.addSequencerState(name));
	
	def add() : State = {
	  val s = new State(seq.addSequencerState("S_" + uid))
    uid = uid + 1
    return s
	}
	 
	def * (e:ExprItem) : SeqExpr = {
    return new SeqExpr(this, e)
  }

}

class State(val state: SequencerState){
  
	def -> (s:State) : State = {
	  state.addStateTransit(s.state)
	  return s
	}
	
  def -> (t:(ExprItem, State)) : State = {
    state.addStateTransit(t._1.toHDLExpr, t._2.state)
    return t._2
  }
	
  def max_delay(v:Int):Unit = {
	  state.setMaxConstantDelay(v)
	}
  
  def * (e:ExprItem) : StateExpr = {
    return new StateExpr(this, e)
  }

  def | (s:StateExpr) : State = {
    this -> (s.expr, s.state)
    return this
  }
  
}

class StateExpr(val state:State, val expr:ExprItem){
  def -> (s:State) : State = {
    state.state.addStateTransit(expr.toHDLExpr, s.state)
    return s
  }
}

class SeqExpr(val seq:Sequencer, val expr:ExprItem){

}
