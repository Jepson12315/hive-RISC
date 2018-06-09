package Pipeline

import chisel3._
import chisel3.util._


class WritebackIo extends Bundle()
{
    val WbEn        = Input(Bool())
    val EX_out      = Input(UInt(32.W))
    val dat_to_reg  = Input(UInt(32.W))
	val EX_HI       = Input(UInt(32.W))
    val EX_LO       = Input(UInt(32.W))
    val wxd   		= Input(UInt(3.W))
	val Regdst		= Input(Bool())


	val rf_wr       = Output(Bool())
    val rf_busw     = Output(UInt(32.W))
    val rf_addr     = Output(UInt(5.W))
	val rf_rd_in    = Input(UInt(5.W))
	val rf_rt_in	= Input(UInt(5.W))
	val pcReg 		= Input(UInt(32.W))
	val pcRegValid  = Input(Bool())
	val jalr 		= Input(Bool())
}


class Writeback extends Module {
    val io		=IO(new WritebackIo())

	when(io.Regdst){
		io.rf_addr := io.rf_rd_in
	}.otherwise{
		when(io.pcRegValid === true.B){
            io.rf_addr  := 31.U
        }.otherwise{
            io.rf_addr := io.rf_rt_in
        }
	}


    when( io.WbEn ){
    	io.rf_wr 		:= true.B
    	when(!io.pcRegValid)
    	{
    		when(io.Regdst)
    			{
					io.rf_addr := io.rf_rd_in
				}.otherwise{
					io.rf_addr := io.rf_rt_in
				}

    		when(io.wxd === 0.U){
			//Ex
    			when(io.jalr){
    				io.rf_busw	:= io.pcReg + 8.U
    			}.otherwise{
    				io.rf_busw 	:= io.EX_out
    			}
    		}.elsewhen(io.wxd === 1.U){
			//Mem
    			io.rf_busw 	:= io.dat_to_reg
    		}.elsewhen(io.wxd === 2.U){
    			io.rf_busw 	:= io.dat_to_reg
    		}.elsewhen(io.wxd === 3.U){
    			io.rf_busw 	:= io.dat_to_reg
    		}.elsewhen(io.wxd === 4.U){
                io.rf_busw  := io.dat_to_reg
            }
		}.otherwise{
    		io.rf_busw 	:= io.pcReg + 8.U
    		io.rf_addr  := 31.U
    	}
    }.otherwise{
		io.rf_wr 		:= false.B
		io.rf_busw 		:= 0.U
	}


}
