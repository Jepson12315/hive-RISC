package Pipeline

import chisel3._
import chisel3.util._

class RegFile extends Module()
{
	val io = IO(new Bundle{
		//32-bit bus
		val BusA   	= Output(UInt(32.W))
		val BusB   	= Output(UInt(32.W))
		val BusW   	= Input(UInt(32.W))
	    //register
	    val RS     	= Input(UInt(5.W))
		val RT     	= Input(UInt(5.W))
	    val RD     	= Input(UInt(5.W))
		//write enable
		val RegWr  	= Input(Bool())
		val regs3   = Output(UInt(32.W))
		val rf_19_reset = Input(Bool())

		val HI_in     	= Input(UInt(32.W))
		val LO_in     	= Input(UInt(32.W))
		val HI_out     	= Output(UInt(32.W))
		val LO_out     	= Output(UInt(32.W))
		val HI_wr		= Input(Bool())
		val LO_wr		= Input(Bool())

		val reset 		= Input(Bool())


		//val HILO_rd		= Input(Bool())

	})
	//the past Module is not right, because the contrl signal is involed

	val RegFile 	= Mem(32,UInt(32.W))

	when(!io.reset){
		RegFile(1)     := 0.U
		RegFile(2)     := 0.U
		RegFile(3)     := 0.U
		RegFile(4)     := 0.U
		RegFile(5)     := 0.U
		RegFile(6)     := 0.U
		RegFile(7)     := 0.U
		RegFile(8)     := 0.U
		RegFile(9)     := 0.U
		RegFile(10)     := 0.U
		RegFile(11)     := 0.U
		RegFile(12)     := 0.U
		RegFile(13)     := 0.U
		RegFile(14)     := 0.U
		RegFile(15)    := 0.U
		RegFile(16)     := 0.U
		RegFile(17)     := 0.U
		RegFile(18)     := 0.U
		RegFile(19)     := 0.U
		RegFile(20)     := 0.U
		RegFile(21)     := 0.U
		RegFile(22)     := 0.U
		RegFile(23)     := 0.U
		RegFile(24)     := 0.U
		RegFile(25)     := 0.U
		RegFile(26)     := 0.U
		RegFile(27)     := 0.U
		RegFile(28)     := 0.U
		RegFile(29)     := 0.U
		RegFile(30)     := 0.U
		RegFile(31)     := 0.U

	}


	RegFile(0)     := 0.U

	val HI			= RegInit(0.U(32.W))//乘法高位；除法商
	val LO		    = RegInit(0.U(32.W))//乘法低位；除法余

	when(io.LO_wr){
		LO := io.LO_in
	}
	when(io.HI_wr){
		HI := io.HI_in
	}

	io.HI_out := HI
	io.LO_out := LO

	//printf("mul : %d\n", HI)
	//printf("mul : %d\n", LO)

	//RegFile Output
	io.BusA         := RegFile( io.RS )
	io.BusB         := RegFile( io.RT )

	when (io.rf_19_reset){
		RegFile(19) := 0.U
	}

	//RegFile Input
	when (io.RegWr){
		RegFile( io.RD ) := io.BusW


	}.otherwise{}
}
