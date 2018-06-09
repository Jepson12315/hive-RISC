package Pipeline

import chisel3._
import chisel3.util._


object ALU
{
	val FN_X    = BitPat("b????")
	val FN_ADD  = 0.U
	val FN_SL   = 1.U
	val FN_SRA  = 2.U
	val FN_NOR  = 3.U
 	val FN_XOR  = 4.U
  	val FN_SR   = 5.U
  	val FN_OR   = 6.U
  	val FN_AND  = 7.U
  	val FN_LUI  = 8.U  // need to be added


  	val FN_SUB  = 18.U
  	val FN_SLT  = 19.U
  	val FN_SLTU = 20.U

  	def isSub(fn: UInt) = fn(4)
}

import ALU._

class ALU extends Module{
	val io = IO(new Bundle{
		val in1		= Input(UInt(32.W))
		val in2 	= Input(UInt(32.W))
		val ALU_fn 	= Input(UInt(5.W))
		val ALU_out	= Output(UInt(32.W))
		val overflow = Output(Bool())
		})



	val in2_inv = Mux(isSub(io.ALU_fn),~io.in2,io.in2)
  	//val in1_xor_in2 = io.in1(27,0) ^  io.in2(27,0) //equal 0
	  val in1_xor_in2 = io.in1 ^  io.in2
  	val in2_cmplm_2 = in2_inv + isSub(io.ALU_fn)
	// ADD, SUB
	val adder_out = io.in1 + in2_cmplm_2
	val c1 = Wire(Bool())
	val c2 = Wire(Bool())
	c1 := (~io.in1(31)) & (~in2_cmplm_2(31)) & (adder_out(31))
	c2 := (io.in1(31)) & (in2_cmplm_2(31)) & (~adder_out(31))
	io.overflow := (io.in1 === "h80000000".U) && (in2_cmplm_2(31)) || (in2_cmplm_2 === "h80000000".U) && isSub(io.ALU_fn) || (c1 | c2)
  	val lgc = Wire(UInt(1.W))
  	lgc := 0.U
  	// SLT, SLTU
  	when(io.ALU_fn === FN_SLT){
  		when(io.in1(31) === io.in2(31))
  		{
  			lgc := Mux(adder_out(31) === 1.U,1.U,0.U)
  		}.otherwise{
  				lgc := io.in1(31)
		}
  	}
  	when(io.ALU_fn === FN_SLTU){
  		when(io.in1(31) === io.in2(31)){
  			lgc := adder_out(31)
  		}.otherwise{
  			lgc := io.in2(31)
  		}
  	}

  	// SLL, SRL, SRA
  	val shamt = io.in1(4,0)
  	val shin	= io.in2
  	val shout	= Wire(UInt (32.W))
    val shr = shin >> shamt
    val shl = shin << shamt
  	when(io.ALU_fn === FN_SR){
  		shout := shr
  	}
  	when(io.ALU_fn === FN_SRA){
      	shout := (Cat(shin(31),shin).asSInt >> shamt)(31,0)
  	}
  	when(io.ALU_fn === FN_SL){
  		shout := shl
  	}

  // AND, OR, XOR
    val bit = Wire(UInt(32.W))
	bit := 0.U
  	when(io.ALU_fn === FN_AND){
  		bit := io.in1 & io.in2
  	}
  	when(io.ALU_fn === FN_OR){
  		bit := io.in1 | io.in2
  	}
  	when(io.ALU_fn === FN_XOR){
  		bit := in1_xor_in2
  	}
  	when(io.ALU_fn === FN_NOR){
    	bit := ~(io.in1 | io.in2)
  	}


	when(io.ALU_fn === FN_SL||io.ALU_fn === FN_SRA||io.ALU_fn === FN_SR){
      	io.ALU_out := shout
    }
	when(io.ALU_fn === FN_SUB || io.ALU_fn === FN_ADD)
  	{
    	io.ALU_out := adder_out
  	}
	when(io.ALU_fn === FN_AND ||io.ALU_fn === FN_OR||io.ALU_fn === FN_XOR ||io.ALU_fn === FN_NOR){
    	io.ALU_out := bit
	}
	when(io.ALU_fn === FN_LUI){
  		io.ALU_out := io.in1
	}
	when((io.ALU_fn === FN_SLT) ||( io.ALU_fn === FN_SLTU)){
		io.ALU_out := Cat(Fill(30,0.U),lgc)
	}
}
