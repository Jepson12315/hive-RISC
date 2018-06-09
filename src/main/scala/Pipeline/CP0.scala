package Pipeline

import chisel3._
import chisel3.util._

class CP0 extends Module{
val io = IO(new Bundle{
    val sel = Input(UInt(3.W))
    val CP0_read = Input(Bool())
    val CP0_write = Input(Bool())
    val CP0_datIn = Input(UInt(32.W))
    val CP0_datOut = Output(UInt(32.W))
    val CP0_addr = Input(UInt(5.W))
    val Exception = Input(Bool())
    val BD = Input(Bool())
    val ExceptionAddr = Input(UInt(32.W))
    val ExceptionType = Input(UInt(5.W))
    val Badaddr = Input(UInt(32.W))
    val SolveEntry = Output(UInt(32.W))
    val SolveBack = Input(Bool())
    val PC_back = Output(UInt(32.W))
    val EPC = Output(Bool())
    val done = Output(Bool())

    val reset = Input(Bool())
})

    val CP0Reg      = Mem(32,UInt(32.W))
    val CP0Regsel1  = Mem(32,UInt(32.W))
    val CP0Regsel2  = Mem(32,UInt(32.W))
    val CP0Regsel3  = Mem(32,UInt(32.W))
    val CP0Regsel4  = Mem(32,UInt(32.W))
    val CP0Regsel5  = Mem(32,UInt(32.W))
    val CP0Regsel6  = Mem(32,UInt(32.W))
    val CP0Regsel7  = Mem(32,UInt(32.W))

    when(!io.reset){
        CP0Reg(8) := 0.U
        CP0Reg(12) := 0.U
        CP0Reg(13) := 0.U
        CP0Reg(14) := 0.U
    }.otherwise{}

    io.EPC := false.B
    io.done := false.B
    when(io.CP0_write){
        when(io.sel===0.U){
            CP0Reg(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===1.U){
            CP0Regsel1(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===2.U){
            CP0Regsel2(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===3.U){
            CP0Regsel3(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===4.U){
            CP0Regsel4(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===5.U){
            CP0Regsel5(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===6.U){
            CP0Regsel6(io.CP0_addr):=io.CP0_datIn
        }.elsewhen(io.sel===7.U){
            CP0Regsel7(io.CP0_addr):=io.CP0_datIn
        }
    }
    when(io.CP0_read){
        io.CP0_datOut:=MuxCase(CP0Reg(io.CP0_addr),Array(
            (io.sel===1.U) -> CP0Regsel1(io.CP0_addr),
            (io.sel===2.U) -> CP0Regsel2(io.CP0_addr),
            (io.sel===3.U) -> CP0Regsel3(io.CP0_addr),
            (io.sel===4.U) -> CP0Regsel4(io.CP0_addr),
            (io.sel===5.U) -> CP0Regsel5(io.CP0_addr),
            (io.sel===6.U) -> CP0Regsel6(io.CP0_addr),
            (io.sel===7.U) -> CP0Regsel7(io.CP0_addr)
        ))
    }

    /**

    val IM0 = Status(8)
    val IM1 = Status(9)
    val IM2 = Status(10)
    val IM3 = Status(11)
    val IM4 = Status(12)
    val IM5 = Status(13)
    val IM6 = Status(14)
    val IM7 = Status(15)
    val EXL = Status(1)
    val IE  = Status(0)
    //

    val BD  = Cause(31)
    val IP0 = Cause(8)
    val IP1 = Cause(9)
    val IP2 = Cause(10)
    val IP3 = Cause(11)
    val IP4 = Cause(12)
    val IP5 = Cause(13)
    val IP6 = Cause(14)
    val IP7 = Cause(15)
    val ExcCode = Cause(6,2)
    */

    val BadVAddr   = CP0Reg(8)
    val Status     = CP0Reg(12)
    val Cause      = CP0Reg(13)
    val EPC        = CP0Reg(14)


    when(io.Exception === true.B && Status(1) === 0.U){
        CP0Reg(12.U) := Cat(Status(31,2),1.U,Status(0))
        CP0Reg(13.U) := Cat(io.BD,Cause(30,7),io.ExceptionType,Cause(1,0))
        when(io.BD === false.B){
            CP0Reg(14.U) := io.ExceptionAddr
        }.otherwise{
            CP0Reg(14.U) := io.ExceptionAddr-4.U
        }
        when(io.ExceptionType === 4.U||io.ExceptionType === 5.U){
            CP0Reg(8.U) := io.Badaddr
        }
        io.SolveEntry := "hbfc00380".U  //Lonngson
        //io.SolveEntry := "hbfc040b0".U //for single test
        io.EPC := true.B
        //solving := true.B
    }
    when(io.SolveBack === true.B){
        CP0Reg(12.U) := Cat(Status(31,2),0.U,Status(0))
        io.PC_back := CP0Reg(14.U)
        io.done := true.B
        //solving := false.B
}}
